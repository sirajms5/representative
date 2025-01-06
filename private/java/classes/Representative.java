package classes;

import java.util.List;

import com.google.gson.Gson;

public class Representative {
    private boolean isHonourable;
    private String firstName;
    private String lastName;
    private String constituency;
    private String provinceOrTerritory;
    private String politicalAffiliation; // con, lib ..
    private String startDate;
    private String endDate;
    private String position;
    private String photoUrl;// photo
    private String languages;
    private List<Office> offices;
    private List<String> roles;
    private String boundaryExternalId;
    private String level;
    private String email;
    private String url;
    private String fedUid;

    public Representative(String level) {
        this.level = level;
    }

    public Representative(String firstName, String lastName, String position, String level) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.level = level;
    }

    public Representative(boolean isHonourable, String firstName, String lastName,
                    String constituency, String provinceOrTerritory,
                    String politicalAffiliation, String startDate, String endDate, String position, String level) {
        this.isHonourable = isHonourable;
        this.firstName = firstName;
        this.lastName = lastName;
        this.constituency = constituency;
        this.provinceOrTerritory = provinceOrTerritory;
        this.politicalAffiliation = politicalAffiliation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
        this.level = level;
    }        

    public Representative(String honorificTitle, String firstName, String lastName, String constituency,
            String provinceOrTerritory, String politicalAffiliation, String startDate, String endDate, String position,
            String photoUrl, String languages, String boundaryExternalId, String level, String email, String url, boolean isHonourable) {
        this.isHonourable = isHonourable;
        this.firstName = firstName;
        this.lastName = lastName;
        this.constituency = constituency;
        this.provinceOrTerritory = provinceOrTerritory;
        this.politicalAffiliation = politicalAffiliation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
        this.photoUrl = photoUrl;
        this.languages = languages;
        this.boundaryExternalId = boundaryExternalId;
        this.level = level;
        this.email = email;
        this.url = url;    
        this.level = level;
    }

    public boolean isHonorificTitle() {
        return isHonourable;
    }

    public void setHonorificTitle(boolean isHonourable) {
        this.isHonourable = isHonourable;
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
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFedUid() {
        return fedUid;
    }

    public void setFedUid(String fedUid) {
        this.fedUid = fedUid;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
