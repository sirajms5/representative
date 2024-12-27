package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import classes.HOCMember;
import classes.Office;
import db.RepresentativeCRUD;
import utilities.Helpers;
import utilities.LogKeeper;

public class HOCApiFetch {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public List<HOCMember> fetchHOCMembersFromApi(List<HOCMember> hocMembers) {
        List<HOCMember> updatedMembers = new ArrayList<>();
        int fetchCounter = 0;
        for (HOCMember hocMember : hocMembers) {
            try {
                String encodedFirstName = URLEncoder.encode(hocMember.getFirstName(),
                        StandardCharsets.UTF_8.toString());
                String encodedLastName = URLEncoder.encode(hocMember.getLastName(), StandardCharsets.UTF_8.toString());
                String encodedPosition = URLEncoder.encode(hocMember.getPosition(), StandardCharsets.UTF_8.toString());
                // Build API URL with parameters from hocMember
                String apiUrl = String.format(
                        "https://represent.opennorth.ca/representatives/house-of-commons/?first_name=%s&last_name=%s&elected_office=%s",
                        encodedFirstName,
                        encodedLastName,
                        encodedPosition);

                fetchCounter = fetchCounter + 1;
                logKeeper.appendLog("Fetching data for " + fetchCounter + ": " + hocMember.getFirstName() + " " + hocMember.getLastName());
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                // Check response code
                if (conn.getResponseCode() != 200) {
                    unavilableHocMember(hocMember);
                    continue;
                }

                // Read the response
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                conn.disconnect();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray objectsArray = jsonResponse.getJSONArray("objects");

                if (objectsArray.length() > 0) {
                    JSONObject obj = objectsArray.getJSONObject(0); // Assume the first result is correct

                    // Update hocMember with API data
                    hocMember.setPhotoUrl(obj.optString("photo_url", null));
                    hocMember.setEmail(obj.optString("email", line));

                    // Set languages
                    JSONObject extra = obj.optJSONObject("extra");
                    if (extra != null && extra.has("preferred_languages")) {
                        JSONArray languagesArray = extra.getJSONArray("preferred_languages");
                        hocMember.setLanguages(languagesArray.getString(0));
                    }

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
                        hocMember.setOffices(offices);
                    }

                    // Set roles
                    if (extra != null && extra.has("roles")) {
                        JSONArray rolesArray = extra.getJSONArray("roles");
                        List<String> roles = new ArrayList<>();
                        for (int j = 0; j < rolesArray.length(); j++) {
                            roles.add(rolesArray.getString(j));
                        }
                        hocMember.setRoles(roles);
                    }

                    // Set boundaryExternalId
                    JSONObject related = obj.optJSONObject("related");
                    if (related != null) {
                        String boundaryUrl = related.optString("boundary_url", null);
                        if (boundaryUrl != null && boundaryUrl.matches(".*/(\\d+)/?$")) {
                            String boundaryId = boundaryUrl.replaceAll(".*/(\\d+)/?$", "$1");
                            hocMember.setBoundaryExternalId(boundaryId);
                        } else {
                            hocMember.setBoundaryExternalId(null);
                        }
                    }

                    // Add updated member to the list
                    updatedMembers.add(hocMember);
                } else {
                    unavilableHocMember(hocMember);                   
                    continue;
                }

            } catch (Exception e) {
                logKeeper.appendLog(e.getMessage());
            }

            Helpers.sleep(1);
        }

        return updatedMembers;
    }

    private void unavilableHocMember(HOCMember hocMember) {
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        representativeCRUD.insertUnavailableRepresentative(hocMember);  
    }
}
