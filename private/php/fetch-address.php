<?php

    include_once "conn.php";
    include_once "fetch-coordinates.php";
    include_once "postal-code-coordinates-crud.php";

    if (isset($conn)) {
        $postalCode = getData('postal_code');
        $latitude = getData('latitude');
        $longitude = getData('longitude');
        $resultObj = "";

        if (!empty($postalCode)) {
            $postalCodeInDB = getCoordinatesByPostalCode($postalCode);
            $dbChecker = false;
            // Check if postal code is in the database
            if (!empty($postalCodeInDB)) {
                $dbChecker = true;
                if($postalCodeInDB['isfound'] == 1) {
                    $resultObj = json_encode(array(
                        "success" => "Postal code converted to coordinates",
                        "postal_code" => $postalCodeInDB['postal_code'],
                        "latitude" => $postalCodeInDB['latitude'],
                        "longitude" => $postalCodeInDB['longitude'],
                        "displayName" => $postalCodeInDB['display_name'] ?? "N/A"
                    ));
                } else {
                    echo json_encode(array("error" => "Postal code not found."));
                }                
            }

            // If the postal code is not in the database then we will fetch its coordinates from the API
            if(!$dbChecker) {
                $result = fetchCoordinatesFromPostalCode($postalCode);

                if (isset($result['error'])) {
                    insertIntoPostalCodeCoordinatesNotFound($postalCode);  
                    echo json_encode(array("error" => $result['error']));
                } else {   
                    $resultObj = json_encode(array(
                        "success" => "Postal code converted to coordinates",
                        "postal_code" => $result['postal_code'],
                        "latitude" => $result['latitude'],
                        "longitude" => $result['longitude'],
                        "displayName" => $result['display_name']
                    ));
    
                    insertIntoPostalCodeCoordinatesFound(json_decode($resultObj, true));   
                }
            }            
        } elseif (!empty($latitude) && !empty($longitude)) {
            $latitude = floatval($latitude);
            $longitude = floatval($longitude);

            echo json_encode(array(
                "success" => "Coordinates provided",
                "latitude" => $latitude,
                "longitude" => $longitude
            ));
        } else {
            echo json_encode(array("error" => "No valid input provided (postal code or coordinates)"));
        }

         
        echo $resultObj;

    } else {
        echo json_encode(array("error" => "no connection to database from fetch-address.php"));
    }
?>