import java.util.List;

import csv.CSVReader;
import csv.HOCMember;

public class Main{
    public static void main(String[] args) {
        CSVReader csvReader = new CSVReader();
        List<HOCMember> members = csvReader.readCSV("C:\\xampp\\htdocs\\representative\\private\\java\\csv\\files\\export.csv");

        for (HOCMember member : members) {
            System.out.println(member);
        }
    }
}