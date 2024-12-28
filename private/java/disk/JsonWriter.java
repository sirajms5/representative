package disk;

import java.util.List;

import classes.Boundary;
import classes.GeoJsonFeatureCollection;
import classes.HOCMember;
import db.HocBoundariesCRUD;
import db.HocRepresentativeCRUD;
import utilities.LogKeeper;

public class JsonWriter {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void writeHocMembersJson() {
        HocRepresentativeCRUD hocRepresentativeCRUD = new HocRepresentativeCRUD();
        List<HOCMember> hocMembers = hocRepresentativeCRUD.getHocMembers();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int index = 0; index < hocMembers.size(); index++) {
            HOCMember member = hocMembers.get(index);
            jsonBuilder.append(member.toJson());
            if (index < hocMembers.size() - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("]");
        DiskUtilities diskUtilities = new DiskUtilities();
        diskUtilities.txtWriter(jsonBuilder.toString(), "C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\json\\hoc\\HOCMembers.json");
        logKeeper.appendLog("Created JSON file for HOC members");
    }   
    
    public void writeHocBoundariesJson () {
        HocBoundariesCRUD hocBoundariesCRUD = new HocBoundariesCRUD();
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

    public void writeHocBoundariesGeoJson (GeoJsonFeatureCollection geoJsonFeatureCollection) {
        String geoCollectionJson =  geoJsonFeatureCollection.toJson();
        DiskUtilities diskUtilities = new DiskUtilities();
        diskUtilities.txtWriter(geoCollectionJson, "C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\json\\hoc\\GeoJsonCollection.json");
        logKeeper.appendLog("Created JSON file for HOC boundaries geo json");
    }
}
