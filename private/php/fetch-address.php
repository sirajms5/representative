<?php

    include_once "conn.php";
    include_once "fetch-coordinates.php";
    include_once "postal-code-coordinates-crud.php";

    if (isset($conn)) {
        $postalCode = getData('postal_code');
        $latitude = getData('latitude');
        $longitude = getData('longitude');

        if (!empty($postalCode)) {
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
                echo $resultObj;
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

    } else {
        echo json_encode(array("error" => "no connection to database from fetch-address.php"));
    }
?>