package api;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.json.JSONArray;
import org.json.JSONObject;

import classes.Boundary;
import classes.Representative;
import db.BoundariesCRUD;
import db.RepresentativeCRUD;
import enums.RepresentativePositionEnum;
import utilities.APIHelpers;
import utilities.Constants;
import utilities.Helpers;
import utilities.LogKeeper;

public class BoundariesApiFetch {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public String fetchBoundaryExternalIdByConstituency(String constituency) {
        String boundaryExternalId = null;
        try {
            String encodedConstituency = URLEncoder.encode(constituency, StandardCharsets.UTF_8.toString());
            String apiUrl = String.format("https://represent.opennorth.ca/boundaries/?name=%s", encodedConstituency);
            logKeeper.appendLog("Fetching boundary external id for: " + constituency);
            HttpURLConnection conn = APIHelpers.createHttpConnection(apiUrl);

            // Check response code
            if (conn.getResponseCode() != 200) {
                throw new RuntimeErrorException(null, "Failed to reach the API");
            }

            JSONObject response = APIHelpers.parseJsonResponse(conn);
            conn.disconnect();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray objectsArray = jsonResponse.getJSONArray("objects");

            if (objectsArray.length() > 0) {
                JSONObject obj = objectsArray.getJSONObject(0); // Assume the first result is correct
                boundaryExternalId = obj.optString("external_id", null);
                logKeeper.appendLog("Found external id for " + constituency + ": " + boundaryExternalId);
                Helpers.sleep(1);
            } else {
                logKeeper.appendLog("Response 200 but no actual boundaries found");
            }

        } catch (Exception e) {
            logKeeper.appendLog(e.getMessage());
        }
        return boundaryExternalId;
    }

    public List<Boundary> fetchHocBoundaryByExternalId(List<Representative> hocRepresentatives,
            String boundaryIdOrUrl) {
        List<Boundary> boundaries = new ArrayList<>();
        int fetchCounter = 0;
        for (Representative hocRepresentative : hocRepresentatives) {
            try {
                fetchCounter = fetchCounter + 1;
                String apiUrl = "";
                switch (boundaryIdOrUrl) {
                    case "url":
                        apiUrl = Constants.REPRESENTATIVE_API_BASE_URL + hocRepresentative.getBoundaryUrl();
                        logKeeper.appendLog("Fetching boundaries " + fetchCounter + " for constituency: " 
                                + hocRepresentative.getConstituency());
                        break;
                    case "id":
                        String encodedBoundaryExternalId = URLEncoder.encode(hocRepresentative.getBoundaryExternalId(),
                                StandardCharsets.UTF_8.toString());
                        apiUrl = String.format(
                                "https://represent.opennorth.ca/boundaries/federal-electoral-districts/%s/",
                                encodedBoundaryExternalId);
                        logKeeper.appendLog("Fetching data for " + fetchCounter + " with Boundary id of: "
                                + hocRepresentative.getBoundaryExternalId());
                        break;
                    default:
                        // do nothing
                        break;
                }

                // Fetch boundary details
                HttpURLConnection conn = APIHelpers.createHttpConnection(apiUrl);
                if (conn.getResponseCode() != 200) {
                    unavilableHocBoundary(hocRepresentative);
                    continue;
                }

                JSONObject boundaryResponse = APIHelpers.parseJsonResponse(conn);
                conn.disconnect();

                if (boundaryResponse.length() > 0) {
                    // Extract details from the response
                    String name = boundaryResponse.optString("name"); // Boundary name
                    String externalId = boundaryResponse.optString("external_id"); // Boundary external id
                    hocRepresentative.setBoundaryExternalId(externalId);
                    RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
                    if(!hocRepresentative.getPosition().equals(RepresentativePositionEnum.MP.getValue())) {
                        externalId = representativeCRUD.updateProvincialMemberBoundaryId(hocRepresentative); // reassigning incase the id was duplicate (API Representative issue)
                    }
                    
                    JSONArray extentArray = boundaryResponse.optJSONArray("extent");
                    double minLongitude = extentArray.getDouble(0); // boundary min longitude
                    double minLatitude = extentArray.getDouble(1); // boundary min latitude
                    double maxLongitude = extentArray.getDouble(2); // boundary max longitude
                    double maxLatitude = extentArray.getDouble(3); // boundary max latitude
                    String shapeUrl = Constants.REPRESENTATIVE_API_BASE_URL
                            + boundaryResponse.getJSONObject("related").getString("shape_url");
                    Boundary boundary = new Boundary(externalId, name, minLatitude, maxLatitude, minLongitude,
                            maxLongitude, shapeUrl);
                    boundaries.add(boundary);
                } else {
                    unavilableHocBoundary(hocRepresentative);
                }

            } catch (Exception e) {
                logKeeper.appendLog(e.getMessage());
            }

            Helpers.sleep(1);
        }
        return boundaries;
    }

    private void unavilableHocBoundary(Representative hocRepresentative) {
        BoundariesCRUD hocBoundariesCRUD = new BoundariesCRUD();
        hocBoundariesCRUD.insertUnavilableBoundary(hocRepresentative);
    }
}
