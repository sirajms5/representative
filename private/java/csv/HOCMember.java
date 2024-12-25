package csv;
public class HOCMember {
    private String honorificTitle;
    private String firstName;
    private String lastName;
    private String constituency;
    private String provinceOrTerritory;
    private String politicalAffiliation;
    private String startDate;
    private String endDate;

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

    // Getters and toString()
    public String getHonorificTitle() {
        return honorificTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getConstituency() {
        return constituency;
    }

    public String getProvinceOrTerritory() {
        return provinceOrTerritory;
    }

    public String getPoliticalAffiliation() {
        return politicalAffiliation;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "Member{" +
                "honorificTitle='" + honorificTitle + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", constituency='" + constituency + '\'' +
                ", provinceOrTerritory='" + provinceOrTerritory + '\'' +
                ", politicalAffiliation='" + politicalAffiliation + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }    
}
