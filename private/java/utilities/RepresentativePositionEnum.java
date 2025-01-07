package utilities;

public enum RepresentativePositionEnum {
    MP("MP"),
    MPP("MPP"),
    MNA("MNA"),
    ;

    private final String value;

    RepresentativePositionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
