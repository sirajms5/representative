<?php

    include_once "conn.php";

    function fetchCoordinatesFromPostalCode($postalCode) {
        global $conn;
        if (isset($conn)) {
            
            // Check global rate limit
            if (!canMakeGlobalRequest()) {
                return array("error" => "Global rate limit exceeded. Please wait before making another request.");
            }

            // Check user-specific rate limit
            if (!canMakeUserRequest()) {
                return array("error" => "User-specific rate limit exceeded. Please wait before making another request.");
            }

            $url = "https://nominatim.openstreetmap.org/search?postalcode=" . urlencode($postalCode) . "&format=json&limit=1";

            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_TIMEOUT, 10); // Timeout after 10 seconds
            curl_setopt($ch, CURLOPT_HTTPHEADER, [
                "User-Agent: representatives/1.0 (sirajmsaleem@gmail.com)"
            ]);

            $response = curl_exec($ch);

            if ($response === false) {
                return array("error" => "Failed to fetch data from Nominatim API. cURL Error: " . curl_error($ch));
            }

            $responseCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
            if ($responseCode !== 200) {
                curl_close($ch);
                return array("error" => "Nominatim API returned HTTP status $responseCode");
            }
            curl_close($ch);

            error_log("Error in conn.php: " . $response . "\n", 3, "./logs/errors-log.log");
            $data = json_decode($response, true);

            if (!empty($data) && isset($data[0]['lat'], $data[0]['lon'], $data[0]['name'], $data[0]['display_name'])) {
                return array(
                    "latitude" => $data[0]['lat'],
                    "longitude" => $data[0]['lon'],
                    "postal_code" => $data[0]['name'],
                    "display_name" => $data[0]['display_name']
                );
            } else {
                return array("error" => "No results found for the provided postal code.");
            }
        } else {
            echo json_encode(array("error" => "no connection to database from fetch-coordinates.php"));
        }
    }

    function canMakeGlobalRequest() {
        global $conn;
    
        if (isset($conn)) {
            $lastRequestTime = null;
            // Get the timestamp of the last request
            $query = "SELECT request_time FROM nominatim_requests ORDER BY request_time DESC LIMIT 1";
            $stmt = $conn->prepare($query);
            $stmt->execute();
            $stmt->bind_result($lastRequestTime);
            $stmt->fetch();
            $stmt->close();
        
            $currentTime = time();
        
            // Check if the last request was made less than 1 second ago
            if ($lastRequestTime && ($currentTime - $lastRequestTime < 1)) {
                return false; // Global rate limit exceeded
            }
        
            // Insert the current request time into the database
            $insertQuery = "INSERT INTO nominatim_requests (request_time) VALUES (?)";
            $insertStmt = $conn->prepare($insertQuery);
            $insertStmt->bind_param("i", $currentTime);
            $insertStmt->execute();

            $insertStmt->close();
        
            return true; // Global request allowed
        } else {
            return false;
        }
    }  

    function canMakeUserRequest() {
        $currentTime = time();
    
        // Check if the user has a session variable for their last request time
        if (isset($_SESSION['last_request_time']) && ($currentTime - $_SESSION['last_request_time'] < 1)) {
            return false; // User-specific rate limit exceeded
        }
    
        // Update the user's session with the current time
        $_SESSION['last_request_time'] = $currentTime;
        return true; // User-specific request allowed
    }

?>
