package jobs;

import utilities.LogKeeper;

public class MppMlaMnaRepresentativeJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeMppMlaMnaRepresentativeJob() {
        logKeeper.appendLog("======================================== Executing Ontario MPP Representatives Job ========================================");
        

        logKeeper.appendLog("======================================== Finished Ontario MPP Representatives Job ========================================");
    }
}
