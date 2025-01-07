package jobs;

import java.util.List;
import java.util.Map;

import api.MppMlaMnaApiFetch;
import classes.Representative;
import db.RepresentativeCRUD;
import selenium.MppOntarioSelenium;
import utilities.Constants;
import utilities.LogKeeper;
import utilities.RepresentativePositionEnum;

public class ProvincialRepresentativeJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeMppMlaMnaRepresentativeJob() {
        logKeeper.appendLog(
                "======================================== Executing Ontario MPP Representatives Job ========================================");
        MppMlaMnaApiFetch mppApiFetch = new MppMlaMnaApiFetch();
        List<Representative> ontarioRepresentatives = mppApiFetch
                .fetchRepresentativesFromApi(RepresentativePositionEnum.MPP.getValue());
        MppOntarioSelenium mppOntarioSelenium = new MppOntarioSelenium();
        Map<String, List<Representative>> validatedMembers = mppOntarioSelenium.validateMembers(ontarioRepresentatives);
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        for (Representative representative : validatedMembers.get(Constants.AVAILABLE)) {
            representativeCRUD.insertRepresentative(representative);
        }

        for (Representative representative : validatedMembers.get(Constants.UNAVILABLE)) {
            representativeCRUD.insertUnavailableRepresentativeByFullName(representative);
        }

        logKeeper.appendLog(
                "======================================== Finished Ontario MPP Representatives Job ========================================");
    }
}
