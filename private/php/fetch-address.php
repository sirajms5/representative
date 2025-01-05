<?php

    include_once "conn.php";
    include_once "fetch-coordinates.php";
    include_once "postal-code-coordinates-crud.php";
    include_once "representative-crud.php";

    try {
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
                if($postalCodeInDB['is_found'] == 1) {
                    $resultObj = array(
                        "success" => "Postal code converted to coordinates",
                        "postal_code" => $postalCodeInDB['postal_code'],
                        "latitude" => $postalCodeInDB['latitude'],
                        "longitude" => $postalCodeInDB['longitude'],
                        "displayName" => $postalCodeInDB['display_name'] ?? "N/A"
                    );
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
                    $resultObj = array(
                        "success" => "Postal code converted to coordinates",
                        "postal_code" => $result['postal_code'],
                        "latitude" => $result['latitude'],
                        "longitude" => $result['longitude'],
                        "displayName" => $result['display_name']
                    );
    
                    insertIntoPostalCodeCoordinatesFound($resultObj);   
                }
            }            
        } elseif (!empty($latitude) && !empty($longitude)) { // user provided coordinates
            $latitude = floatval($latitude);
            $longitude = floatval($longitude);

            $resultObj = array(
                "success" => "Coordinates provided",
                "latitude" => $latitude,
                "longitude" => $longitude
            );
        } else {
            echo json_encode(array("error" => "No valid input provided (postal code or coordinates)"));
        }

        $representativesByCoordinates = getRepresentativesByCoordinates($resultObj['latitude'], $resultObj['longitude']);
         
        echo json_encode($representativesByCoordinates, true);

    } catch (Exception $exception) {
        error_log("Error in fetch-address.php: " . $exception->getMessage(), 3, "./logs/errors-log.log");
    }
?>