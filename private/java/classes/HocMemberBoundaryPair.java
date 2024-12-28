package classes;

public class HocMemberBoundaryPair {

    private String fullName;
    private String constituency;
    private String provinceOrTerritory;
    private String politicalAffiliation;
    private String boundaryExternalId;
    private String simepleShapeUrl;
    
    public HocMemberBoundaryPair(String fullName, String constituency, String provinceOrTerritory,
            String politicalAffiliation, String boundaryExternalId, String simepleShapeUrl) {
        this.fullName = fullName;
        this.constituency = constituency;
        this.provinceOrTerritory = provinceOrTerritory;
        this.politicalAffiliation = politicalAffiliation;
        this.boundaryExternalId = boundaryExternalId;
        this.simepleShapeUrl = simepleShapeUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getProvinceOrTerritory() {
        return provinceOrTerritory;
    }

    public void setProvinceOrTerritory(String provinceOrTerritory) {
        this.provinceOrTerritory = provinceOrTerritory;
    }

    public String getPoliticalAffiliation() {
        return politicalAffiliation;
    }

    public void setPoliticalAffiliation(String politicalAffiliation) {
        this.politicalAffiliation = politicalAffiliation;
    }
    
    public String getBoundaryExternalId() {
        return boundaryExternalId;
    }

    public void setBoundaryExternalId(String boundaryExternalId) {
        this.boundaryExternalId = boundaryExternalId;
    }  

    public String getSimepleShapeUrl() {
        return simepleShapeUrl;
    }

    public void setSimepleShapeUrl(String simepleShapeUrl) {
        this.simepleShapeUrl = simepleShapeUrl;
    }  
}
