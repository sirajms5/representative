package enums;

public enum RepresentativeLevelEnum {
    FEDERAL("Federal"),
    PROVINCIAL("Provincial"),
    TERRITORIAL("Territorial"),
    MUNICIPAL("Municipal");

    private final String value;

    RepresentativeLevelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getProvincialOrTerritorial(String region) {
        switch (region) {
            case "Ontario":
            case "Quebec":
            case "Nova Scotia":
            case "New Brunswick":
            case "Manitoba":
            case "British Columbia":
            case "Prince Edward Island":
            case "Saskatchewan":
            case "Alberta":
            case "Newfoundland and Labrador":
                return PROVINCIAL.getValue();

            case "Yukon":
            case "Northwest Territories":
            case "Nunavut":
                return TERRITORIAL.getValue();

            default:
                return MUNICIPAL.getValue();
        }
    }
}
