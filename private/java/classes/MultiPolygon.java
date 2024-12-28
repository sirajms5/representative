package classes;

import java.util.List;

public class MultiPolygon {

    private final String type = "MultiPolygon";
    private List<List<List<List<Double>>>> coordinates;
    private String boundaryExternalId;

    public MultiPolygon(List<List<List<List<Double>>>> coordinates, String boundaryExternalId) {
        this.coordinates = coordinates;
        this.boundaryExternalId = boundaryExternalId;
    }

    public String getType() {
        return type;
    }

    public List<List<List<List<Double>>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<List<Double>>>> coordinates) {
        this.coordinates = coordinates;
    }

    public String getBoundaryExternalId() {
        return boundaryExternalId;
    }

    public void setBoundaryExternalId(String boundaryExternalId) {
        this.boundaryExternalId = boundaryExternalId;
    }    
}
