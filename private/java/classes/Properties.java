package classes;

public class Properties {
    
    private String representative;
    private String constituency;
    private String politicalAffiliation;
    private String provinceOrTerritory;
    private String boundaryExternalId;

    public Properties(String representative, String constituency, String politicalAffiliation, String provinceOrTerritory, String boundaryExternalId) {
        this.representative = representative;
        this.constituency = constituency;
        this.politicalAffiliation = politicalAffiliation;
        this.provinceOrTerritory = provinceOrTerritory;
        this.boundaryExternalId = boundaryExternalId;
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

    public String getPoliticalAffiliation() {
        return politicalAffiliation;
    }

    public void setPoliticalAffiliation(String politicalAffiliation) {
        this.politicalAffiliation = politicalAffiliation;
    }

    public String getProvinceOrTerritory() {
        return provinceOrTerritory;
    }

    public void setProvinceOrTerritory(String provinceOrTerritory) {
        this.provinceOrTerritory = provinceOrTerritory;
    }

    public String getBoundaryExternalId() {
        return boundaryExternalId;
    }

    public void setBoundaryExternalId(String boundaryExternalId) {
        this.boundaryExternalId = boundaryExternalId;
    }
}

