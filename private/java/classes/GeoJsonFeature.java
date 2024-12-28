package classes;

public class GeoJsonFeature {
    
    private String type = "Feature";
    private MultiPolygon geometry;
    private Properties properties;

    public GeoJsonFeature(MultiPolygon geometry, Properties properties) {
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public MultiPolygon getGeometry() {
        return geometry;
    }

    public void setGeometry(MultiPolygon geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}

