<?php

    require_once '../../vendor/autoload.php';    
    include_once "conn.php";
    
    use Dotenv\Dotenv;
    $dotenv = Dotenv::createImmutable(dirname(__DIR__, 2));

    $dotenv->load();

    function fetchCoordinatesFromPostalCode($postalCode) {
        global $conn;
        try {
            
            // Check global rate limit
            if (!canMakeGlobalRequest()) {
                return array("error" => "Global rate limit exceeded. Please wait before making another request.");
            }

            // Check user-specific rate limit
            if (!canMakeUserRequest()) {
                return array("error" => "User-specific rate limit exceeded. Please wait before making another request.");
            }

            // Nominatim API
            $appName = $_ENV['APPLICATION_NAME'];
            $appEmail = $_ENV['APPLICATION_EMAIL'];

            $nominatimUrl = "https://nominatim.openstreetmap.org/search?postalcode=" . urlencode($postalCode) . "&format=json&limit=1";

            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $nominatimUrl);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_TIMEOUT, 10); // Timeout after 10 seconds
            curl_setopt($ch, CURLOPT_HTTPHEADER, [
                "User-Agent: {$appName}/1.0 ({$appEmail}"
            ]);

            $nominatimResponse = curl_exec($ch);
            if ($nominatimResponse === false) {
                error_log("Nominatim API cURL Error: " . curl_error($ch) . "\n", 3, "./logs/errors-log.log");
            }
    
            $responseCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
            curl_close($ch);
            if ($responseCode === 200) {
                $nominatimData = json_decode($nominatimResponse, true);    
                if (!empty($nominatimData) && isset($nominatimData[0]['lat'], $nominatimData[0]['lon'])) {
                    return array(
                        "latitude" => $nominatimData[0]['lat'],
                        "longitude" => $nominatimData[0]['lon'],
                        "postal_code" => $nominatimData[0]['name'],
                        "display_name" => $nominatimData[0]['display_name']
                    );
                }
            }


            // Opencage API as a fallback

            $apiKey = $_ENV['OPENCAGE_API_KEY'];
            $url = "https://api.opencagedata.com/geocode/v1/json?q=" . urlencode($postalCode) . "&countrycode=ca&key=$apiKey";

            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_TIMEOUT, 10); // Timeout after 10 seconds

            $openCageResponse  = curl_exec($ch);

            if ($openCageResponse  === false) {
                error_log("Failed to fetch data from OpenCage API. cURL Error: " . curl_error($ch) . "\n", 3, "./logs/errors-log.log");

                return array("error" => "Failed to fetch data from OpenCage API. cURL Error: " . curl_error($ch));
            }

            $responseCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
            curl_close($ch);

            if ($responseCode === 200) {
                $openCageData  = json_decode($openCageResponse , true);
                if (!empty($openCageData['results']) && isset($openCageData['results'][0]['geometry']['lat'], $openCageData['results'][0]['geometry']['lng'])) {
                    return array(
                        "latitude" => $openCageData ['results'][0]['geometry']['lat'],
                        "longitude" => $openCageData ['results'][0]['geometry']['lng'],
                        "postal_code" => $openCageData ['results'][0]["components"]["postcode"],
                        "display_name" => $openCageData ['results'][0]['formatted']
                    );
                } 
            }

            return array("error" => "No results found for the provided postal code.");  

        } catch (Exception $exception) {
            error_log("Error in fetch-coordinates.php: " . $exception->getMessage() . "\n", 3, "./logs/errors-log.log");
        }
    }

    function canMakeGlobalRequest() {
        global $conn;
    
        try {
            $lastRequestTime = null;
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
        
            $insertQuery = "INSERT INTO nominatim_requests (request_time) VALUES (?)";
            $insertStmt = $conn->prepare($insertQuery);
            $insertStmt->bind_param("i", $currentTime);
            $insertStmt->execute();

            $insertStmt->close();
        
            return true; // Global request allowed
        } catch (Exception $exception) {
            error_log("Error in fetch-coordinates.php: " . $exception->getMessage() . "\n", 3, "./logs/errors-log.log");
        }
    }  

    function canMakeUserRequest() {
        $currentTime = time();
    
        if (isset($_SESSION['last_request_time']) && ($currentTime - $_SESSION['last_request_time'] < 1)) {
            return false; // User-specific rate limit exceeded
        }
    
        $_SESSION['last_request_time'] = $currentTime;
        return true; // User-specific request allowed
    }

?>
