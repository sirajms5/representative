package classes;

import com.google.gson.Gson;

public class Boundary {

    private String externalId;
    private String boundaryName;
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;
    private String simepleShapeUrl;

    public Boundary(String externalId, String boundaryName, double minLatitude, double maxLatitude, double minLongitude,
            double maxLongitude, String simepleShapeUrl) {
        this.externalId = externalId;
        this.boundaryName = boundaryName;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
        this.simepleShapeUrl = simepleShapeUrl;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getBoundaryName() {
        return boundaryName;
    }

    public void setBoundaryName(String boundaryName) {
        this.boundaryName = boundaryName;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public String getSimepleShapeUrl() {
        return simepleShapeUrl;
    }

    public void setSimepleShapeUrl(String simepleShapeUrl) {
        this.simepleShapeUrl = simepleShapeUrl;
    }
    
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
