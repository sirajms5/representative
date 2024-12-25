package csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    public List<HOCMember> readCSV(String filePath) {
        List<HOCMember> members = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip the header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);

                // Handle cases where some columns might be empty
                String honorificTitle = values.length > 0 ? values[0] : "";
                String firstName = values.length > 1 ? values[1] : "";
                String lastName = values.length > 2 ? values[2] : "";
                String constituency = values.length > 3 ? values[3] : "";
                String provinceOrTerritory = values.length > 4 ? values[4] : "";
                String politicalAffiliation = values.length > 5 ? values[5] : "";
                String startDate = values.length > 6 ? values[6] : "";
                String endDate = values.length > 7 ? values[7] : "";

                members.add(new HOCMember(honorificTitle, firstName, lastName, constituency,
                        provinceOrTerritory, politicalAffiliation, startDate, endDate));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return members;
    }
}
