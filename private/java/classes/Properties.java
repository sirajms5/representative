package classes;

public class Properties {
    
    private String representative;
    private String constituency;
    private String politicalAffiliation;

    public Properties(String representative, String constituency, String politicalAffiliation) {
        this.representative = representative;
        this.constituency = constituency;
        this.politicalAffiliation = politicalAffiliation;
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
}

