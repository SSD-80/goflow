package model;

// This class is used to store driver information
public class Driver {

    // attributes
    private int id;
    private String name;
    private String email;
    private int vehicleType;
    private String password;
    private String tel;
    private String oauthProvider;
    private String oauthSub;

    // default constructor
    public Driver() {}

    // parameterized constructor (without OAuth)
    public Driver(int id, String name, String email, String password, String tel) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tel = tel;
    }

    // parameterized constructor (with OAuth)
    public Driver(int id, String name, String email, int vehicleType, String password, String tel,
                  String oauthProvider, String oauthSub) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.vehicleType = vehicleType;
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

    public int getVehicleType() {
        return this.vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
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

    // toString method
    @Override
    public String toString() {
        return "Driver ID = " + this.id
                + "\nDriver Name = " + this.name
                + "\nEmail = " + this.email
                + "\nTelephone = " + this.tel
                + "\nVehicle Type = " + this.vehicleType
                + (oauthProvider != null ? "\nOAuth Provider = " + this.oauthProvider : "")
                + (oauthSub != null ? "\nOAuth Sub = " + this.oauthSub : "");
    }
}
