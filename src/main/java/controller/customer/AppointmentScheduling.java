//package controller.customer;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import model.servicetype.Service;
//import dao.carservice.CarServiceDAO;
//import dao.customer.CustomerDAO;
////import service.carservice.ServiceRequestService;
//
//import java.io.IOException;
//
//@WebServlet(name = "AppointmentScheduling", urlPatterns = {"/AppointmentScheduling"})
//public class AppointmentScheduling extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//    request.getRequestDispatcher("/appointment-scheduling.jsp").forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//    String action = request.getParameter("action");
//    if (action.equals("logout")) {
//        request.getSession().invalidate();
//        response.sendRedirect("login.jsp");
//    }
//    }
//}
