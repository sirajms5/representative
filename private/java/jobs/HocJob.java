package jobs;

import java.util.List;

import api.HOCApiFetch;
import classes.HOCMember;
import csv.CSVReader;
import db.RepresentativeCRUD;
import disk.JsonWriter;
import selenium.ScrappingRemaningHocMembers;
import utilities.LogKeeper;

public class HocJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocJob() {
        logKeeper.appendLog("======================================== Executing HOC Job ========================================");
        CSVReader csvReader = new CSVReader();
        List<HOCMember> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\csv\\export.csv");
        HOCApiFetch hocApiFetch = new HOCApiFetch();
        List<HOCMember> updatedMembers = hocApiFetch.fetchHOCMembersFromApi(members);        
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        for (HOCMember hocMember : updatedMembers) {
            representativeCRUD.insertHOCMemeber(hocMember);
        }

        List<HOCMember> unavilableHocMembers = representativeCRUD.getUnavilableHOCMembers();
        ScrappingRemaningHocMembers scrappingRemaningHocMembers = new ScrappingRemaningHocMembers();
        List<HOCMember> scrappedHOCMembers = scrappingRemaningHocMembers.scrapHocMembers(unavilableHocMembers);
        for (HOCMember hocMember : scrappedHOCMembers) {
            boolean isInserted = representativeCRUD.insertHOCMemeber(hocMember);
            if(isInserted) {
                representativeCRUD.updateUnavilableHOCMember(hocMember, isInserted);
            }
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocMembersJson();
        logKeeper.appendLog("======================================== Finished HOC Job ========================================");
    }
}
