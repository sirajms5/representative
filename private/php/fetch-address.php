<?php

    include_once "conn.php";
    include_once "fetch-coordinates.php";
    include_once "postal-code-coordinates-crud.php";
    include_once "representative-crud.php";

    try {
        $postalCode = getData('postal_code');
        $resultObj = "";
        $isCoordsFound = false;

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
                    $isCoordsFound = true;
                } else {
                    error_log("Error in fetch-address.php: Postal code ". $postalCode . "not found.\n", 3, "./logs/errors-log.log");
                    echo json_encode(array("error" => "Postal code not found."));
                }                
            }

            // If the postal code is not in the database then we will fetch its coordinates from the API
            if(!$dbChecker) {
                $result = fetchCoordinatesFromPostalCode($postalCode);

                if (isset($result['error'])) {
                    insertIntoPostalCodeCoordinatesNotFound($postalCode);  
                    error_log("Error in fetch-address.php: " . $result['error'] . "\n", 3, "./logs/errors-log.log");
                    echo json_encode(array("error" => $result['error']));
                } else {   
                    $resultObj = array(
                        "success" => "Postal code converted to coordinates",
                        "postal_code" => $result['postal_code'],
                        "latitude" => $result['latitude'],
                        "longitude" => $result['longitude'],
                        "displayName" => $result['display_name']
                    );
    
                    $isCoordsFound = true;
                    insertIntoPostalCodeCoordinatesFound($resultObj);   
                }
            }   
            
            if($isCoordsFound) {
                $representativesByCoordinates = getRepresentativesByCoordinates($resultObj['latitude'], $resultObj['longitude']);
         
                echo json_encode($representativesByCoordinates, true);
            }
            
        } else {
            error_log("Error in fetch-address.php: No valid input for the provided postal code: " . $postalCode . "\n", 3, "./logs/errors-log.log");
            echo json_encode(array("error" => "No valid input provided (postal code or coordinates)"));
        }

    } catch (Exception $exception) {
        error_log("Error in fetch-address.php: " . $exception->getMessage() . "\n", 3, "./logs/errors-log.log");
    }
?>