package controller.employee.customerservice;

import dao.customer.CustomerDAO;
import dao.vehicle.CarDataDAO;
import dao.user.UserDAO;
import model.customer.Customer;
import model.user.User;
import model.vehicle.CarBrand;
import model.workorder.ServiceRequest;
import service.carservice.ServiceRequestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CustomerCreateRequestServlet", urlPatterns = { "/customerservice/createRequest" })
public class CreateServiceRequest extends HttpServlet {

    private final CarDataDAO carDataDAO = new CarDataDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        // Message từ session
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

        User cskhUser = (User) session.getAttribute("user");
        if (cskhUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String customerIdParam = request.getParameter("customerId");
        if (customerIdParam == null || customerIdParam.isEmpty()) {
            request.setAttribute("error", "Customer ID is required.");
            request.getRequestDispatcher("/view/customerservice/result.jsp").forward(request, response);
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdParam);

            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                request.setAttribute("error", "Customer not found.");
                request.getRequestDispatcher("/view/customerservice/result.jsp").forward(request, response);
                return;
            }
            request.setAttribute("customer", customer);

            // ===== LOAD BRANDS CHO MODAL =====
            try {
                List<CarBrand> brands = carDataDAO.getAllBrands();
                request.setAttribute("brands", brands);
                System.out.println("Loaded " + brands.size() + " brands"); // Debug log
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error loading brands: " + e.getMessage());
                request.setAttribute("brands", new ArrayList<>()); // Empty list để tránh null
            }
            // =================================

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load data: " + e.getMessage());
        }

        request.getRequestDispatcher("/view/customerservice/create-service-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String customerIdParam = request.getParameter("customerId");
        String appointmentIdParam = request.getParameter("appointmentId");
        String note = request.getParameter("note");
        if (customerIdParam == null || customerIdParam.isEmpty()) {
            session.setAttribute("message", "Customer ID is required.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customerservice/search-customer");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdParam);
            Integer appointmentId = (appointmentIdParam != null && !appointmentIdParam.isEmpty())
                    ? Integer.valueOf(appointmentIdParam)
                    : null;

            // Lấy tất cả service order dựa trên prefix name
            List<String> vehicleKeys = new ArrayList<>();
            List<String> serviceKeys = new ArrayList<>();
            request.getParameterMap().keySet().forEach(key -> {
                if (key.startsWith("vehicleIds"))
                    vehicleKeys.add(key);
                if (key.startsWith("serviceIds"))
                    serviceKeys.add(key);
            });

            if (vehicleKeys.isEmpty()) {
                session.setAttribute("message", "Vui lòng chọn xe cho ít nhất một Service Order.");
                session.setAttribute("messageType", "error");
                response.sendRedirect(request.getContextPath() + "/customerservice/createRequest?customerId="
                        + URLEncoder.encode(customerIdParam, StandardCharsets.UTF_8));
                return;
            }

            UserDAO userDAO = new UserDAO();
            List<Integer> techManagerIds = userDAO.findUserIdsByRoleName("Tech Manager");
            ServiceRequestService requestService = new ServiceRequestService();

            for (int i = 0; i < vehicleKeys.size(); i++) {
                String vehicleStr = request.getParameter(vehicleKeys.get(i));
                String[] serviceArr = i < serviceKeys.size() ? request.getParameterValues(serviceKeys.get(i)) : null;

                if (vehicleStr == null || vehicleStr.isEmpty())
                    continue;
                if (serviceArr == null || serviceArr.length == 0) {
                    session.setAttribute("message", "Vui lòng chọn dịch vụ cho Service Order #" + (i + 1));
                    session.setAttribute("messageType", "error");
                    response.sendRedirect(request.getContextPath() + "/customerservice/createRequest?customerId="
                            + URLEncoder.encode(customerIdParam, StandardCharsets.UTF_8));
                    return;
                }

                int vehicleId = Integer.parseInt(vehicleStr);
                List<Integer> serviceIds = new ArrayList<>();
                for (String s : serviceArr)
                    if (s != null && !s.isEmpty())
                        serviceIds.add(Integer.parseInt(s));
                if (serviceIds.isEmpty())
                    continue;

                ServiceRequest newRequest = new ServiceRequest();
                newRequest.setCustomerID(customerId);
                newRequest.setAppointmentID(appointmentId);
                newRequest.setVehicleID(vehicleId);
                newRequest.setNote(note);

                requestService.createRequestAndNotify(newRequest, serviceIds, techManagerIds);
            }

            session.setAttribute("message", "Service orders created successfully!");
            session.setAttribute("messageType", "success");
            response.sendRedirect(request.getContextPath() + "/customerservice/view-all-repairs");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("message", "Error: " + e.getMessage());
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customerservice/createRequest?customerId="
                    + URLEncoder.encode(customerIdParam, StandardCharsets.UTF_8));
        }
    }
}