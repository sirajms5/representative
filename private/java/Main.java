import java.util.List;

import api.HOCApiFetch;
import classes.HOCMember;
import csv.CSVReader;
import db.RepresentativeCRUD;
import disk.DiskUtilities;
import selenium.ScrappingRemaningHocMembers;

public class Main{
    public static void main(String[] args) {
        CSVReader csvReader = new CSVReader();
        List<HOCMember> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\csv\\files\\export.csv");
        HOCApiFetch hocApiFetch = new HOCApiFetch();
        List<HOCMember> updatedMembers = hocApiFetch.fetchHOCMembersFromApi(members);
        // Build a JSON string from the updated members
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int index = 0; index < updatedMembers.size(); index++) {
            HOCMember member = updatedMembers.get(index);
            jsonBuilder.append(member.toJson());
            if (index < updatedMembers.size() - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("]");
        DiskUtilities diskUtilities = new DiskUtilities();
        diskUtilities.txtWriter(jsonBuilder.toString(), "C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\HOCjson.json");
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
    }
}