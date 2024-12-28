package classes;

import java.util.List;

import com.google.gson.Gson;

public class GeoJsonFeatureCollection {

    private String type = "FeatureCollection";
    private List<GeoJsonFeature> features;

    public GeoJsonFeatureCollection(List<GeoJsonFeature> features) {
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public List<GeoJsonFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoJsonFeature> features) {
        this.features = features;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
