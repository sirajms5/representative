package disk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import utilities.LogKeeper;

public class DiskUtilities {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void txtWriter(String txtToWrite, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(txtToWrite);
            logKeeper.appendLog("Successfully wrote to the file: " + filePath);
        } catch (IOException e) {
            logKeeper.appendLog("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
