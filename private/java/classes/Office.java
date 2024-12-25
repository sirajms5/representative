package classes;

public class Office {
    private String fax;
    private String tel;
    private String type;
    private String postal;

    public Office(String fax, String tel, String type, String postal) {
        this.fax = fax;
        this.tel = tel;
        this.type = type;
        this.postal = postal;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }
}
