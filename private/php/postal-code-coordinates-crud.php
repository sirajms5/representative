<?php

    include_once "conn.php";

    function insertIntoPostalCodeCoordinatesFound($postalCodeArray) {
        global $conn;
        if (isset($conn)) {
            $postalCode = $postalCodeArray['postal_code'];
            $latitude = $postalCodeArray['latitude'];
            $longitude = $postalCodeArray['longitude'];
            $displayName = $postalCodeArray['displayName'];
            $isFound = 1;

            $postalCodeInsertQuery = "INSERT IGNORE INTO postal_code_coordinates (postal_code, latitude, longitude, isfound, display_name) VALUES (?, ?, ?, ?, ?);";
            $insertQuery = $conn->prepare($postalCodeInsertQuery);
            $insertQuery->bind_param("sddds", $postalCode, $latitude, $longitude, $isFound, $displayName);
            $insertQuery->execute();
        } else {
            echo json_encode(array("error" => "no connection to database from postal-code-coordinates-crud.php"));
        }
    }

    function insertIntoPostalCodeCoordinatesNotFound($postalCode) {
        global $conn;
        if (isset($conn)) {
            $postalCodeInsertQuery = "INSERT IGNORE INTO postal_code_coordinates (postal_code) VALUES (?);";
            $insertQuery = $conn->prepare($postalCodeInsertQuery);
            $insertQuery->bind_param("s", $postalCode);
            $insertQuery->execute();
        } else {
            echo json_encode(array("error" => "no connection to database from postal-code-coordinates-crud.php"));
        }
    }

    function getCoordinatesByPostalCode($postalCode) {
        global $conn;
        $getPostalCodeQuery = "SELECT postal_code, latitude, longitude, display_name, isfound FROM postal_code_coordinates WHERE postal_code = ? LIMIT 1;";
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
    }
?>