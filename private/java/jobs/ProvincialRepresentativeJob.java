package jobs;

import api.MppMlaMnaApiFetch;
import utilities.Constants;
import utilities.LogKeeper;
import utilities.RepresentativePositionEnum;

public class ProvincialRepresentativeJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeMppMlaMnaRepresentativeJob() {
        logKeeper.appendLog("======================================== Executing Ontario MPP Representatives Job ========================================");
        MppMlaMnaApiFetch mppApiFetch = new MppMlaMnaApiFetch();
        mppApiFetch.fetchRepresentativesFromApi(RepresentativePositionEnum.MPP.getValue());
        

        logKeeper.appendLog("======================================== Finished Ontario MPP Representatives Job ========================================");
    }
}
