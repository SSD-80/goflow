package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import service.auth.IOAuthService;
import service.auth.OAuthServiceImpl;

import java.io.*;
import java.util.logging.Logger;

public class GoogleCallback extends HttpServlet {
    private static final Logger log = Logger.getLogger(GoogleCallback.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

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

        String role = (String) session.getAttribute("oauth_role");
        if (role == null) role = "Rider";

        IOAuthService oauthService = new OAuthServiceImpl();

        try {
            if ("Rider".equals(role)) {
                oauthService.handleRiderLogin(code, state, session);
                resp.sendRedirect(req.getContextPath() + "/AddRide?type=start");
            } else if ("Driver".equals(role)) {
                oauthService.handleDriverLogin(code, state, session);
                resp.sendRedirect(req.getContextPath() + "/DriverRinging");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth processing failed");
        }
    }

}
