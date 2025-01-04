package jobs;

import java.util.List;

import api.HOCApiFetch;
import classes.HOCMember;
import csv.CSVReader;
import db.HocBoundaryPolygonsCRUD;
import db.HocRepresentativeCRUD;
import disk.JsonWriter;
import selenium.ScrappingRemaningHocMembers;
import utilities.LogKeeper;

public class HocRepresentativesJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocRepresentativesJob() {
        logKeeper.appendLog("======================================== Executing HOC Representatives Job ========================================");
        // https://www.ourcommons.ca/Members/en/search
        CSVReader csvReader = new CSVReader();
        List<HOCMember> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\csv\\export.csv");
        HOCApiFetch hocApiFetch = new HOCApiFetch();
        List<HOCMember> updatedMembers = hocApiFetch.fetchHOCMembersFromApi(members);        
        HocRepresentativeCRUD hocRepresentativeCRUD = new HocRepresentativeCRUD();
        for (HOCMember hocMember : updatedMembers) {
            hocRepresentativeCRUD.insertHOCMemeber(hocMember);
        }

        List<HOCMember> unavilableHocMembers = hocRepresentativeCRUD.getUnavilableHOCMembers();
        ScrappingRemaningHocMembers scrappingRemaningHocMembers = new ScrappingRemaningHocMembers();
        List<HOCMember> scrappedHOCMembers = scrappingRemaningHocMembers.scrapHocMembers(unavilableHocMembers);
        for (HOCMember hocMember : scrappedHOCMembers) {
            boolean isInserted = hocRepresentativeCRUD.insertHOCMemeber(hocMember);
            if(isInserted) {
                hocRepresentativeCRUD.updateUnavilableHOCMember(hocMember, isInserted);
            }
        }

        // Assign boundary polygon ID to HOC
        List<HOCMember> fullList = hocRepresentativeCRUD.getHocMembers();
        HocBoundaryPolygonsCRUD hocBoundaryPolygonsCRUD = new HocBoundaryPolygonsCRUD();
        for(HOCMember hocMember : fullList) {
            String fedUid = hocBoundaryPolygonsCRUD.getFedUidByConstituency(hocMember);
            hocMember.setFedUid(fedUid);
            hocRepresentativeCRUD.updateHocMemberFedUid(hocMember);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocMembersJson();
        logKeeper.appendLog("======================================== Finished HOC Representatives Job ========================================");
    }
}
