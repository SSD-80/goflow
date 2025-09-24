package util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AccessControlFilter implements Filter {

    // Public URLs (no login required)
    private final String[] excludedUrls = {
            "/", "/index.jsp",
            "/Login", "/Register", "/Logout",
            "/AdminLogin", "/DriverLogin", "/RiderLogin",
            "/DriverRegister", "/RiderRegister",
            "/AddDriver", "/AddRider", "/oauth/google",
            "/oauth/callback",   // self-registration
            // static files
            "/public/css/styles.css", "/public/css/styles_home.css",
            "/public/js/scripts.js", "/public/js/location.js",
            "/public/js/validation/loginValidation.js",
            "/public/js/validation/rideValidation.js",
            "/public/images/GoFlow-Logo.png", "/public/images/GoFlow_White.png",
            "/public/images/Hero-img.jpg", "/public/images/map_icon.png",
            "/public/fonts/Gabarito/Gabarito-VariableFont_wght.ttf",
            "/public/images/web_dark_sq_ctn@1x.png"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Allow public URLs
        for (String excludedUrl : excludedUrls) {
            if (path.equals(excludedUrl)) {
                chain.doFilter(request, response);
                return;
            }
        }

        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        // If not logged in → redirect to login
        if (!isLoggedIn) {
            httpResponse.sendRedirect(contextPath + "/Login");
            return;
        }

        // ================================
        // Role-based Access Control
        // ================================

        // --- Admin-only ---
        if (path.equals("/AdminDashboard") ||
                path.equals("/AddVehicleType") || path.equals("/UpdateVehicleType") ||
                path.equals("/DeleteVehicleType") || path.equals("/ListVehicleType") ||
                path.equals("/GetVehicleType")) {

            if (!"Admin".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access only");
                return;
            }
        }

        // --- Rider-only ---
        if (path.equals("/ListRider") || path.equals("/GetRider") ||
                path.equals("/UpdateRider") || path.equals("/DeleteRider") ||
                path.equals("/RiderRideStatus") || path.equals("/RiderViewRideStatus")) {

            if (!"Rider".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Rider access only");
                return;
            }
        }

        // --- Driver-only ---
        if (path.equals("/UpdateDriver") || path.equals("/DeleteDriver") ||
                path.equals("/ListDriver") || path.equals("/GetDriver") ||
                path.equals("/DriverRideStatus") || path.equals("/DriverUpdateRideStatus") ||
                path.equals("/DriverRinging")) {

            if (!"Driver".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Driver access only");
                return;
            }
        }

        // --- Shared (Ride-related) ---
        if (path.equals("/AddRide") || path.equals("/DeleteRide") ||
                path.equals("/RidesHistory") || path.equals("/ListCity")) {

            if (!"Rider".equals(role) && !"Driver".equals(role) && !"Admin".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Ride access denied");
                return;
            }
        }

        // Passed all checks → continue to servlet
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {}
    @Override
    public void destroy() {}
}






//package util;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//
//@WebFilter("/*")
//public class AccessControlFilter implements Filter {
//
//private final String[] excludedUrls = {
//        "/", "/index.jsp",
//        "/Login", "/Register", "/Logout",
//        "/AdminLogin", "/DriverLogin", "/RiderLogin",
//        "/DriverRegister", "/RiderRegister",
//        "/AddDriver", "/AddRider",   // self-registration
//        // static files
//        "/public/css/styles.css", "/public/css/styles_home.css",
//        "/public/js/scripts.js", "/public/js/location.js",
//        // public
//        "/",
//        "/DriverLogin",
//        "/RiderLogin",
//        "/DriverRegister",
//        "/RiderRegister",
//        "/AddDriver",
//        "/AddRider",
//        "/Login",
//        "/Register",
//        "/AdminLogin",
//        "/index.jsp",
//
//        // OAuth endpoints
//        "/oauth/google",
//        "/oauth/callback",
//
//        // css
//        "/public/css/styles.css",
//        "/public/css/styles_home.css",
//
//        // scripts
//        "/public/js/scripts.js",
//        "/public/js/location.js",
//        "/public/js/validation/loginValidation.js",
//        "/public/js/validation/rideValidation.js",
//
//        // images
//        "/public/images/GoFlow-Logo.png",
//        "/public/images/GoFlow_White.png",
//        "/public/images/Hero-img.jpg",
//        "/public/images/map_icon.png",
//        "/public/images/web_dark_sq_ctn@1x.png",
//
//        // fonts
//        "/public/fonts/Gabarito/Gabarito-VariableFont_wght.ttf",
//        "/public/images/GoFlow-Logo.png", "/public/images/GoFlow_White.png",
//        "/public/images/Hero-img.jpg", "/public/images/map_icon.png",
//        "/public/fonts/Gabarito/Gabarito-VariableFont_wght.ttf"
//};
//
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        String requestURI = httpRequest.getRequestURI();
//        // Check if the request matches an excluded URL
//        for (String excludedUrl : excludedUrls) {
//            if (requestURI.matches(httpRequest.getContextPath() + excludedUrl)) {
//                chain.doFilter(request, response);  // Allow access
//                return;
//            }
//        }
//
//        HttpSession session = httpRequest.getSession(false);
//        String loginURI = httpRequest.getContextPath() + "/Login";
//
//        // Allow access to the login page without authentication
//        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
//        boolean isLoggedIn = session != null && session.getAttribute("username") != null;
//
//        if (isLoginRequest || isLoggedIn) {
//            chain.doFilter(request, response);
//        } else {
//            httpResponse.sendRedirect(httpRequest.getContextPath() + "/Login");
//        }
//    }
//
//    // Other methods for filter lifecycle management (init, destroy)
//    // These can be left empty for this example
//    public void init(FilterConfig fConfig) throws ServletException {}
//    public void destroy() {}
//}