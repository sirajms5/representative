package jobs;

import java.util.List;

import api.BoundariesApiFetch;
import classes.Boundary;
import classes.Representative;
import db.BoundariesCRUD;
import db.RepresentativeCRUD;
import disk.JsonWriter;
import utilities.Constants;
import utilities.LogKeeper;
import utilities.RepresentativePositionEnum;

public class HocBoundariesJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocBoundariesJob() {
        logKeeper.appendLog("======================================== Executing HOC Boundaries Job ========================================");
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        List<Representative> hocMembers = representativeCRUD.getHocMembers(RepresentativePositionEnum.MP.getValue());
        BoundariesApiFetch boundariesApiFetch = new BoundariesApiFetch();
        List<Boundary> boundaries = boundariesApiFetch.fetchHocBoundaryByExternalId(hocMembers, Constants.ID);
        BoundariesCRUD hocBoundariesCRUD = new BoundariesCRUD();
        for(Boundary boundary : boundaries) {
            hocBoundariesCRUD.insertBoundary(boundary);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocBoundariesJson();
        logKeeper.appendLog("======================================== Finished HOC Boundaries Job ========================================");
    }
    
}
