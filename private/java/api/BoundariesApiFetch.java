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
import classes.HOCMember;
import db.HocBoundariesCRUD;
import utilities.APIHelpers;
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

    public List<Boundary> fetchHocBoundaryByExternalId(List<HOCMember> hocMembers) {
        List<Boundary> boundaries = new ArrayList<>();
        int fetchCounter = 0;
        for (HOCMember hocMember : hocMembers) {
            try {
                String encodedBoundaryExternalId = URLEncoder.encode(hocMember.getBoundaryExternalId(),
                        StandardCharsets.UTF_8.toString());
                String apiUrl = String.format(
                        "https://represent.opennorth.ca/boundaries/federal-electoral-districts/%s/",
                        encodedBoundaryExternalId);
                fetchCounter = fetchCounter + 1;
                logKeeper.appendLog("Fetching data for " + fetchCounter + " with Boundary id of: "
                        + hocMember.getBoundaryExternalId());

                // Fetch boundary details
                HttpURLConnection conn = APIHelpers.createHttpConnection(apiUrl);
                if (conn.getResponseCode() != 200) {
                    unavilableHocBoundary(hocMember);
                    continue;
                }

                JSONObject boundaryResponse = APIHelpers.parseJsonResponse(conn);
                conn.disconnect();

                if (boundaryResponse.length() > 0) {
                    // Extract details from the response
                    String name = boundaryResponse.optString("name"); // Boundary name
                    String externalId = boundaryResponse.optString("external_id"); // Boundary external id
                    JSONArray extentArray = boundaryResponse.optJSONArray("extent");
                    double minLongitude = extentArray.getDouble(0); // boundary min longitude
                    double minLatitude = extentArray.getDouble(1); // boundary min latitude
                    double maxLongitude = extentArray.getDouble(2); // boundary max longitude
                    double maxLatitude = extentArray.getDouble(3); // boundary max latitude
                    String simpleShapeUrl = "https://represent.opennorth.ca"
                            + boundaryResponse.getJSONObject("related").getString("simple_shape_url");

                    Helpers.sleep(1);

                    Boundary boundary = new Boundary(externalId, name, minLatitude, maxLatitude, minLongitude,
                            maxLongitude, simpleShapeUrl);
                    boundaries.add(boundary);
                } else {
                    unavilableHocBoundary(hocMember);
                }

            } catch (Exception e) {
                logKeeper.appendLog(e.getMessage());
            }

            Helpers.sleep(1);
        }
        return boundaries;
    }

    private void unavilableHocBoundary(HOCMember hocMember) {
        HocBoundariesCRUD hocBoundariesCRUD = new HocBoundariesCRUD();
        hocBoundariesCRUD.insertUnavilableBoundary(hocMember);
    }

    private void unavilableHocSimpleShapeBoundary(HOCMember hocMember, String simpleShapeUrl) {
        HocBoundariesCRUD hocBoundariesCRUD = new HocBoundariesCRUD();
        hocBoundariesCRUD.insertUnavilableSimpleShape(hocMember, simpleShapeUrl);
    }

    private List<List<List<List<Double>>>> parseCoordinates(JSONArray jsonArray) {
        List<List<List<List<Double>>>> coordinates = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray outerArray = jsonArray.getJSONArray(i);
            List<List<List<Double>>> outerList = new ArrayList<>();

            for (int j = 0; j < outerArray.length(); j++) {
                JSONArray middleArray = outerArray.getJSONArray(j);
                List<List<Double>> middleList = new ArrayList<>();

                for (int k = 0; k < middleArray.length(); k++) {
                    JSONArray innerArray = middleArray.getJSONArray(k);
                    List<Double> innerList = new ArrayList<>();

                    for (int l = 0; l < innerArray.length(); l++) {
                        innerList.add(innerArray.getDouble(l));
                    }
                    middleList.add(innerList);
                }
                outerList.add(middleList);
            }
            coordinates.add(outerList);
        }

        return coordinates;
    }
}
