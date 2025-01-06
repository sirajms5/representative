package jobs;

import java.util.List;

import api.BoundariesApiFetch;
import classes.Boundary;
import classes.Representative;
import db.HocBoundariesCRUD;
import db.RepresentativeCRUD;
import disk.JsonWriter;
import utilities.LogKeeper;

public class HocBoundariesJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocBoundariesJob() {
        logKeeper.appendLog("======================================== Executing HOC Boundaries Job ========================================");
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        List<Representative> hocMembers = representativeCRUD.getHocMembers();
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
