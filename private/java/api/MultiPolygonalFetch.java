package api;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import classes.GeoJsonFeature;
import classes.GeoJsonFeatureCollection;
import classes.HocMemberBoundaryPair;
import classes.MultiPolygon;
import classes.Properties;
import db.HocBoundariesCRUD;
import utilities.APIHelpers;
import utilities.Helpers;
import utilities.LogKeeper;

public class MultiPolygonalFetch {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public GeoJsonFeatureCollection fetchHocBoundaryMultiPolygonalBySimpleShapeUrl(List<HocMemberBoundaryPair> hocMemberBoundaryPairs) {
        GeoJsonFeatureCollection geoJsonFeatureCollection = null;
        List<GeoJsonFeature> geoJsonFeatures = new ArrayList<>();
        int fetchCounter = 0;
        for(HocMemberBoundaryPair hocMemberBoundaryPair : hocMemberBoundaryPairs) {
            try {
                String apiUrl = hocMemberBoundaryPair.getSimepleShapeUrl();
                fetchCounter = fetchCounter + 1;
                logKeeper.appendLog("Fetching simple shape number " + fetchCounter + "for boundary: " + hocMemberBoundaryPair.getBoundaryExternalId());

                // Fetch Sime shape details
                HttpURLConnection conn = APIHelpers.createHttpConnection(apiUrl);
                if (conn.getResponseCode() != 200) {
                    unavilableHocSimpleShapeBoundary(hocMemberBoundaryPair.getBoundaryExternalId(), hocMemberBoundaryPair.getSimepleShapeUrl());
                    continue;
                }

                JSONObject smallShapeResponse = APIHelpers.parseJsonResponse(conn);
                conn.disconnect();

                if (smallShapeResponse.length() > 0) {
                    JSONArray coordinatesArray = smallShapeResponse.optJSONArray("coordinates");
                    List<List<List<List<Double>>>> coordinates = parseCoordinates(coordinatesArray);
                    MultiPolygon multiPolygon = new MultiPolygon(coordinates, hocMemberBoundaryPair.getBoundaryExternalId());
                    Properties properties = new Properties(hocMemberBoundaryPair.getFullName(), hocMemberBoundaryPair.getConstituency(), hocMemberBoundaryPair.getPoliticalAffiliation(), hocMemberBoundaryPair.getProvinceOrTerritory());                    
                    GeoJsonFeature geoJsonFeature = new GeoJsonFeature(multiPolygon, properties);
                    geoJsonFeatures.add(geoJsonFeature);
                } else {
                    unavilableHocSimpleShapeBoundary(hocMemberBoundaryPair.getBoundaryExternalId(), hocMemberBoundaryPair.getSimepleShapeUrl());
                }
            } catch (Exception e) {
                logKeeper.appendLog(e.getMessage());
            }

            Helpers.sleep(1);
        }

        geoJsonFeatureCollection = new GeoJsonFeatureCollection(geoJsonFeatures);
        
        return geoJsonFeatureCollection;
    }

    private void unavilableHocSimpleShapeBoundary(String boundaryExternalId, String simpleShapeUrl) {
        HocBoundariesCRUD hocBoundariesCRUD = new HocBoundariesCRUD();
        hocBoundariesCRUD.insertUnavilableSimpleShape(boundaryExternalId, simpleShapeUrl);
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
