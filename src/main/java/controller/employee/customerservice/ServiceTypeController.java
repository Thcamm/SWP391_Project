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
import java.util.Set;
import java.util.stream.Collectors;

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

    // Hiển thị danh sách service types với tìm kiếm và phân trang
    private void listServices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy các tham số filter
        String searchQuery = request.getParameter("search");
        String categoryFilter = request.getParameter("category");
        String sortBy = request.getParameter("sort");

        // Lấy các tham số phân trang
        int page = 1;
        int pageSize = 10;

        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
            if (request.getParameter("pageSize") != null) {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            }
        } catch (NumberFormatException e) {
            page = 1;
            pageSize = 10;
        }

        // Lấy tất cả service types
        List<Service> allServices = serviceDAO.getAllServices();

        // Áp dụng tìm kiếm
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            final String search = searchQuery.toLowerCase().trim();
            allServices = allServices.stream()
                    .filter(s -> s.getServiceName().toLowerCase().contains(search) ||
                            s.getCategory().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        // Áp dụng lọc theo category
        if (categoryFilter != null && !categoryFilter.trim().isEmpty()) {
            final String category = categoryFilter.trim();
            allServices = allServices.stream()
                    .filter(s -> s.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        // Áp dụng sắp xếp
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "name_asc":
                    allServices.sort((s1, s2) -> s1.getServiceName().compareToIgnoreCase(s2.getServiceName()));
                    break;
                case "name_desc":
                    allServices.sort((s1, s2) -> s2.getServiceName().compareToIgnoreCase(s1.getServiceName()));
                    break;
                case "price_asc":
                    allServices.sort((s1, s2) -> Double.compare(s1.getPrice(), s2.getPrice()));
                    break;
                case "price_desc":
                    allServices.sort((s1, s2) -> Double.compare(s2.getPrice(), s1.getPrice()));
                    break;
            }
        }

        // Tính toán phân trang
        int totalServices = allServices.size();
        int totalPages = (int) Math.ceil((double) totalServices / pageSize);

        // Đảm bảo page trong phạm vi hợp lệ
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        // Lấy services cho trang hiện tại
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalServices);

        List<Service> paginatedServices = allServices.subList(startIndex, endIndex);

        // Lấy tất cả categories duy nhất cho dropdown filter
        Set<String> categories = serviceDAO.getAllServices().stream()
                .map(Service::getCategory)
                .collect(Collectors.toSet());

        // Set attributes
        request.setAttribute("services", paginatedServices);
        request.setAttribute("categories", categories);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalServices", totalServices);

        request.getRequestDispatcher("/view/customerservice/service-types.jsp").forward(request, response);
    }

    // Hiển thị form edit
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        Service service = serviceDAO.getServiceById(id);

        // Lấy các tham số filter hiện tại
        String searchQuery = request.getParameter("search");
        String categoryFilter = request.getParameter("category");
        String sortBy = request.getParameter("sort");
        int page = 1;
        int pageSize = 10;

        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
            if (request.getParameter("pageSize") != null) {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            }
        } catch (NumberFormatException e) {
            page = 1;
            pageSize = 10;
        }

        // Lấy tất cả service types và áp dụng filters
        List<Service> allServices = serviceDAO.getAllServices();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            final String search = searchQuery.toLowerCase().trim();
            allServices = allServices.stream()
                    .filter(s -> s.getServiceName().toLowerCase().contains(search) ||
                            s.getCategory().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        if (categoryFilter != null && !categoryFilter.trim().isEmpty()) {
            final String category = categoryFilter.trim();
            allServices = allServices.stream()
                    .filter(s -> s.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "name_asc":
                    allServices.sort((s1, s2) -> s1.getServiceName().compareToIgnoreCase(s2.getServiceName()));
                    break;
                case "name_desc":
                    allServices.sort((s1, s2) -> s2.getServiceName().compareToIgnoreCase(s1.getServiceName()));
                    break;
                case "price_asc":
                    allServices.sort((s1, s2) -> Double.compare(s1.getPrice(), s2.getPrice()));
                    break;
                case "price_desc":
                    allServices.sort((s1, s2) -> Double.compare(s2.getPrice(), s1.getPrice()));
                    break;
            }
        }

        int totalServices = allServices.size();
        int totalPages = (int) Math.ceil((double) totalServices / pageSize);

        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalServices);

        List<Service> paginatedServices = allServices.subList(startIndex, endIndex);

        Set<String> categories = serviceDAO.getAllServices().stream()
                .map(Service::getCategory)
                .collect(Collectors.toSet());

        request.setAttribute("services", paginatedServices);
        request.setAttribute("categories", categories);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalServices", totalServices);
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

        // Giữ lại filters khi redirect
        String redirectUrl = buildRedirectUrl(request);
        response.sendRedirect(redirectUrl);
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

        // Giữ lại filters khi redirect
        String redirectUrl = buildRedirectUrl(request);
        response.sendRedirect(redirectUrl);
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

        // Giữ lại filters khi redirect
        String redirectUrl = buildRedirectUrl(request);
        response.sendRedirect(redirectUrl);
    }

    // Helper method để build redirect URL với filters
    private String buildRedirectUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getContextPath() + "/customerservice/service-types");
        StringBuilder queryParams = new StringBuilder();

        String search = request.getParameter("search");
        String category = request.getParameter("category");
        String sort = request.getParameter("sort");
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");

        if (search != null && !search.trim().isEmpty()) {
            appendParam(queryParams, "search", search);
        }
        if (category != null && !category.trim().isEmpty()) {
            appendParam(queryParams, "category", category);
        }
        if (sort != null && !sort.trim().isEmpty()) {
            appendParam(queryParams, "sort", sort);
        }
        if (page != null && !page.trim().isEmpty()) {
            appendParam(queryParams, "page", page);
        }
        if (pageSize != null && !pageSize.trim().isEmpty()) {
            appendParam(queryParams, "pageSize", pageSize);
        }

        if (queryParams.length() > 0) {
            url.append("?").append(queryParams);
        }

        return url.toString();
    }

    // Helper method để append parameters
    private void appendParam(StringBuilder queryParams, String key, String value) {
        if (queryParams.length() > 0) {
            queryParams.append("&");
        }
        queryParams.append(key).append("=").append(value);
    }
}