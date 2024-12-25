import requests
import mysql.connector
from mysql.connector import Error
import time

# Database connection setup
def create_connection():
    try:
        connection = mysql.connector.connect(
            host="localhost",
            user="root",  # Replace with your MySQL username
            password="",  # Replace with your MySQL password
            database="representatives"  # Replace with your database name
        )
        if connection.is_connected():
            print("Connection to MySQL established")
        return connection
    except Error as e:
        print(f"Error: {e}")
        return None

# Fetch API data
def fetch_representatives(api_url):
    BASE_URL = "https://represent.opennorth.ca"
    representatives = []
    next_url = api_url

    while next_url:
        # Ensure next_url is a full URL
        if not next_url.startswith("http"):
            next_url = BASE_URL + next_url

        # Debugging: Print the URL being fetched
        print(f"Fetching URL: {next_url}")

        response = requests.get(next_url)
        if response.status_code == 429:
            print("Rate limit exceeded. Waiting before retrying...")
            time.sleep(60)  # Wait for 1 minute before retrying
            continue  # Retry the same request
        elif response.status_code != 200:
            print(f"Error fetching data from API: {response.status_code}")
            break

        data = response.json()
        representatives.extend(data.get('objects', []))
        next_url = data.get('meta', {}).get('next')  # Update next_url with the next page link

        # Add a 10-second delay between requests
        time.sleep(1)

    return representatives

# Insert boundary into Boundaries table
def insert_boundary(cursor, boundary):
    sql = """
    INSERT INTO Boundaries (name, boundary_type, external_id, centroid_lat, centroid_lon, geojson)
    VALUES (%s, %s, %s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE 
        name=VALUES(name),
        boundary_type=VALUES(boundary_type),
        centroid_lat=VALUES(centroid_lat),
        centroid_lon=VALUES(centroid_lon),
        geojson=VALUES(geojson);
    """
    # Extract required fields from the boundary JSON
    name = boundary.get('name')
    boundary_type = boundary.get('boundary_set_name', 'Unknown')
    external_id = boundary.get('external_id')
    centroid_lat = boundary.get('centroid', {}).get('coordinates', [None, None])[1]
    centroid_lon = boundary.get('centroid', {}).get('coordinates', [None, None])[0]

    # Execute SQL statement to insert the boundary
    cursor.execute(sql, (
        name,
        boundary_type,
        external_id,
        centroid_lat,
        centroid_lon,
        None  # Assuming geojson is not being processed currently
    ))
    
    # Fetch the boundary ID for the inserted or updated record
    cursor.execute("SELECT id FROM Boundaries WHERE external_id = %s", (external_id,))
    return cursor.fetchone()[0]

# Insert representative into Representatives table
def insert_representative(cursor, representative, boundary_id):
    sql = """
    INSERT INTO Representatives (name, first_name, last_name, position, level, district_name, party_name, email, photo_url, official_url, personal_url, gender, boundary_id)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE position=VALUES(position), level=VALUES(level), district_name=VALUES(district_name),
                            party_name=VALUES(party_name), email=VALUES(email), photo_url=VALUES(photo_url),
                            official_url=VALUES(official_url), personal_url=VALUES(personal_url), gender=VALUES(gender);
    """
    if boundary_id == None:
        boundary_id = 0
    cursor.execute(sql, (
        representative.get('name'),
        representative.get('first_name'),
        representative.get('last_name'),
        representative.get('elected_office'),
        infer_level(representative),
        representative.get('district_name'),
        representative.get('party_name'),
        representative.get('email'),
        representative.get('photo_url'),
        representative.get('url'),
        representative.get('personal_url', None),
        representative.get('gender'),
        boundary_id
    ))
    cursor.execute("SELECT id FROM Representatives WHERE name = %s AND (boundary_id = %s OR boundary_id IS NULL)", (representative['name'], boundary_id))
    return cursor.fetchone()[0]

# Insert office into Offices table
def insert_offices(cursor, representative_id, offices):
    sql = """
    INSERT INTO Offices (representative_id, type, postal_address, phone, fax)
    VALUES (%s, %s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE type=VALUES(type), postal_address=VALUES(postal_address), phone=VALUES(phone), fax=VALUES(fax);
    """
    for office in offices:
        cursor.execute(sql, (
            representative_id,
            office.get('type', 'legislature'),
            office.get('postal'),
            office.get('tel'),
            office.get('fax')
        ))
        print(f"Inserted office for {representative_id}")
        time.sleep(1)

