package utilities;

public enum ProvincialRepresentativeKeysEnum {
    OLA("Ontario"),
    ASSNAT("Quebec"),
    BRITISH_COLUMBIA("bc"),
    ALBERTA("ab"),
    MANITOBA("mb");

    private final String value;

    ProvincialRepresentativeKeysEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getProvincialOrTerritorialByKey(String key) {
        switch (key) {
            case "OLA":
                return OLA.getValue();

            default:
                return null;
        }
    }
}
