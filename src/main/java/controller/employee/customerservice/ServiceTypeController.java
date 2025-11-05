package controller.employee.customerservice;

import model.servicetype.Service;
import dao.carservice.ServiceDAO;

import dao.carservice.ServiceRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/customerservice/service-types")
public class ServiceTypeController extends HttpServlet {

    private ServiceDAO serviceDAO;

    @Override
    public void init() throws ServletException {
        serviceDAO = new ServiceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    listServices(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteService(request, response);
                    break;
                default:
                    listServices(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addService(request, response);
            } else if ("update".equals(action)) {
                updateService(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Hiển thị danh sách service types
    private void listServices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Service> services = serviceDAO.getAllServices();
        request.setAttribute("services", services);
        request.getRequestDispatcher("/view/customerservice/service-types.jsp").forward(request, response);
    }

    // Hiển thị form edit
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        Service service = serviceDAO.getServiceById(id);

        List<Service> services = serviceDAO.getAllServices();
        request.setAttribute("services", services);
        request.setAttribute("editService", service);
        request.getRequestDispatcher("/view/customerservice/service-types.jsp").forward(request, response);
    }

    // Thêm service type mới
    private void addService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String serviceName = request.getParameter("serviceName");
        String category = request.getParameter("category");
        double price = Double.parseDouble(request.getParameter("price"));

        Service service = new Service();
        service.setServiceName(serviceName);
        service.setCategory(category);
        service.setPrice(price);

        boolean success = serviceDAO.addService(service);

        if (success) {
            request.getSession().setAttribute("message", "Service type added successfully!");
            request.getSession().setAttribute("messageType", "success");
        } else {
            request.getSession().setAttribute("message", "Failed to add service type!");
            request.getSession().setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/customerservice/service-types");
    }

    // Cập nhật service type
    private void updateService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("serviceTypeId"));
        String serviceName = request.getParameter("serviceName");
        String category = request.getParameter("category");
        double price = Double.parseDouble(request.getParameter("price"));

        Service service = new Service();
        service.setServiceTypeID(id);
        service.setServiceName(serviceName);
        service.setCategory(category);
        service.setPrice(price);

        boolean success = serviceDAO.updateService(service);

        if (success) {
            request.getSession().setAttribute("message", "Service type updated successfully!");
            request.getSession().setAttribute("messageType", "success");
        } else {
            request.getSession().setAttribute("message", "Failed to update service type!");
            request.getSession().setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/customerservice/service-types");
    }

    // Xóa service type
    private void deleteService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = serviceDAO.deleteService(id);

        if (success) {
            request.getSession().setAttribute("message", "Service type deleted successfully!");
            request.getSession().setAttribute("messageType", "success");
        } else {
            request.getSession().setAttribute("message", "Failed to delete service type!");
            request.getSession().setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/customerservice/service-types");
    }
}