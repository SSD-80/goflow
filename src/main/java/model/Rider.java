package model;

// This class is used to store rider information
public class Rider {

    // attributes
    private int id;
    private String name;
    private String email;
    private String password;
    private String tel;
    private String oauthProvider;
    private String oauthSub;

    // default constructor
    public Rider() {

    }

    // parameterized constructor (without OAuth)
    public Rider(int id, String name, String email, String password, String tel) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tel = tel;
    }

    // parameterized constructor (with OAuth)
    public Rider(int id, String name, String email, String password, String tel, String oauthProvider, String oauthSub) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tel = tel;
        this.oauthProvider = oauthProvider;
        this.oauthSub = oauthSub;
    }

    // getters and setters
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getOauthSub() {
        return oauthSub;
    }

    public void setOauthSub(String oauthSub) {
        this.oauthSub = oauthSub;
    }

    @Override
    public String toString() {
        return "Rider ID = " + this.id
                + "\nRider Name = " + this.name
                + "\nEmail = " + this.email
                + "\nTelephone = " + this.tel
                + (oauthProvider != null ? "\nOAuth Provider = " + this.oauthProvider : "")
                + (oauthSub != null ? "\nOAuth Sub = " + this.oauthSub : "");
    }
}
