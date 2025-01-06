package api;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import classes.Representative;
import classes.Office;
import db.HocRepresentativeCRUD;
import utilities.APIHelpers;
import utilities.Constants;
import utilities.Helpers;
import utilities.LogKeeper;

public class HOCApiFetch {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public List<Representative> fetchHOCMembersFromApi(List<Representative> hocRepresentatives) {
        List<Representative> updatedHocRepresentatives = new ArrayList<>();
        int fetchCounter = 0;
        for (Representative hocRepresentative : hocRepresentatives) {
            try {
                String encodedFirstName = URLEncoder.encode(hocRepresentative.getFirstName(),
                        StandardCharsets.UTF_8.toString());
                String encodedLastName = URLEncoder.encode(hocRepresentative.getLastName(), StandardCharsets.UTF_8.toString());
                String encodedPosition = URLEncoder.encode(hocRepresentative.getPosition(), StandardCharsets.UTF_8.toString());
                // Build API URL with parameters from representative
                String url = Constants.REPRESENTATIVE_API_BASE_URL + Constants.HOC_REPRESENTATIVE_API_URL;
                String apiUrl = String.format(
                        url,
                        encodedFirstName,
                        encodedLastName,
                        encodedPosition);

                fetchCounter = fetchCounter + 1;
                logKeeper.appendLog("Fetching data for " + fetchCounter + ": " + hocRepresentative.getFirstName() + " " + hocRepresentative.getLastName());
                HttpURLConnection conn = APIHelpers.createHttpConnection(apiUrl);

                // Check response code
                if (conn.getResponseCode() != 200) {
                    unavilableHocMember(hocRepresentative);
                    continue;
                }

                JSONObject respnse = APIHelpers.parseJsonResponse(conn);
                conn.disconnect();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(respnse.toString());
                JSONArray objectsArray = jsonResponse.getJSONArray("objects");

                if (objectsArray.length() > 0) {
                    JSONObject obj = objectsArray.getJSONObject(0); // Assume the first result is correct

                    // Update representative with API data
                    hocRepresentative.setPhotoUrl(obj.optString("photo_url", null));
                    hocRepresentative.setEmail(obj.optString("email", null));
                    hocRepresentative.setUrl(obj.optString("url", null));

                    // Set languages
                    JSONObject extra = obj.optJSONObject("extra");
                    if (extra != null && extra.has("preferred_languages")) {
                        JSONArray languagesArray = extra.getJSONArray("preferred_languages");
                        hocRepresentative.setLanguages(languagesArray.getString(0));
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
                        hocRepresentative.setOffices(offices);
                    }

                    // Set roles
                    if (extra != null && extra.has("roles")) {
                        JSONArray rolesArray = extra.getJSONArray("roles");
                        List<String> roles = new ArrayList<>();
                        for (int j = 0; j < rolesArray.length(); j++) {
                            roles.add(rolesArray.getString(j));
                        }
                        hocRepresentative.setRoles(roles);
                    }

                    // Set boundaryExternalId
                    JSONObject related = obj.optJSONObject("related");
                    if (related != null) {
                        String boundaryUrl = related.optString("boundary_url", null);
                        if (boundaryUrl != null && boundaryUrl.matches(".*/(\\d+)/?$")) {
                            String boundaryId = boundaryUrl.replaceAll(".*/(\\d+)/?$", "$1");
                            hocRepresentative.setBoundaryExternalId(boundaryId);
                        } else {
                            hocRepresentative.setBoundaryExternalId(null);
                        }
                    }

                    // Add updated member to the list
                    updatedHocRepresentatives.add(hocRepresentative);
                } else {
                    unavilableHocMember(hocRepresentative);                   
                    continue;
                }

            } catch (Exception e) {
                logKeeper.appendLog(e.getMessage());
            }

            Helpers.sleep(1);
        }

        return updatedHocRepresentatives;
    }

    private void unavilableHocMember(Representative hocRepresentative) {
        HocRepresentativeCRUD hocRepresentativeCRUD = new HocRepresentativeCRUD();
        hocRepresentativeCRUD.insertUnavailableRepresentative(hocRepresentative);  
    }
}
