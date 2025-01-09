package jobs;

import java.util.List;

import api.MultiPolygonalFetch;
import classes.GeoJsonFeatureCollection;
import classes.HocMemberBoundaryPair;
import db.HocBoundriesMultiPolygonalUtilities;
import disk.JsonWriter;
import enums.RepresentativePositionEnum;
import utilities.LogKeeper;

public class HocBoundariesMultPolygonalJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocBoundariesMultiPolygonalJob() {
        logKeeper.appendLog("======================================== Executing HOC Boundaries MultiPolygonal Job ========================================");
        HocBoundriesMultiPolygonalUtilities hocBoundriesMultiPolygonalUtilities = new HocBoundriesMultiPolygonalUtilities();
        List<HocMemberBoundaryPair> hocMemberBoundaryPairs = hocBoundriesMultiPolygonalUtilities.getHocMemberRepresentativesAndBoundaries(RepresentativePositionEnum.MP.getValue());
        MultiPolygonalFetch multiPolygonalFetch = new MultiPolygonalFetch();
        GeoJsonFeatureCollection geoJsonFeatureCollection = multiPolygonalFetch.fetchHocBoundaryMultiPolygonalByShapeUrl(hocMemberBoundaryPairs);
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocBoundariesGeoJson(geoJsonFeatureCollection, "GeoJsonCollection");
        logKeeper.appendLog("======================================== Finished HOC Boundaries MultiPolygonal Job ========================================");
    }

}
