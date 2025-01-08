package jobs;

import java.util.List;
import java.util.Map;

import api.BoundariesApiFetch;
import api.MppMlaMnaApiFetch;
import api.MultiPolygonalFetch;
import classes.Boundary;
import classes.GeoJsonFeatureCollection;
import classes.HocMemberBoundaryPair;
import classes.Representative;
import db.BoundariesCRUD;
import db.HocBoundriesMultiPolygonalUtilities;
import db.RepresentativeCRUD;
import disk.JsonWriter;
import selenium.MppOntarioSelenium;
import utilities.Constants;
import utilities.LogKeeper;
import utilities.RepresentativePositionEnum;

public class ProvincialRepresentativeJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeMppRepresentativeJob() {
        logKeeper.appendLog(
                "======================================== Executing Ontario MPP Representatives Job ========================================");
        MppMlaMnaApiFetch mppApiFetch = new MppMlaMnaApiFetch();
        List<Representative> ontarioRepresentatives = mppApiFetch
                .fetchRepresentativesFromApi(RepresentativePositionEnum.MPP.getValue());
        MppOntarioSelenium mppOntarioSelenium = new MppOntarioSelenium();
        Map<String, List<Representative>> validatedMembers = mppOntarioSelenium.validateMembers(ontarioRepresentatives);
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        List<Representative> updatedRepresentatives = validatedMembers.get(Constants.AVAILABLE);
        for (Representative representative : updatedRepresentatives) {
            representativeCRUD.insertRepresentative(representative);
        }

        for (Representative representative : validatedMembers.get(Constants.UNAVILABLE)) {
            representativeCRUD.insertUnavailableRepresentativeByFullName(representative);
        }

        // update MPPs with external boundary id
        BoundariesApiFetch boundariesApiFetch = new BoundariesApiFetch();
        List<Boundary> boundaries = boundariesApiFetch.fetchHocBoundaryByExternalId(updatedRepresentatives,
                Constants.URL);
        BoundariesCRUD hocBoundariesCRUD = new BoundariesCRUD();
        for (Boundary boundary : boundaries) {
            hocBoundariesCRUD.insertBoundary(boundary);
        }

        // polygons
        HocBoundriesMultiPolygonalUtilities hocBoundriesMultiPolygonalUtilities = new HocBoundriesMultiPolygonalUtilities();
        List<HocMemberBoundaryPair> hocMemberBoundaryPairs = hocBoundriesMultiPolygonalUtilities
                .getHocMemberRepresentativesAndBoundaries(RepresentativePositionEnum.MPP.getValue());
        MultiPolygonalFetch multiPolygonalFetch = new MultiPolygonalFetch();
        GeoJsonFeatureCollection geoJsonFeatureCollection = multiPolygonalFetch
                .fetchHocBoundaryMultiPolygonalByShapeUrl(hocMemberBoundaryPairs);
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocBoundariesGeoJson(geoJsonFeatureCollection, RepresentativePositionEnum.MPP.getValue());

        logKeeper.appendLog(
                "======================================== Finished Ontario MPP Representatives Job ========================================");
    }

    public void executeMnaMlaMhaRepresentativeJob(String position) {
        logKeeper.appendLog(
                "======================================== Executing Quebec MNA Representatives Job ========================================");
        MppMlaMnaApiFetch mppApiFetch = new MppMlaMnaApiFetch();
        List<Representative> quebecRepresentatives = mppApiFetch
                .fetchRepresentativesFromApi(position);
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        for (Representative representative : quebecRepresentatives) {
            representativeCRUD.insertRepresentative(representative);
        }

        // update MNAs with external boundary id
        BoundariesApiFetch boundariesApiFetch = new BoundariesApiFetch();
        List<Boundary> boundaries = boundariesApiFetch.fetchHocBoundaryByExternalId(quebecRepresentatives,
                Constants.URL);
        BoundariesCRUD hocBoundariesCRUD = new BoundariesCRUD();
        for (Boundary boundary : boundaries) {
            hocBoundariesCRUD.insertBoundary(boundary);
        }

        // polygons
        HocBoundriesMultiPolygonalUtilities hocBoundriesMultiPolygonalUtilities = new HocBoundriesMultiPolygonalUtilities();
        List<HocMemberBoundaryPair> hocMemberBoundaryPairs = hocBoundriesMultiPolygonalUtilities
                .getHocMemberRepresentativesAndBoundaries(position);
        MultiPolygonalFetch multiPolygonalFetch = new MultiPolygonalFetch();
        GeoJsonFeatureCollection geoJsonFeatureCollection = multiPolygonalFetch
                .fetchHocBoundaryMultiPolygonalByShapeUrl(hocMemberBoundaryPairs);
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocBoundariesGeoJson(geoJsonFeatureCollection, position);

        logKeeper.appendLog(
                "======================================== Finished Quebec MNA Representatives Job ========================================");
    }
}
