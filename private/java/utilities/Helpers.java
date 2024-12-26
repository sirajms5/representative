package utilities;

public class Helpers {
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            // LoggerUtility.logError(e.getMessage());
            System.out.println(e.getMessage());
        }
    }
}
