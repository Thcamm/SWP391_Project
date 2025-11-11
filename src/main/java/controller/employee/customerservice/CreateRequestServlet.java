//package controller.employee.customerservice;
//
//
//import dao.carservice.CarServiceDAO;
//import dao.customer.CustomerDAO;
//import dao.user.UserDAO;
//import dao.vehicle.VehicleDAO;
//
//
//import model.customer.Customer;
//import model.servicetype.Service;
//import model.user.User;
//import model.vehicle.Vehicle;
//import model.workorder.ServiceRequest;
//import service.carservice.ServiceRequestService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.List;
//
//@WebServlet(name = "CustomerCreateRequestServlet", urlPatterns = {"/customerservice/createRequest"})
//public class CreateRequestServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession();
//        User cskhUser = (User) session.getAttribute("user");
//
//        if (cskhUser == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        String customerIdParam = request.getParameter("customerId");
//        if (customerIdParam == null || customerIdParam.isEmpty()) {
//            request.setAttribute("error", "Customer ID is required.");
//            request.getRequestDispatcher("/view/customerservice/error.jsp").forward(request, response);
//            return;
//        }
//
//        try {
//            int customerId = Integer.parseInt(customerIdParam);
//
//            CustomerDAO customerDAO = new CustomerDAO();
//            VehicleDAO vehicleDAO = new VehicleDAO();
//            CarServiceDAO carServiceDAO = new CarServiceDAO();
//
//            Customer customer = customerDAO.getCustomerById(customerId);
//            List<Vehicle> vehicles = vehicleDAO.getVehiclesByCustomerId(customerId);
//            List<Service> services = carServiceDAO.getAllServices();
//
//            if (customer == null) {
//                request.setAttribute("error", "Customer not found.");
//                request.getRequestDispatcher("/view/customerservice/error.jsp").forward(request, response);
//                return;
//            }
//
//            request.setAttribute("customer", customer);
//            request.setAttribute("vehicles", vehicles);
//            request.setAttribute("services", services);
//
//        } catch (SQLException | NumberFormatException e) {
//            e.printStackTrace();
//            request.setAttribute("error", "Could not load data for the form: " + e.getMessage());
//        }
//
//        request.getRequestDispatcher("/view/customerservice/createRequest.jsp").forward(request, response);
//    }
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession();
//
//        User cskhUser = (User) session.getAttribute("user");
//        if (cskhUser == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        String customerIdParam = request.getParameter("customerId");
//        String vehicleIdParam = request.getParameter("vehicleId");
//        String serviceIdParam = request.getParameter("serviceId");
//        String appointmentIdParam = request.getParameter("appointmentId");
//
//        try {
//            int customerId = Integer.parseInt(customerIdParam);
//            int vehicleId = Integer.parseInt(vehicleIdParam);
//            int serviceId = Integer.parseInt(serviceIdParam);
//
//            // 2. KHỞI TẠO ĐÚNG MODEL
//            ServiceRequest newRequest = new ServiceRequest();
//            newRequest.setCustomerID(customerId);
//            newRequest.setVehicleID(vehicleId);
//            newRequest.setServiceID(serviceId);
//
//            if (appointmentIdParam != null && !appointmentIdParam.isEmpty()) {
//                newRequest.setAppointmentID(Integer.parseInt(appointmentIdParam));
//            }
//
//            // 3. GỌI LOGIC TÌM TECH MANAGER
//            UserDAO userDAO = new UserDAO();
//            List<Integer> techManagerIds = userDAO.findUserIdsByRoleName("Tech Manager");
//
//            // 4. GỌI SERVICE
//            ServiceRequestService requestService = new ServiceRequestService();
//            boolean success = requestService.createRequestAndNotify(newRequest, techManagerIds);
//
//            // 5. REDIRECT
//            if (success) {
//                session.setAttribute("successMessage", "Service request created successfully!");
//            } else {
//                session.setAttribute("errorMessage", "Failed to create the service request.");
//            }
//            response.sendRedirect(request.getContextPath() + "/customerservice/search-customer");
//
//        } catch (SQLException | NumberFormatException e) {
//            e.printStackTrace();
//            session.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
//            response.sendRedirect(request.getContextPath() + "/customerservice/createRequest?customerId=" + customerIdParam);
//        }
//    }
//}