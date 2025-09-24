package service.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpSession;
import model.Driver;
import model.Rider;
import service.driver.DriverServiceImpl;
import service.driver.IDriverService;
import service.rider.IRiderService;
import service.rider.RiderServiceImpl;
import util.OAuthConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuthServiceImpl implements IOAuthService{

    static { // static block
//        createCityTable();
    }

    public OAuthServiceImpl() { // default constructor
    }


    private String getAccessToken(String code) throws IOException {
        String body = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(OAuthConfig.CLIENT_ID, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(OAuthConfig.CLIENT_SECRET, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(OAuthConfig.REDIRECT_URI, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        URL url = new URL(OAuthConfig.TOKEN_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) responseBuilder.append(line);
        }

        JsonObject tokenJson = JsonParser.parseString(responseBuilder.toString()).getAsJsonObject();
        return tokenJson.get("access_token").getAsString();
    }

    private JsonObject getUserProfile(String accessToken) throws IOException {
        URL userInfoUrl = new URL(OAuthConfig.USERINFO_ENDPOINT);
        HttpURLConnection userInfoConn = (HttpURLConnection) userInfoUrl.openConnection();
        userInfoConn.setRequestProperty("Authorization", "Bearer " + accessToken);

        StringBuilder userInfoBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(userInfoConn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) userInfoBuilder.append(line);
        }

        return JsonParser.parseString(userInfoBuilder.toString()).getAsJsonObject();
    }

    @Override
    public Rider handleRiderLogin(String code, String state, HttpSession session) throws Exception {
        String accessToken = getAccessToken(code);
        JsonObject profile = getUserProfile(accessToken);

        String sub = profile.get("sub").getAsString();
        String email = profile.get("email").getAsString();
        String name = profile.has("name") ? profile.get("name").getAsString() : "Unknown";

        IRiderService riderService = new RiderServiceImpl();
        Rider rider = riderService.getRiderByOAuth("google", sub);

        if (rider == null) {
            rider = riderService.getRiderByEmail(email);
            if (rider != null && rider.getID() != 0) {
                riderService.linkRiderOAuthByEmail(email, "google", sub);
            } else {
                rider = new Rider();
                rider.setName(name);
                rider.setEmail(email);
                rider.setTel("");
                rider.setOauthProvider("google");
                rider.setOauthSub(sub);
                riderService.addOAuthRider(rider);
                rider = riderService.getRiderByEmail(email);
            }
        }

        session.setAttribute("username", email);
        session.setAttribute("id", rider.getID());
        session.setAttribute("role", "Rider");
        return rider;
    }

    @Override
    public Driver handleDriverLogin(String code, String state, HttpSession session) throws Exception {
        String accessToken = getAccessToken(code);
        JsonObject profile = getUserProfile(accessToken);

        String sub = profile.get("sub").getAsString();
        String email = profile.get("email").getAsString();
        String name = profile.has("name") ? profile.get("name").getAsString() : "Unknown";

        IDriverService driverService = new DriverServiceImpl();
        Driver driver = driverService.getDriverByOAuth("google", sub);

        if (driver == null) {
            driver = driverService.getDriverByEmail(email);
            if (driver != null && driver.getID() != 0) {
                driverService.linkDriverOAuthByEmail(email, "google", sub);
            } else {
                driver = new Driver();
                driver.setName(name);
                driver.setEmail(email);
                driver.setTel("");
                driver.setVehicleType(0); // default safe value
                driver.setOauthProvider("google");
                driver.setOauthSub(sub);
                driverService.addOAuthDriver(driver);
                driver = driverService.getDriverByEmail(email);
            }
        }

        session.setAttribute("username", email);
        session.setAttribute("id", driver.getID());
        session.setAttribute("vehicleType", driver.getVehicleType());
        session.setAttribute("role", "Driver");
        return driver;
    }
}
