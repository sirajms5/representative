package classes;

import java.util.List;

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
    private List<String> languages;
    private List<Office> offices;
    private List<String> roles;
    private String boundaryExternalId;

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

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
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

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"honorificTitle\":\"").append(honorificTitle).append("\",");
        json.append("\"firstName\":\"").append(firstName).append("\",");
        json.append("\"lastName\":\"").append(lastName).append("\",");
        json.append("\"constituency\":\"").append(constituency).append("\",");
        json.append("\"provinceOrTerritory\":\"").append(provinceOrTerritory).append("\",");
        json.append("\"politicalAffiliation\":\"").append(politicalAffiliation).append("\",");
        json.append("\"startDate\":\"").append(startDate).append("\",");
        json.append("\"endDate\":\"").append(endDate).append("\",");
        json.append("\"position\":\"").append(position).append("\",");
        json.append("\"photoUrl\":\"").append(photoUrl).append("\",");
    
        // Add languages
        json.append("\"languages\":[");
        if (languages != null && !languages.isEmpty()) {
            for (int i = 0; i < languages.size(); i++) {
                json.append("\"").append(languages.get(i)).append("\"");
                if (i < languages.size() - 1) json.append(",");
            }
        }
        json.append("],");
    
        // Add offices
        json.append("\"offices\":[");
        if (offices != null && !offices.isEmpty()) {
            for (int i = 0; i < offices.size(); i++) {
                Office office = offices.get(i);
                json.append("{");
                json.append("\"fax\":\"").append(office.getFax()).append("\",");
                json.append("\"tel\":\"").append(office.getTel()).append("\",");
                json.append("\"type\":\"").append(office.getType()).append("\",");
                json.append("\"postal\":\"").append(office.getPostal()).append("\"");
                json.append("}");
                if (i < offices.size() - 1) json.append(",");
            }
        }
        json.append("],");
    
        // Add roles
        json.append("\"roles\":[");
        if (roles != null && !roles.isEmpty()) {
            for (int i = 0; i < roles.size(); i++) {
                json.append("\"").append(roles.get(i)).append("\"");
                if (i < roles.size() - 1) json.append(",");
            }
        }
        json.append("],");
    
        // Add boundaryExternalId
        json.append("\"boundaryExternalId\":\"").append(boundaryExternalId).append("\"");
        json.append("}");
    
        return json.toString();
    }    
}