# Insert roles into RepresentativeRoles table
def insert_roles(cursor, rep_id, roles):
    """
    Inserts roles into the database for a given representative ID.
    """
    if not roles:
        return

    sql = """
        INSERT INTO representativeroles (representative_id, role_name)
        VALUES (%s, %s)
    """
    for role in roles:
        try:
            cursor.execute(sql, (rep_id, role))
            time.sleep(1)
            print(f"Inserted role for {rep_id}")
        except Exception as e:
            print(f"Failed to insert role: {role} for representative ID: {rep_id} due to error: {e}")

# Insert languages into RepresentativeLanguages table
def insert_languages(cursor, representative_id, languages):
    sql = """
    INSERT INTO RepresentativeLanguages (representative_id, language)
    VALUES (%s, %s)
    ON DUPLICATE KEY UPDATE language=VALUES(language);
    """
    for language in languages:
        cursor.execute(sql, (representative_id, language))
        time.sleep(1)
        print(f"Inserted language for {representative_id}")

def insert_unavailable(cursor, rep_id, isOffice, isLanguage, isRole, isBoundry):
    sql = """
    INSERT INTO unavailablerepresentatives(representative_id, isoffice, islanguage, isrole, isboundry) VALUES (%s, %s, %s, %s, %s)
    """
    cursor.execute(sql, (rep_id, isOffice, isLanguage, isRole, isBoundry))
    print(f"Inserted unavailable representative for {rep_id}")
    

# Infer level based on position
def infer_level(representative):
    POSITION_LEVEL_MAP = {
        "MP": "federal",
        "MPP": "provincial",
        "MLA": "provincial",
        "MNA": "provincial",
        "Mayor": "municipal",
        "Councillor": "municipal",
    }
    return POSITION_LEVEL_MAP.get(representative.get('elected_office'), 'municipal')

# Main script to populate the database
def populate_database():
    connection = create_connection()
    if not connection:
        return

    cursor = connection.cursor()
    cursor = connection.cursor(buffered=True)

    api_url = "https://represent.opennorth.ca/representatives/"
    representatives = fetch_representatives(api_url)

    for rep in representatives:
        isBoundry = False
        isRole = False
        isLanguage = False
        isOffice = False 
        
        # Extract boundary URL from the nested JSON structure
        boundary_url = rep.get('related', {}).get('boundary_url')
        boundary_id = None  # Default to None if no boundary

        if boundary_url:
            try:
                # Construct the full URL for the boundary
                BASE_URL = "https://represent.opennorth.ca"
                full_boundary_url = f"{BASE_URL}{boundary_url}"

                # Fetch the boundary data
                boundary_response = requests.get(full_boundary_url)
                if boundary_response.status_code == 200:
                    boundary = boundary_response.json()
                    # Insert boundary into the database
                    time.sleep(1)
                    boundary_id = insert_boundary(cursor, boundary)
                    print(f"Inserted boundry for {boundary_id}")
                else:
                    print(f"Failed to fetch boundary data for URL: {full_boundary_url}")
            except Exception as e:
                print(f"FAILED to insert boundry for {boundary_id}: {e}")

        # Insert representative
        try:
            time.sleep(1)
            rep_id = insert_representative(cursor, rep, boundary_id)
            print(f"Inserted representative for {rep_id}")
            if boundary_id == None:
                isBoundry = True
        except Exception as e:
            print(f"Failed to insert representative: {rep['name']} due to error: {e}")
            continue

        # Insert offices
        offices = rep.get('offices', [])
        if offices:
            try:
                insert_offices(cursor, rep_id, offices)
            except Exception as e:
                isOffice = True
                print(f"Failed to insert offices for representative: {rep['name']} due to error: {e}")

        # Insert roles
        roles = rep.get('extra', {}).get('roles', [])
        if roles:
            try:
                insert_roles(cursor, rep_id, roles)
            except Exception as e:
                isRole = True
                print(f"Failed to insert roles for representative: {rep['name']} due to error: {e}")


        # Insert languages
        languages = rep.get('extra', {}).get('preferred_languages', [])
        if languages:
            try:
                insert_languages(cursor, rep_id, languages)
            except Exception as e:
                isLanguage = True
                print(f"Failed to insert languages for representative: {rep['name']} due to error: {e}")

        # Insert Unavailable data
        if isRole or isOffice or isLanguage or isBoundry:
            try:
                time.sleep(1)                
                insert_unavailable(cursor, rep_id, isOffice, isLanguage, isRole, isBoundry)
            except Exception as e:
                print(f"Failed to enter {rep_id} into unavailable representatives: {e}")
            

        # Commit the changes after each representative
        connection.commit()
        print(f"Successfully processed representative: {rep['name']}")

    cursor.close()
    connection.close()
    print("Database population complete.")



if __name__ == "__main__":
    populate_database()
