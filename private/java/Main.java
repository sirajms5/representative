import jobs.HocJob;
import utilities.LogKeeper;

public class Main{
    public static void main(String[] args) {

        LogKeeper logKeeper = LogKeeper.getInstance();
        logKeeper.appendLog("======================================== Starting Backend Jobs ========================================");
        HocJob hocJob = new HocJob();
        hocJob.executeHocJob();
        logKeeper.appendLog("======================================== Finished Backend Jobs ========================================");
        logKeeper.writeLogToFile("C:\\xampp\\htdocs\\representative\\private\\java\\logs\\backend-log.txt");
    }
}