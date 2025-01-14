<?php

    require_once '../../vendor/autoload.php';    
    include_once "conn.php";
    
    use Dotenv\Dotenv;
    $dotenv = Dotenv::createImmutable(dirname(__DIR__, 2));

    $dotenv->load();

    function fetchCoordinatesFromPostalCode($postalCode) {
        global $conn;
        try {

            $sessionId = session_id();
            $insertQuery = "INSERT INTO request_queue (session_id, postal_code) VALUES (?, ?)";
            $stmt = $conn->prepare($insertQuery);
            $stmt->bind_param("ss", $sessionId, $postalCode);
            $stmt->execute();
            $requestId = $stmt->insert_id;
            $stmt->close();
            while (true) {
                $checkQuery = "SELECT id FROM request_queue WHERE status = 'processing' OR status = 'pending' ORDER BY id ASC LIMIT 1";
                $result = $conn->query($checkQuery);
                $row = $result->fetch_assoc();

                if ($row['id'] == $requestId) {
                    $updateQuery = "UPDATE request_queue SET status = 'processing' WHERE id = ?";
                    $stmt = $conn->prepare($updateQuery);
                    $stmt->bind_param("i", $requestId);
                    $stmt->execute();
                    $stmt->close();
                    break;
                } else {
                    sleep(1);
                }
            }

            $coordinates = processPostalCode($postalCode);
            $updateQuery = "UPDATE request_queue SET status = 'completed' WHERE id = ?";
            $stmt = $conn->prepare($updateQuery);
            $stmt->bind_param("i", $requestId);
            $stmt->execute();
            $stmt->close();

            return $coordinates;        

        } catch (Exception $exception) {
            error_log("Error in fetch-coordinates.php: " . $exception->getMessage() . "\n", 3, "./logs/errors-log.log");
        }
    }

    function processPostalCode($postalCode) {
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
    }
    


?>
