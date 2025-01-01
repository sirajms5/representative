<?php

    include_once "conn.php";

    function insertIntoPostalCodeCoordinatesFound($postalCodeArray) {
        global $conn;
        if (isset($conn)) {
            $postalCode = $postalCodeArray['postal_code'];
            $latitude = $postalCodeArray['latitude'];
            $longitude = $postalCodeArray['longitude'];
            $isFound = 1;

            $insertQuery = $conn->prepare("INSERT IGNORE INTO postal_code_coordinates (postal_code, latitude, longitude, isfound) VALUES (?, ?, ?, ?)");
            $insertQuery->bind_param("sddd", $postalCode, $latitude, $longitude, $isFound);
            $insertQuery->execute();
        } else {
            echo json_encode(array("error" => "no connection to database from postal-code-coordinates-crud.php"));
        }
    }

    function insertIntoPostalCodeCoordinatesNotFound($postalCode) {
        global $conn;
        if (isset($conn)) {
            $insertQuery = $conn->prepare("INSERT IGNORE INTO postal_code_coordinates (postal_code) VALUES (?)");
            $insertQuery->bind_param("s", $postalCode);
            $insertQuery->execute();
        } else {
            echo json_encode(array("error" => "no connection to database from postal-code-coordinates-crud.php"));
        }
    }
?>