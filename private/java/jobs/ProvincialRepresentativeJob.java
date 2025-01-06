package jobs;

import java.util.List;

import api.MppMlaMnaApiFetch;
import classes.Representative;
import db.RepresentativeCRUD;
import utilities.LogKeeper;
import utilities.RepresentativePositionEnum;

public class ProvincialRepresentativeJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeMppMlaMnaRepresentativeJob() {
        logKeeper.appendLog("======================================== Executing Ontario MPP Representatives Job ========================================");
        MppMlaMnaApiFetch mppApiFetch = new MppMlaMnaApiFetch();
        List<Representative> ontarioRepresentatives = mppApiFetch.fetchRepresentativesFromApi(RepresentativePositionEnum.MPP.getValue());
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        for (Representative representative : ontarioRepresentatives) {
            representativeCRUD.insertRepresentative(representative);
        }        

        logKeeper.appendLog("======================================== Finished Ontario MPP Representatives Job ========================================");
    }
}
