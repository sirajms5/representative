package classes;

public class Properties {
    
    private String representative;
    private String constituency;
    private double centroidLatitude;
    private double centroidLongitude;

    public Properties(String representative, String constituency, double centroidLatitude, double centroidLongitude) {
        this.representative = representative;
        this.constituency = constituency;
        this.centroidLatitude = centroidLatitude;
        this.centroidLongitude = centroidLongitude;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public double getCentroidLatitude() {
        return centroidLatitude;
    }

    public void setCentroidLatitude(double centroidLatitude) {
        this.centroidLatitude = centroidLatitude;
    }

    public double getCentroidLongitude() {
        return centroidLongitude;
    }

    public void setCentroidLongitude(double centroidLongitude) {
        this.centroidLongitude = centroidLongitude;
    }
}

