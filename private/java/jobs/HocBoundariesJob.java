package jobs;

import java.util.List;

import api.BoundariesApiFetch;
import classes.Boundary;
import classes.HOCMember;
import db.HocBoundariesCRUD;
import db.HocRepresentativeCRUD;
import disk.JsonWriter;
import utilities.LogKeeper;

public class HocBoundariesJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocBoundaries() {
        logKeeper.appendLog("======================================== Executing HOC Boundaries Job ========================================");
        HocRepresentativeCRUD hocRepresentativeCRUD = new HocRepresentativeCRUD();
        List<HOCMember> hocMembers = hocRepresentativeCRUD.getHocMembers();
        BoundariesApiFetch boundariesApiFetch = new BoundariesApiFetch();
        List<Boundary> boundaries = boundariesApiFetch.fetchHocBoundaryByExternalId(hocMembers);
        HocBoundariesCRUD hocBoundariesCRUD = new HocBoundariesCRUD();
        for(Boundary boundary : boundaries) {
            hocBoundariesCRUD.insertBoundary(boundary);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocBoundariesJson();
        logKeeper.appendLog("======================================== Finished HOC Boundaries Job ========================================");
    }
    
}
