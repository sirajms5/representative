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
                representative_offices.type AS office_type,
                representative_offices.postal_code AS office_postal_code,
                representative_offices.phone AS office_phone,
                representative_offices.fax AS office_fax
            FROM 
                representatives
            JOIN 
                boundaries_polygons
            ON 
                representatives.fed_uid = boundaries_polygons.feduid
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

        if (isset($conn)) {
            $stmt = $conn->prepare($getRepresentativesQuery);
            $point = "POINT($latitude $longitude)";
            $stmt->bind_param("s", $point);
            $stmt->execute();
            $result = $stmt->get_result();
            $representatives = [];
            while ($row = $result->fetch_assoc()) {
                $repId = $row['representative_id'];
                if (!isset($representatives[$repId])) {
                    $representatives[$repId] = [
                        "first_name" => $row['first_name'],
                        "last_name" => $row['last_name'],
                        "constituency" => $row['constituency'],
                        "province_or_territory" => $row['province_or_territory'],
                        "political_affiliation" => $row['political_affiliation'],
                        "email" => $row['email'],
                        "position" => $row['position'],
                        "photo_url" => $row['photo_url'],
                        "level" => $row['level'],
                        "languages" => str_replace("  ", " / ", $row['languages']),
                        "url" => $row['url'],
                        "offices" => []
                    ];
                }

                // Add the office information (if any) to the representative's offices
                if (!empty($row['office_type']) || !empty($row['office_postal_code']) || !empty($row['office_phone']) || !empty($row['office_fax'])) {
                    $representatives[$repId]['offices'][] = [
                        "type" => $row['office_type'],
                        "postal_code" => $row['office_postal_code'],
                        "phone" => $row['office_phone'],
                        "fax" => $row['office_fax']
                    ];
                }
            }

            $stmt->close();

            // Reindex the array to remove keys based on representative_id
            return array_values($representatives);
        } else {
            return ["error" => "no connection to database from representative-crud.php"];
        }
    }

    /* Sample of a single json return
    [
        {
            "first_name": "Yasir",
            "last_name": "Naqvi",
            "constituency": "Ottawa Centre",
            "province_or_territory": "Ontario",
            "political_affiliation": "Liberal",
            "email": "yasir.naqvi@parl.gc.ca",
            "position": "MP",
            "photo_url": "https://www.ourcommons.ca/Content/Parliamentarians/Images/OfficialMPPhotos/44/NaqviYasir_Lib.jpg",
            "level": "Federal",
            "languages": "English, French",
            "url": "https://www.ourcommons.ca/Members/en/yasir-naqvi(110572)",
            "offices": [
            {
                "type": "constituency",
                "postal_code": "Main office - Ottawa\n404-1066 Somerset St W\nOttawa ON  K1Y 4T3",
                "phone": "1 613 946-8682",
                "fax": "1 613 946-8680"
            },
            {
                "type": "legislature",
                "postal_code": "House of Commons\nOttawa ON  K1A 0A6",
                "phone": "1 613 996-5322",
                "fax": "1 613 996-5322"
            }
            ]
        }
    ]

    */
?>