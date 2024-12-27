package utilities;

public class Helpers {

    public static void sleep(int seconds) {
        
        LogKeeper logKeeper = LogKeeper.getInstance();

        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            logKeeper.appendLog(e.getMessage());
        }
    }
}
