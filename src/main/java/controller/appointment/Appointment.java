package controller.appointment;

import dao.appointment.AppointmentDAO;
import dao.customer.CustomerDAO;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import model.vehicle.Vehicle;
import service.vehicle.VehicleService;
import util.MailService;

import java.io.IOException;

@WebServlet(name = "AppointmentService", urlPatterns = { "/customer/AppointmentService" })
public class Appointment extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response.setContentType("text/plain;charset=UTF-8");
        // response.getWriter().write("AppointmentService GET OK");
        request.getRequestDispatcher("/view/customer/appointment-scheduling.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: handle POST
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String fullName = (String) session.getAttribute("fullName");
        String phoneNumber = (String) session.getAttribute("phoneNumber");
        request.setAttribute("fullName", user.getUserName());
        request.setAttribute("phoneNumber", user.getPhoneNumber());

        // 1. Check if user is logged in
        if (user == null) {
            response.sendRedirect("login.jsp"); // Redirect to login page
            return; // Stop execution immediately
        }

        // 2. Get necessary parameters
        String carBrand = request.getParameter("carBrand");
        String licensePlate = request.getParameter("licensePlate");
        String dateStr = request.getParameter("appointmentDate");
        String description = request.getParameter("description");

        CustomerDAO customerDAO = new CustomerDAO();
        VehicleDAO vehicleDAO = new VehicleDAO(); // Cần có VehicleDAO
        AppointmentDAO appointmentDAO = new AppointmentDAO(); // Hoặc dùng Service
        VehicleService vehicleService = new VehicleService(vehicleDAO);


        try {
            // 4. Get CustomerID from UserID
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());
            if (customerID == -1) {
                // Handle case where user exists but has no record in Customer table
                // Example: Create new customer record then get ID
                throw new ServletException("Customer profile not found for the logged-in user.");
            }


            // 6. Handle Appointment
            model.appointment.Appointment appointment = new model.appointment.Appointment();
            appointment.setCustomerID(customerID);
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dateStr);
            appointment.setAppointmentDate(dateTime);
            appointment.setDescription(description);
            appointment.setStatus("PENDING"); // Set initial status

            // 7. Save to database
            appointmentDAO.insertAppointment(appointment);

            MailService.sendEmail(user.getEmail(),  "AppointmentService scheduled successfully","AppointmentService scheduled successfully");
            // 8. Redirect after success (PRG Pattern)
            request.setAttribute("successMessage", "AppointmentService scheduled successfully!");
            request.getRequestDispatcher("/view/customer/appointment-scheduling.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            // Catch error if date format is incorrect
            request.setAttribute("errorMessage", "Invalid date format. Please use YYYY-MM-DD.");
            request.getRequestDispatcher("/view/customer/appointment-scheduling.jsp").forward(request, response);
        } catch (Exception e) {
            // Catch other errors (DB errors, etc.)
            e.printStackTrace(); // Log error to console
            // Display detailed error on screen
            String detailError = "An error occurred: " + e.getMessage() + " | Type: " + e.getClass().getSimpleName();
            request.setAttribute("errorMessage", detailError);
            request.getRequestDispatcher("/view/customer/appointment-scheduling.jsp").forward(request, response);
        }
    }
}