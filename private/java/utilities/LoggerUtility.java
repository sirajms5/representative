package utilities;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtility {

    private static Logger logger;

    static {
        try {
            logger = Logger.getLogger(LoggerUtility.class.getName());

            FileHandler fileHandler = new FileHandler("C:\\xampp\\htdocs\\representative\\private\\java\\logs\\application_logs.log", true); 
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);

            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warning(message);
    }

    public static void logError(String message) {
        logger.severe(message);
    }
}

