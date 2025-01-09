package disk;

import java.util.List;

import classes.Boundary;
import classes.GeoJsonFeatureCollection;
import classes.Representative;
import db.BoundariesCRUD;
import db.RepresentativeCRUD;
import enums.RepresentativePositionEnum;
import utilities.LogKeeper;

public class JsonWriter {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void writeHocMembersJson() {
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        List<Representative> hocRepresentatives = representativeCRUD.getHocMembers(RepresentativePositionEnum.MP.getValue());
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int index = 0; index < hocRepresentatives.size(); index++) {
            Representative hocMember = hocRepresentatives.get(index);
            jsonBuilder.append(hocMember.toJson());
            if (index < hocRepresentatives.size() - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("]");
        DiskUtilities diskUtilities = new DiskUtilities();
        diskUtilities.txtWriter(jsonBuilder.toString(), "C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\json\\hoc\\HOCMembers.json");
        logKeeper.appendLog("Created JSON file for HOC members");
    }   
    
    public void writeHocBoundariesJson () {
        BoundariesCRUD hocBoundariesCRUD = new BoundariesCRUD();
        List<Boundary> boundaries = hocBoundariesCRUD.getHocBoundaries();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int index = 0; index < boundaries.size(); index++) {
            Boundary boundary = boundaries.get(index);
            jsonBuilder.append(boundary.toJson());
            if (index < boundaries.size() - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("]");
        DiskUtilities diskUtilities = new DiskUtilities();
        diskUtilities.txtWriter(jsonBuilder.toString(), "C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\json\\hoc\\HOCBoundaries.json");
        logKeeper.appendLog("Created JSON file for HOC boundaries");
    }

    public void writeHocBoundariesGeoJson (GeoJsonFeatureCollection geoJsonFeatureCollection, String jsonFileName) {
        String geoCollectionJson =  geoJsonFeatureCollection.toJson();
        DiskUtilities diskUtilities = new DiskUtilities();
        String path = String.format("C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\json\\hoc\\%s.json", jsonFileName);
        diskUtilities.txtWriter(geoCollectionJson, path);
        logKeeper.appendLog("Created JSON file for HOC boundaries geo json");
    }
}
