<?php

    include_once "conn.php";

    function insertIntoPostalCodeCoordinatesFound($postalCodeArray) {
        global $conn;
        try {
            $postalCode = $postalCodeArray['postal_code'];
            $latitude = $postalCodeArray['latitude'];
            $longitude = $postalCodeArray['longitude'];
            $displayName = $postalCodeArray['displayName'];
            $isFound = 1;

            $postalCodeInsertQuery = "INSERT IGNORE INTO postal_code_coordinates (postal_code, latitude, longitude, is_found, display_name) VALUES (?, ?, ?, ?, ?);";
            $insertQuery = $conn->prepare($postalCodeInsertQuery);
            $insertQuery->bind_param("sddds", $postalCode, $latitude, $longitude, $isFound, $displayName);
            $insertQuery->execute();
        } catch (Exception $exception) {
            error_log("Error in postal-code-coordinates-crud.php: " . $exception->getMessage(), 3, "./logs/errors-log.log");
        }
    }

    function insertIntoPostalCodeCoordinatesNotFound($postalCode) {
        global $conn;
        try {
            $postalCodeInsertQuery = "INSERT IGNORE INTO postal_code_coordinates (postal_code) VALUES (?);";
            $insertQuery = $conn->prepare($postalCodeInsertQuery);
            $insertQuery->bind_param("s", $postalCode);
            $insertQuery->execute();
        } catch (Exception $exception) {
            error_log("Error in postal-code-coordinates-crud.php: " . $exception->getMessage(), 3, "./logs/errors-log.log");
        }
    }

    function getCoordinatesByPostalCode($postalCode) {
        global $conn;
        try {
            $getPostalCodeQuery = "SELECT postal_code, latitude, longitude, display_name, is_found FROM postal_code_coordinates WHERE postal_code = ? LIMIT 1;";
            $postalCodeInDBQuery = $conn->prepare($getPostalCodeQuery);
            $postalCodeInDBQuery->bind_param("s", $postalCode);
            $postalCodeInDBQuery->execute();
            // Bind the result to variables
            $dbPostalCode = "";
            $latitude = "";
            $longitude = "";
            $displayName = "";
            $isFound = "";
            $postalCodeInDBQuery->bind_result($dbPostalCode, $latitude, $longitude, $displayName, $isFound);

            // Fetch the result into variables
            if ($postalCodeInDBQuery->fetch()) {
                // Return as an associative array
                return array(
                    "postal_code" => $dbPostalCode,
                    "latitude" => $latitude,
                    "longitude" => $longitude,
                    "display_name" => $displayName,
                    "isfound" => $isFound
                );
            } else {
                return null; // No results found
            }
        } catch (Exception $exception) {
            error_log("Error in postal-code-coordinates-crud.php: " . $exception->getMessage(), 3, "./logs/errors-log.log");
            return null;
        }
    }
?>