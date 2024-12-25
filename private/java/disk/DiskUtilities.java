package disk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DiskUtilities {
    public void txtWriter(String txtToWrite, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(txtToWrite);
            System.out.println("Successfully wrote to the file: " + filePath);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
