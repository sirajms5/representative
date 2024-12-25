package classes;

import java.util.List;

import com.google.gson.Gson;

public class HOCMember {
    private String honorificTitle;
    private String firstName;
    private String lastName;
    private String constituency;
    private String provinceOrTerritory;
    private String politicalAffiliation; // con, lib ..
    private String startDate;
    private String endDate;
    private String position = "MP";
    private String photoUrl;// photo
    private String languages;
    private List<Office> offices;
    private List<String> roles;
    private String boundaryExternalId;
    private String level = "Federal";

    // Constructor
    public HOCMember(String honorificTitle, String firstName, String lastName,
                    String constituency, String provinceOrTerritory,
                    String politicalAffiliation, String startDate, String endDate) {
        this.honorificTitle = honorificTitle;
        this.firstName = firstName;
        this.lastName = lastName;
        this.constituency = constituency;
        this.provinceOrTerritory = provinceOrTerritory;
        this.politicalAffiliation = politicalAffiliation;
        this.startDate = startDate;
        this.endDate = endDate;
    }    

    public String getHonorificTitle() {
        return honorificTitle;
    }

    public void setHonorificTitle(String honorificTitle) {
        this.honorificTitle = honorificTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }    

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public List<Office> getOffices() {
        return offices;
    }

    public void setOffices(List<Office> offices) {
        this.offices = offices;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getBoundaryExternalId() {
        return boundaryExternalId;
    }

    public void setBoundaryExternalId(String boundaryExternalId) {
        this.boundaryExternalId = boundaryExternalId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }    

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
