package classes;

import java.util.List;

public class MultiPolygon {

    private final String type = "MultiPolygon";
    private List<List<List<List<Double>>>> coordinates;

    public MultiPolygon(List<List<List<List<Double>>>> coordinates) {
        this.coordinates = coordinates;
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
}
