package classes;

import com.google.gson.Gson;

public class Boundaries {

    private String externalId;
    private String name;
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;
    private double centroidLatitude;
    private double centroidLogitude;
    private MultiPolygon multiPolygon;

    public Boundaries(String externalId, String name, double minLatitude, double maxLatitude, double minLongitude,
            double maxLongitude, double centroidLatitude, double centroidLogitude, MultiPolygon multiPolygon) {
        this.externalId = externalId;
        this.name = name;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
        this.centroidLatitude = centroidLatitude;
        this.centroidLogitude = centroidLogitude;
        this.multiPolygon = multiPolygon;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getCentroidLatitude() {
        return centroidLatitude;
    }

    public void setCentroidLatitude(double centroidLatitude) {
        this.centroidLatitude = centroidLatitude;
    }

    public double getCentroidLogitude() {
        return centroidLogitude;
    }

    public void setCentroidLogitude(double centroidLogitude) {
        this.centroidLogitude = centroidLogitude;
    }

    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }
    
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
