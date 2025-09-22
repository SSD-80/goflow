package controller.auth;

import util.OAuthConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servlet implementation class GoogleAuth
 */
public class GoogleAuth extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GoogleAuth() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("DEBUG: Entered GoogleAuth.doGet");
        // Generate CSRF state token
        String state = UUID.randomUUID().toString();

        // inside doGet(), before building the URL:
        String role = req.getParameter("role");
        if (!"Driver".equalsIgnoreCase(role)) role = "Rider"; // default
        req.getSession().setAttribute("oauth_role", role);

        System.out.println("DEBUG: Role = " + role);
        System.out.println("DEBUG: State = " + state);

        // Collect query parameters
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", OAuthConfig.CLIENT_ID);
        params.put("redirect_uri", OAuthConfig.REDIRECT_URI);
        params.put("scope", OAuthConfig.SCOPE);
        params.put("state", state);

        // Build query string safely with URLEncoder
        String queryString = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        // Create the full authorization URL
        URI authUri = URI.create(OAuthConfig.AUTHZ_ENDPOINT + "?" + queryString);

        System.out.println("DEBUG: Redirecting to Google = " + authUri);

        req.getSession().setAttribute("oauth_state", state);
        System.out.println("DEBUG[Auth]: JSESSIONID = " + req.getSession().getId());
        System.out.println("DEBUG[Auth]: Saved state = " + state);


        // Redirect user to Google login
        resp.sendRedirect(authUri.toString());
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
