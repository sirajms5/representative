package jobs;

import java.util.List;

import api.MultiPolygonalFetch;
import classes.GeoJsonFeatureCollection;
import classes.HocMemberBoundaryPair;
import db.HocBoundriesMultiPolygonalUtilities;
import disk.JsonWriter;
import utilities.LogKeeper;

public class HocBoundariesMultPolygonalJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocBoundariesMultiPolygonalJob() {
        logKeeper.appendLog("======================================== Executing HOC Boundaries MultiPolygonal Job ========================================");
        HocBoundriesMultiPolygonalUtilities hocBoundriesMultiPolygonalUtilities = new HocBoundriesMultiPolygonalUtilities();
        List<HocMemberBoundaryPair> hocMemberBoundaryPairs = hocBoundriesMultiPolygonalUtilities.getHocMemberRepresentativesAndBoundaries();
        MultiPolygonalFetch multiPolygonalFetch = new MultiPolygonalFetch();
        GeoJsonFeatureCollection geoJsonFeatureCollection = multiPolygonalFetch.fetchHocBoundaryMultiPolygonalByShapeUrl(hocMemberBoundaryPairs);
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocBoundariesGeoJson(geoJsonFeatureCollection);
        logKeeper.appendLog("======================================== Finished HOC Boundaries MultiPolygonal Job ========================================");
    }

}
