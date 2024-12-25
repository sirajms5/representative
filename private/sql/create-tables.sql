CREATE TABLE Boundaries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    boundary_type VARCHAR(50) NOT NULL,
    external_id VARCHAR(50) UNIQUE NOT NULL,
    centroid_lat DECIMAL(10, 8) NULL,
    centroid_lon DECIMAL(11, 8) NULL,
    geojson JSON NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE Representatives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NULL,
    last_name VARCHAR(50) NULL,
    position VARCHAR(50) NOT NULL,  -- e.g., MP, MPP, Mayor
    level ENUM('federal', 'provincial', 'municipal') NOT NULL,
    district_name VARCHAR(100) NULL,
    party_name VARCHAR(50) NULL,
    email VARCHAR(100) NULL,
    photo_url VARCHAR(255) NULL,
    official_url VARCHAR(255) NULL,
    personal_url VARCHAR(255) NULL,
    gender ENUM('M', 'F', 'Other') NULL,
    boundary_id INT DEFAULT 0,
    FOREIGN KEY (boundary_id) REFERENCES Boundaries(id) ON DELETE CASCADE,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    UNIQUE KEY unique_rep (name, boundary_id)
);

CREATE TABLE Offices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    representative_id INT NOT NULL,
    type ENUM('legislature', 'constituency') NOT NULL,
    postal_address TEXT NULL,
    phone VARCHAR(20) NULL,
    fax VARCHAR(20) NULL,
    FOREIGN KEY (representative_id) REFERENCES Representatives(id) ON DELETE CASCADE
);

CREATE TABLE RepresentativeRoles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    representative_id INT NOT NULL,
    role_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (representative_id) REFERENCES Representatives(id) ON DELETE CASCADE
);


CREATE TABLE RepresentativeLanguages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    representative_id INT NOT NULL,
    language VARCHAR(50) NOT NULL,
    FOREIGN KEY (representative_id) REFERENCES Representatives(id) ON DELETE CASCADE
);

CREATE TABLE unavailablerepresentatives(
	id INT AUTO_INCREMENT PRIMARY KEY,
    representative_id INT,
    isoffice boolean DEFAULT false,
    islanguage boolean DEFAULT false,
    isrole boolean DEFAULT false,
    isboundry boolean DEFAULT false,
    FOREIGN KEY (representative_id) REFERENCES Representatives(id) ON DELETE CASCADE
);
