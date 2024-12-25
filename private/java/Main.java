import java.util.List;

import api.HOCApiFetch;
import classes.HOCMember;
import csv.CSVReader;

public class Main{
    public static void main(String[] args) {
        CSVReader csvReader = new CSVReader();
        List<HOCMember> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\csv\\files\\export.csv");
        HOCApiFetch hocApiFetch = new HOCApiFetch();
        List<HOCMember> updatedMembers = hocApiFetch.fetchHOCMembersFromApi(members);

    }
}