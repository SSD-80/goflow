package controller.ride;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import service.ride.IRideService;
import service.ride.RideServiceImpl;
import model.Ride;

import java.io.IOException;

/**
 * Servlet implementation class CollectPayment
 */
public class RiderRideStatus extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RiderRideStatus() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/html");
//
//        int id = Integer.parseInt(request.getParameter("id"));
//
//        HttpSession session = request.getSession();
//        session.setAttribute("ride_id", id);
//
//        request.setAttribute("ride_id", id);
//
//        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/views/Ride/RiderRideStatus.jsp");
//        dispatcher.forward(request, response);
//
//    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id") == null) {
            response.sendRedirect(request.getContextPath() + "/Login"); // force login
            return;
        }

        try {
            int rideId = Integer.parseInt(request.getParameter("id")); // requested ride
            int sessionRiderId = (int) session.getAttribute("id");     // rider id from session
            String role = (String) session.getAttribute("role");       // role from session

            IRideService rideService = new RideServiceImpl();
            Ride ride = rideService.getRideByIdForRider(rideId, sessionRiderId, role); // secure check

            if (ride == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ride not found");
                return;
            }

            // safe: only if ownership passed
            session.setAttribute("ride_id", rideId);
            request.setAttribute("ride", ride); //  pass full ride object to JSP

            RequestDispatcher dispatcher = this.getServletContext()
                    .getRequestDispatcher("/WEB-INF/views/Ride/RiderRideStatus.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ride id"); //
        } catch (SecurityException se) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, se.getMessage()); // ownership failed
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
}
