<?php

    include_once "conn.php";

    function getRepresentativesByCoordinates($longitude, $latitude) {
        global $conn;
        $getRepresentativesQuery = "SELECT 
                representatives.id AS representative_id,
                representatives.first_name, 
                representatives.last_name, 
                representatives.constituency, 
                representatives.province_or_territory, 
                representatives.political_affiliation, 
                representatives.email, 
                representatives.position, 
                representatives.photo_url, 
                representatives.level, 
                representatives.languages, 
                representatives.url,
                representatives.is_honourable,
                representative_offices.type AS office_type,
                representative_offices.postal_code AS office_postal_code,
                representative_offices.phone AS office_phone,
                representative_offices.fax AS office_fax
            FROM 
                representatives
            JOIN 
                boundaries_polygons
            ON 
                representatives.boundary_external_id = boundaries_polygons.boundary_external_id
            LEFT JOIN 
                representative_offices
            ON 
                representatives.id = representative_offices.representative_id
            WHERE 
                ST_Contains(
                    boundaries_polygons.polygon, 
                    ST_GeomFromText(?)
                );
        ";

        try {
            $stmt = $conn->prepare($getRepresentativesQuery);
            $point = "POINT($latitude $longitude)";
            $stmt->bind_param("s", $point);
            $stmt->execute();
            $result = $stmt->get_result();
            $representatives = [
                'federal' => [],
                'provincial' => [],
                'municipal' => []
            ];

            while ($row = $result->fetch_assoc()) {
                $repId = $row['representative_id'];
                $level = strtolower($row['level']);
                if (!in_array($level, ['federal', 'provincial', 'municipal'])) {
                    error_log("Unknown level for representative ID $repId: " . $row['level'] . "\n", 3, "./logs/errors-log.log");
                    continue;
                }

                if (!isset($representatives[$level][$repId])) {
                    $consituency = str_replace("—", " ", $row['constituency']);
                    $languages = str_replace("  ", " / ", $row['languages']);      
                    $isHonourable = $row["is_honourable"] == 1 ? true : false;  
                    $email = $row['email'];
                    if (preg_match('/^\d/', $email)) {
                        $email = 'N/A';
                    }

                    $representatives[$level][$repId] = [
                        "first_name" => $row['first_name'],
                        "last_name" => $row['last_name'],
                        "constituency" => $consituency,
                        "province_or_territory" => $row['province_or_territory'],
                        "political_affiliation" => $row['political_affiliation'],
                        "email" => $email,
                        "position" => $row['position'],
                        "photo_url" => $row['photo_url'],
                        "level" => $row['level'],
                        "languages" => $languages,
                        "url" => $row['url'],
                        "legislature_offices" => [],
                        "constituency_offices" => [],
                        "is_honourable" => $isHonourable
                    ];
                }

                if (!empty($row['office_type']) || !empty($row['office_postal_code']) || !empty($row['office_phone']) || !empty($row['office_fax'])) {
                    $phoneNumber = formatCanadianPhoneNumber($row['office_phone']);
                    $faxNumber = formatCanadianPhoneNumber($row['office_fax']);
                    $address = formatAddress($row['office_postal_code']);
                    $office = [
                        "type" => $row['office_type'],
                        "postal_code" => $address,
                        "phone" => $phoneNumber,
                        "fax" => $faxNumber
                    ];

                    switch (strtolower($row['office_type'])) {
                        case 'constituency':
                            $representatives[$level][$repId]['constituency_offices'][] = $office;
                            break;
                        case 'legislature':
                            $representatives[$level][$repId]['legislature_offices'][] = $office;
                            break;
                        default:
                            error_log(
                                "Error in representative-crud.php: Unknown office type for representative " . 
                                $representatives[$level][$repId]['first_name'] . " " . $representatives[$level][$repId]['last_name'] . "\n", 
                                3, 
                                "./logs/errors-log.log"
                            );
                            break;
                    }
                }
            }            
            
            $stmt->close();
            $representatives['federal'] = array_values($representatives['federal']);
            $representatives['provincial'] = array_values($representatives['provincial']);
            $representatives['municipal'] = array_values($representatives['municipal']);

            if (isset($representatives['federal'][0]['province_or_territory'])) {
                $representatives['municipal'][0]['province_or_territory'] = $representatives['federal'][0]['province_or_territory'];
            } else if (isset($representatives['provincial'][0]['province_or_territory'])) {
                $representatives['municipal'][0]['province_or_territory'] = $representatives['provincial'][0]['province_or_territory'];
            } else {
                error_log("Federal data or province_or_territory is missing: federal: " . $representatives['federal'] . " and provenical: " . $representatives["provincial"] . "\n", 3, "./logs/errors-log.log");
            }
            
            return [
                'federal' => $representatives['federal'],
                'provincial' => $representatives['provincial'],
                'municipal' => $representatives['municipal']
            ];            
        } catch (Exception $exception) {
            error_log("Error in representative-crud.php: " . $exception->getMessage() . "\n", 3, "./logs/errors-log.log");
            return array("error" => "Error in representative-crud.php: " . $exception->getMessage());
        }
    }

    function formatCanadianPhoneNumber($phoneNumber) {
        $phoneNumber = preg_replace('/\s+/', ' ', trim($phoneNumber));
        if (preg_match('/^(?:1\s?)?(\d{3})[\s\-]?(\d{3})[\s\-]?(\d{4})$/', $phoneNumber, $matches)) {

            return "({$matches[1]}) {$matches[2]}-{$matches[3]}";
        }
    
        return $phoneNumber;
    }

    function formatAddress($address) {
        $lines = explode("\n", $address);    
        $title = $lines[0] ?? "";
        $street = $lines[1] ?? "";
        if (stripos($title, 'House of Commons') === 0) {
            return "House of Commons\nOttawa ON\nCanada\nK1A 0A6";
        }

        $unit = isset($lines[2]) && preg_match('/^(Unit|Suite)/i', trim($lines[2])) ? trim(preg_replace('/^(Unit|Suite)\s*/i', '', $lines[2])) : "";
        $cityProv = isset($lines[2]) && !$unit ? trim($lines[2]) : (isset($lines[3]) ? trim($lines[3]) : "");
        $postalCode = isset($lines[3]) && !$unit ? trim($lines[3]) : (isset($lines[4]) ? trim($lines[4]) : "");    
        if ($unit) {
            $street = preg_replace('/^(.*?)([0-9])/', trim($unit) . '-$1$2', $street);
        }

        if (preg_match('/^(.*?)([A-Z]{2})\s+([A-Z0-9]{3}\s*[A-Z0-9]{3})$/', $cityProv, $matches)) {
            $cityProv = trim($matches[1]) . ', ' . $matches[2];
            $postalCode = $matches[3];
        }
    
        $formattedAddress = $title . "\n" . $street . "\n" . $cityProv . "\n" . $postalCode;
    
        return $formattedAddress;
    }

    /* Sample of a single json return
    [
        {
            "first_name": "Omar",
            "last_name": "Alghabra",
            "constituency": "Mississauga Centre",
            "province_or_territory": "Ontario",
            "political_affiliation": "Liberal",
            "email": "omar.alghabra@parl.gc.ca",
            "position": "MP",
            "photo_url": "https://www.ourcommons.ca/Content/Parliamentarians/Images/OfficialMPPhotos/44/AlghabraOmar_Lib.jpg",
            "level": "Federal",
            "languages": "English / French",
            "url": "https://www.ourcommons.ca/Members/en/omar-alghabra(89535)",
            "legislature_offices": [
            {
                "type": "legislature",
                "postal_code": "House of Commons\nOttawa ON\nCanada\nK1A 0A6",
                "phone": "(613) 992-1301",
                "fax": "(613) 992-1301"
            }
            ],
            "constituency_offices": [
            {
                "type": "constituency",
                "postal_code": "Main office - Mississauga\n506-10 Kingsbridge Garden Circle\nMississauga, ON\nL5R 3K6",
                "phone": "(905) 848-8595",
                "fax": "(905) 848-2712"
            }
            ],
            "is_honourable": true
        }
    ]
    */
?>