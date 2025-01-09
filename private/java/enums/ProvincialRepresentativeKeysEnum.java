package enums;

public enum ProvincialRepresentativeKeysEnum {
    ON("Ontario"),
    QC("Quebec"),
    YT("Yukon"),
    NT("Northwest Territories"),
    MB("Manitoba"),
    BC("British Columbia"),
    PE("Prince Edward Island"),
    NS("Nova Scotia"),
    NB("New Brunswick"),
    SK("Saskatchewan"),
    AB("Alberta"),
    NL("Newfoundland and Labrador"),
    ;

    private final String value;

    ProvincialRepresentativeKeysEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getProvincialOrTerritorialByKey(String key) {
        switch (key) {
            case "assembly.ab.ca":
                return AB.getValue();
            case "assembly.pe.ca":
                return PE.getValue();
            case "ola.org":
                return ON.getValue();
            case "yukonassembly.ca":
                return YT.getValue();
            case "ntlegislativeassembly.ca":
                return NT.getValue();
            case "gov.mb.ca":
                return MB.getValue();
            case "leg.bc.ca":
                return BC.getValue();
            case "nslegislature.ca":
                return NS.getValue();
            case "legnb.ca":
                return NB.getValue();
            case "legassembly.sk.ca":
                return SK.getValue();
            case "assembly.nl.ca":
                return NL.getValue();
            default:
                return "";
        }
    }
}
