package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogKeeper {
    private static LogKeeper instance = null;
    private static StringBuilder log;

    private LogKeeper() {
        log = new StringBuilder();
    }

    public static LogKeeper getInstance() {
        if (instance == null) {
            synchronized (LogKeeper.class) {
                if (instance == null) {
                    instance = new LogKeeper();
                }
            }
        }
        
        return instance;
    }

    public void appendLog(String message) {
        System.out.println(message);
        log.append(message).append(System.lineSeparator());
    }

    public void writeLogToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(log.toString());
        } catch (IOException e) {
            System.err.println("Error writing log to file: " + e.getMessage());
        }
    }

    public void clearLog() {
        log.setLength(0);
    }

    public String getLog() {
        return log.toString();
    }
}
