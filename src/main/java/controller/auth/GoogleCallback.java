package controller.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Rider;
import service.rider.IRiderService;
import service.rider.RiderServiceImpl;
import util.OAuthConfig;

import model.Driver;
import service.driver.IDriverService;
import service.driver.DriverServiceImpl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class GoogleCallback extends HttpServlet {
    private static final Logger log = Logger.getLogger(GoogleCallback.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // --- CSRF check ---
        String state = req.getParameter("state");
        String expectedState = (String) session.getAttribute("oauth_state");
        if (expectedState == null || !expectedState.equals(state)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid state parameter");
            return;
        }

        String code = req.getParameter("code");
        if (code == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing authorization code");
            return;
        }

        // --- Exchange code for tokens ---
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
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line);
            }
        }

        JsonObject tokenJson = JsonParser.parseString(responseBuilder.toString()).getAsJsonObject();
        String accessToken = tokenJson.get("access_token").getAsString();

        // --- Fetch user info ---
        URL userInfoUrl = new URL(OAuthConfig.USERINFO_ENDPOINT);
        HttpURLConnection userInfoConn = (HttpURLConnection) userInfoUrl.openConnection();
        userInfoConn.setRequestProperty("Authorization", "Bearer " + accessToken);

        StringBuilder userInfoBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(userInfoConn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                userInfoBuilder.append(line);
            }
        }

        JsonObject profile = JsonParser.parseString(userInfoBuilder.toString()).getAsJsonObject();
        String sub = profile.get("sub").getAsString();
        String email = profile.get("email").getAsString();
        String name = profile.has("name") ? profile.get("name").getAsString() : "Unknown";

        // --- Decide role (from session set in GoogleAuth) ---
        String role = (String) session.getAttribute("oauth_role");
        if (role == null) role = "Rider"; // default fallback

        if ("Rider".equals(role)) {
            IRiderService riderService = new RiderServiceImpl();
            Rider rider = riderService.getRiderByOAuth("google", sub);

            if (rider == null) {
                // If rider exists by email → link
                rider = riderService.getRiderByEmail(email);
                if (rider != null && rider.getID() != 0) {
                    riderService.linkRiderOAuthByEmail(email, "google", sub);
                } else {
                    // Brand new rider
                    rider = new Rider();
                    rider.setName(name);
                    rider.setEmail(email);
                    rider.setTel(""); // default empty
                    rider.setOauthProvider("google");
                    rider.setOauthSub(sub);
                    riderService.addOAuthRider(rider);
                    rider = riderService.getRiderByEmail(email); // fetch with ID
                }
            }

            // session
            session.setAttribute("username", email);
            session.setAttribute("id", rider.getID());
            session.setAttribute("role", "Rider");

            resp.sendRedirect(req.getContextPath() + "/AddRide?type=start");
        } else if ("Driver".equals(role)) {
            IDriverService driverService = new DriverServiceImpl();
            Driver driver = driverService.getDriverByOAuth("google", sub);

            if (driver == null) {
                // If driver exists by email → link
                driver = driverService.getDriverByEmail(email);
                if (driver != null && driver.getID() != 0) {
                    driverService.linkDriverOAuthByEmail(email, "google", sub);
                } else {
                    // Brand new driver
                    driver = new Driver();
                    driver.setName(name);
                    driver.setEmail(email);
                    driver.setTel(""); // default empty
                    driver.setVehicleType(Integer.parseInt("4")); // default or assign properly
                    driver.setOauthProvider("google");
                    driver.setOauthSub(sub);
                    driverService.addOAuthDriver(driver);
                    driver = driverService.getDriverByEmail(email); // fetch with ID
                }
            }

            // session
            session.setAttribute("username", email);
            session.setAttribute("id", driver.getID());
            session.setAttribute("vehicleType", driver.getVehicleType());
            session.setAttribute("role", "Driver");

            resp.sendRedirect(req.getContextPath() + "/DriverRinging");
        }
    }
}
