package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import classes.Office;
import classes.Representative;
import utilities.APIHelpers;
import utilities.Constants;
import utilities.Helpers;
import utilities.LogKeeper;
import utilities.ProvincialRepresentativeKeysEnum;
import utilities.RepresentativeLevelEnum;

public class MppMlaMnaApiFetch {

    private LogKeeper logKeeper = LogKeeper.getInstance();
    int representativecounter = 0;

    public List<Representative> fetchRepresentativesFromApi(String position) {
        List<Representative> representatives = new ArrayList<>();
        int offset = 0; // Start with the first page
        String firstUrl = "/representatives/?elected_office=";
        String nextUrl = Constants.REPRESENTATIVE_API_BASE_URL + firstUrl + position + "&limit="
                + Constants.REPRESENTATIVE_API_LIMIT + "&offset=" + offset;

        try {
            while (nextUrl != null) {
                // Make the HTTP request
                HttpURLConnection conn = APIHelpers.createHttpConnection(nextUrl);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) { // HTTP OK
                    // Parse the JSON response
                    JSONObject jsonResponse = APIHelpers.parseJsonResponse(conn);
                    JSONArray objects = jsonResponse.getJSONArray("objects");
                    String nextOffset = jsonResponse.getJSONObject("meta").optString("next", null);
                    if (nextOffset != null) {
                        nextUrl = Constants.REPRESENTATIVE_API_BASE_URL + nextOffset;
                    } else {
                        nextUrl = null;
                    }

                    // Extract representatives and add them to the list
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject obj = objects.getJSONObject(i);
                        representativecounter = representativecounter + 1;
                        Representative representative = parseRepresentative(obj, representativecounter);
                        representatives.add(representative);
                    }

                    // Increment the offset for the next page
                    offset += Constants.REPRESENTATIVE_API_LIMIT;
                } else {
                    logKeeper.appendLog("Failed to fetch data from API. HTTP response code: " + responseCode);
                    nextUrl = null;
                }

                Helpers.sleep(1);
            }
        } catch (Exception e) {
            logKeeper.appendLog("Error fetching representatives: " + e.getMessage());
        }

        return representatives;
    }

    private Representative parseRepresentative(JSONObject obj, int representativeCounter) {
        String sourceUrl = obj.optString("source_url");
        String provincialSourceUrlKey = extractProvinceLinkKey(sourceUrl).toUpperCase();
        String provinceOrTerritory = ProvincialRepresentativeKeysEnum
                .getProvincialOrTerritorialByKey(provincialSourceUrlKey);
        Representative representative = new Representative(
                RepresentativeLevelEnum.getProvincialOrTerritorial(provinceOrTerritory));
        representative.setFirstName(obj.optString("first_name"));
        representative.setLastName(obj.optString("last_name"));
        representative.setConstituency(obj.optString("district_name"));
        representative.setProvinceOrTerritory(provinceOrTerritory);
        representative.setPoliticalAffiliation(obj.optString("party_name"));
        representative.setPosition(obj.optString("elected_office"));
        representative.setEmail(obj.optString("email"));
        representative.setUrl(obj.optString("url"));
        representative.setPhotoUrl(obj.optString("photo_url"));

        // Set offices
        JSONArray officesArray = obj.optJSONArray("offices");
        if (officesArray != null) {
            List<Office> offices = new ArrayList<>();
            for (int j = 0; j < officesArray.length(); j++) {
                JSONObject officeObj = officesArray.getJSONObject(j);
                Office office = new Office(
                        officeObj.optString("fax", null),
                        officeObj.optString("tel", null),
                        officeObj.optString("type", null),
                        officeObj.optString("postal", null));
                offices.add(office);
            }
            representative.setOffices(offices);
        }

        // Parse extra roles
        JSONObject extra = obj.optJSONObject("extra");
        if (extra != null && extra.has("roles")) {
            JSONArray roles = extra.optJSONArray("roles");
            List<String> roleList = new ArrayList<>();
            for (int i = 0; i < roles.length(); i++) {
                roleList.add(roles.getString(i));
            }
            representative.setRoles(roleList);
        }

        logKeeper.appendLog("Fetched number " + representativeCounter + ": " + representative.getProvinceOrTerritory()
                + " " + representative.getPosition() + ": " + representative.getFirstName() + " "
                + representative.getLastName());

        return representative;
    }

    public String extractProvinceLinkKey(String url) {
        String regex = "http[s]?://[^.]+\\.([^\\.]+)\\.";
        Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}