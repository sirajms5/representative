package jobs;

import java.util.List;

import api.HOCApiFetch;
import classes.Representative;
import csv.CSVReader;
import db.HocBoundaryPolygonsCRUD;
import db.HocRepresentativeCRUD;
import disk.JsonWriter;
import selenium.ScrappingRemaningHocMembers;
import utilities.Constants;
import utilities.LogKeeper;

public class HocRepresentativesJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocRepresentativesJob() {
        logKeeper.appendLog("======================================== Executing HOC Representatives Job ========================================");
        // https://www.ourcommons.ca/Members/en/search
        CSVReader csvReader = new CSVReader();
        List<Representative> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\csv\\export.csv");
        HOCApiFetch hocApiFetch = new HOCApiFetch();
        List<Representative> updatedMembers = hocApiFetch.fetchHOCMembersFromApi(members);        
        HocRepresentativeCRUD hocRepresentativeCRUD = new HocRepresentativeCRUD();
        for (Representative representative : updatedMembers) {
            hocRepresentativeCRUD.insertHOCMemeber(representative);
        }

        List<Representative> unavilableHocMembers = hocRepresentativeCRUD.getUnavilableHOCMembers(Constants.MP);
        ScrappingRemaningHocMembers scrappingRemaningHocMembers = new ScrappingRemaningHocMembers();
        List<Representative> scrappedHOCMembers = scrappingRemaningHocMembers.scrapHocMembers(unavilableHocMembers);
        for (Representative hocRepresentative : scrappedHOCMembers) {
            boolean isInserted = hocRepresentativeCRUD.insertHOCMemeber(hocRepresentative);
            if(isInserted) {
                hocRepresentativeCRUD.updateUnavilableHOCMember(hocRepresentative, isInserted);
            }
        }

        // Assign boundary polygon ID to HOC
        List<Representative> fullList = hocRepresentativeCRUD.getHocMembers();
        HocBoundaryPolygonsCRUD hocBoundaryPolygonsCRUD = new HocBoundaryPolygonsCRUD();
        for(Representative hocRepresentative : fullList) {
            String fedUid = hocBoundaryPolygonsCRUD.getFedUidByConstituency(hocRepresentative);
            hocRepresentative.setFedUid(fedUid);
            hocRepresentativeCRUD.updateHocMemberFedUid(hocRepresentative);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocMembersJson();
        logKeeper.appendLog("======================================== Finished HOC Representatives Job ========================================");
    }
}
