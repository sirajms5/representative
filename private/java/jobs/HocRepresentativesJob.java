package jobs;

import java.util.List;

import api.HOCApiFetch;
import classes.Representative;
import csv.CSVReader;
import db.HocBoundaryPolygonsCRUD;
import db.RepresentativeCRUD;
import disk.JsonWriter;
import selenium.ScrappingRemaningHocMembers;
import utilities.LogKeeper;
import utilities.RepresentativePositionEnum;

public class HocRepresentativesJob {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void executeHocRepresentativesJob() {
        logKeeper.appendLog("======================================== Executing HOC Representatives Job ========================================");
        // https://www.ourcommons.ca/Members/en/search
        CSVReader csvReader = new CSVReader();
        List<Representative> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\csv\\export.csv");
        HOCApiFetch hocApiFetch = new HOCApiFetch();
        List<Representative> updatedMembers = hocApiFetch.fetchHOCMembersFromApi(members);        
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        for (Representative representative : updatedMembers) {
            representativeCRUD.insertRepresentative(representative);
        }

        List<Representative> unavilableHocMembers = representativeCRUD.getUnavilableHOCMembers(RepresentativePositionEnum.MP.getValue());
        ScrappingRemaningHocMembers scrappingRemaningHocMembers = new ScrappingRemaningHocMembers();
        List<Representative> scrappedHOCMembers = scrappingRemaningHocMembers.scrapHocMembers(unavilableHocMembers);
        for (Representative hocRepresentative : scrappedHOCMembers) {
            boolean isInserted = representativeCRUD.insertRepresentative(hocRepresentative);
            if(isInserted) {
                representativeCRUD.updateUnavilableHOCMember(hocRepresentative, isInserted);
            }
        }

        // Assign boundary polygon ID to HOC
        List<Representative> fullList = representativeCRUD.getHocMembers();
        HocBoundaryPolygonsCRUD hocBoundaryPolygonsCRUD = new HocBoundaryPolygonsCRUD();
        for(Representative hocRepresentative : fullList) {
            String fedUid = hocBoundaryPolygonsCRUD.getFedUidByConstituency(hocRepresentative);
            hocRepresentative.setFedUid(fedUid);
            representativeCRUD.updateHocMemberFedUid(hocRepresentative);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeHocMembersJson();
        logKeeper.appendLog("======================================== Finished HOC Representatives Job ========================================");
    }
}
