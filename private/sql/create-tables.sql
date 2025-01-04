CREATE TABLE representatives (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,  
    constituency VARCHAR(255),       
    province_or_territory VARCHAR(255),
    political_affiliation VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    start_date VARCHAR(30),                 
    position VARCHAR(255),             
    photo_url TEXT,                    
    boundary_external_id VARCHAR(50),  
    level VARCHAR(50),
    languages VARCHAR(50),
    url VARCHAR(255),
    fed_uid VARCHAR(20);
    UNIQUE (position, level, boundary_external_id, first_name, last_name)            
);

CREATE TABLE representative_offices (
    id INT AUTO_INCREMENT PRIMARY KEY,     
    representative_id INT NOT NULL,         
    type VARCHAR(50),                      
    postal_code VARCHAR(255),             
    phone VARCHAR(50),                      
    fax VARCHAR(50),                       
    FOREIGN KEY (representative_id) REFERENCES representatives(id) ON DELETE CASCADE,
    UNIQUE (representative_id, type, postal_code, phone, fax)
);

CREATE TABLE representative_roles (
    id INT AUTO_INCREMENT PRIMARY KEY,    
    representative_id INT NOT NULL,         
    role_name VARCHAR(255) NOT NULL,       
    FOREIGN KEY (representative_id) REFERENCES representatives(id) ON DELETE CASCADE,
    UNIQUE (representative_id, role_name)
);

CREATE TABLE unavilable_representative (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    added BOOLEAN DEFAULT 0,
    UNIQUE (first_name, last_name)
);

CREATE TABLE boundaries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    external_id VARCHAR(255) NOT NULL UNIQUE,
    boundary_name VARCHAR(255) NOT NULL, 
    min_latitude DOUBLE NOT NULL,          
    max_latitude DOUBLE NOT NULL,        
    min_longitude DOUBLE NOT NULL,   
    max_longitude DOUBLE NOT NULL,
    shape_url VARCHAR(255)
);

-- CREATE TABLE boundary_coordinates (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     boundary_external_id VARCHAR(255) NOT NULL, 
--     polygon_index INT NOT NULL,                 
--     ring_index INT NOT NULL,                    
--     coordinate_index INT NOT NULL,              
--     latitude DOUBLE NOT NULL,                   
--     longitude DOUBLE NOT NULL,                  
--     FOREIGN KEY (boundary_external_id) REFERENCES boundaries(external_id) ON DELETE CASCADE
-- );

CREATE TABLE unavilable_hoc_boundary (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    boundary_external_id VARCHAR(255) UNIQUE,
    added BOOLEAN DEFAULT 0
);

CREATE TABLE unavilable_hoc_shape(
    id INT AUTO_INCREMENT PRIMARY KEY, 
    boundary_external_id VARCHAR(255) UNIQUE,
    shape_url VARCHAR(255),
    added BOOLEAN DEFAULT 0
);

CREATE TABLE postal_code_coordinates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    postal_code VARCHAR(10) UNIQUE,
    latitude DOUBLE DEFAULT NULL,
    longitude DOUBLE DEFAULT NULL,
    display_name VARCHAR(255) DEFAULT NULL,
    isfound BOOLEAN DEFAULT false,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE nominatim_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_time INT NOT NULL,
    request_time_readable TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE boundaries_polygons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fedname VARCHAR(255),
    feduid VARCHAR(50) UNIQUE,
    polygon GEOMETRY NOT NULL,
    SPATIAL INDEX (polygon) -- Optimize spatial queries
);

-- REMOVE BOUNDARIES FROM HOC AND ADD FEDUID 