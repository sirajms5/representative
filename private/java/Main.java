import jobs.HocBoundariesJob;
import jobs.HocBoundariesMultPolygonalJob;
import jobs.HocRepresentativesJob;
import utilities.LogKeeper;

public class Main{
    public static void main(String[] args) {

        LogKeeper logKeeper = LogKeeper.getInstance();
        logKeeper.appendLog("======================================== Starting Backend Jobs ========================================");
        HocRepresentativesJob hocRepresentativesJob = new HocRepresentativesJob();
        hocRepresentativesJob.executeHocRepresentativesJob();
        HocBoundariesJob hocBoundariesJob = new HocBoundariesJob();
        hocBoundariesJob.executeHocBoundariesJob();
        // HocBoundariesMultPolygonalJob hocBoundariesMultPolygonal = new HocBoundariesMultPolygonalJob();
        // hocBoundariesMultPolygonal.executeHocBoundariesMultiPolygonalJob();
        logKeeper.appendLog("======================================== Finished Backend Jobs ========================================");
        logKeeper.writeLogToFile("C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\logs\\backend-log.txt");
    }
}