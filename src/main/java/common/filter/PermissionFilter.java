package common.filter;

import dao.employee.admin.rbac.RoleDao;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.auth.AuthService;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PermissionFilter implements Filter {
    private AuthService auth;
    private Map<String, String> routePerm;
    private Map<String, String> areaGate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.auth = new AuthService(new RoleDao());
        this.routePerm = new HashMap<>();
        this.areaGate = new HashMap<>();

        // RBAC (Admin)
        routePerm.put("GET:/view/role/rbac.jsp", "role_permission_manage");
        routePerm.put("GET:/view/role/list.jsp", "role_permission_manage");
        routePerm.put("GET:/view/role/permission_form.jsp", "role_permission_manage");
        routePerm.put("POST:/view/role/permission_form.jsp", "role_permission_manage");
        routePerm.put("GET:/view/role/form.jsp", "role_permission_manage");
        routePerm.put("POST:/view/role/form.jsp", "role_permission_manage");

        // User (Admin) - Updated with new separated routes
        // routePerm.put("GET:/admin/users", "admin");
        // routePerm.put("POST:/admin/users", "user_read"); // For search functionality
        // routePerm.put("GET:/admin/users/create", "user_create");
        // routePerm.put("POST:/admin/users/create", "user_create");
        // routePerm.put("GET:/admin/users/choose-type", "user_create");
        // routePerm.put("GET:/admin/users/create-customer", "user_create");
        // routePerm.put("POST:/admin/users/create-customer", "user_create");
        // routePerm.put("GET:/admin/users/create-employee", "user_create");
        // routePerm.put("POST:/admin/users/create-employee", "user_create");
        // Note: Dynamic routes like /admin/users/view/{id} will be handled by area gate

        // Admin JSP pages
        routePerm.put("GET:/view/admin/users.jsp", "role_permission_manage");
        routePerm.put("GET:/view/admin/create-user.jsp", "user_create");
        routePerm.put("GET:/view/admin/choose-user-type.jsp", "user_create");
        routePerm.put("GET:/view/admin/create-customer.jsp", "user_create");
        routePerm.put("GET:/view/admin/create-employee.jsp", "user_create");
        routePerm.put("GET:/view/admin/user-details.jsp", "role_permission_manage");
        routePerm.put("GET:/view/admin/user-edit.jsp", "role_permission_manage");

        // Customer Service
        routePerm.put("GET:/view/customerservice/customerservice-home.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/customerservice-home.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/appointment-list.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/appointment-list.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/search-customer.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/search-customer.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/create-customer.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/create-customer.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/customer-detail.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/customer-detail.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/support-request-list.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/support-request-list.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/reply-request.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/reply-request.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/support-detail.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/createRequest.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/createRequest.jsp", "cs_access");
        routePerm.put("GET:/view/customerservice/serviceRequest.jsp", "cs_access");
        routePerm.put("POST:/view/customerservice/serviceRequest.jsp", "cs_access");

        // Tech manager - chỉ dashboard, workorders được forward đến WorkOrderController
        routePerm.put("GET:/techmanager/home", "tech_manager_access");


        // WorkOrder management - tất cả operations được xử lý bởi WorkOrderController
        routePerm.put("GET:/view/techmanager/home.jsp", "tech_manager_access");
        routePerm.put("GET:/view/techmanager/home.jsp", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/home.jsp", "tech_manager_access");
        routePerm.put("GET:/view/techmanager/service-requests.jsp", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/service-requests.jsp", "tech_manager_access");
        routePerm.put("GET:/view/techmanager/workorders/list", "tech_manager_access");
        routePerm.put("GET:/view/workorders/create.jsp", "tech_manager_access");
        routePerm.put("POST:/view/workorders/create.jsp", "tech_manager_access");
        routePerm.put("GET:/view/workorders/details.jsp", "tech_manager_access");
        routePerm.put("POST:/view/workorders/add-detail.jsp", "tech_manager_access");
        routePerm.put("GET:/view/workorders/edit-detail.jsp", "tech_manager_access");
        routePerm.put("POST:/view/workorders/edit-detail.jsp", "tech_manager_access");
        routePerm.put("POST:/view/workorders/delete-detail.jsp", "tech_manager_access");
        routePerm.put("POST:/view/workorders/approve-detail.jsp", "tech_manager_access");
        routePerm.put("POST:/view/workorders/decline-detail.jsp", "tech_manager_access");
        routePerm.put("GET:/view/techmanager/workorders/create", "tech_manager_access");
        routePerm.put("GET:/view/workorders/list.jsp", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/workorders/create", "tech_manager_access");
        routePerm.put("GET:/view/techmanager/workorders/details", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/workorders/add-detail", "tech_manager_access");
        routePerm.put("GET:/view/techmanager/workorders/edit-detail", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/workorders/edit-detail", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/workorders/delete-detail", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/workorders/approve-detail", "tech_manager_access");
        routePerm.put("POST:/view/techmanager/workorders/decline-detail", "tech_manager_access");
        routePerm.put("GET:/view/error.jsp", "tech_manager_access");
        // Technician
        routePerm.put("GET:/view/technician/home.jsp", "technician_access");
        routePerm.put("GET:/view/technician/tasks.jsp", "technician_access");

        // Storekeeper (Inventory)
        routePerm.put("GET:/view/storekepper/inventory-list.jsp", "inventory_access");
        routePerm.put("GET:/view/storekepper/inventory-import.jsp", "inventory_access");

        // Accountant
        routePerm.put("GET:/view/accountant/dashboard.jsp", "accountant_access");
        routePerm.put("POST:/view/accountant/dashboard.jsp", "accountant_access");
        routePerm.put("GET:/view/invoice/invoiceDetails.jsp", "accountant_access");
        routePerm.put("POST:/view/invoice/invoiceDetails.jsp", "accountant_access");
        routePerm.put("GET:/view/accountant/recordPayment.jsp", "accountant_access");
        routePerm.put("POST:/view/accountant/recordPayment.jsp", "accountant_access");


        // Customer
        routePerm.put("GET:/view/customer/appointment-scheduling.jsp", "customer_access");
        routePerm.put("POST:/view/customer/appointment-scheduling.jsp", "customer_access");
        routePerm.put("GET:/view/customer/garage.jsp", "customer_access");
        routePerm.put("POST:/view/customer/garage.jsp", "customer_access");
        routePerm.put("GET:/view/customer/addVehicle.jsp", "customer_access");
        routePerm.put("POST:/view/customer/addVehicle.jsp", "customer_access");
        routePerm.put("GET:/view/customer/editVehicle.jsp", "customer_access");
        routePerm.put("POST:/view/customer/editVehicle.jsp", "customer_access");
        routePerm.put("GET:/view/customer/create-support-request.jsp", "customer_access");
        routePerm.put("POST:/view/customer/create-support-request.jsp", "customer_access");
        routePerm.put("GET:/view/customer/appointment-history.jsp", "customer_access");
        routePerm.put("POST:/view/customer/appointment-history.jsp", "customer_access");
        routePerm.put("GET:/view/customer/customer-home.jsp", "customer_access");
        routePerm.put("POST:/view/customer/customer-home.jsp", "customer_access");





        // User
        routePerm.put("GET:/view/user/viewProfile.jsp", "user_access");
        routePerm.put("POST:/view/user/viewProfile.jsp", "user_access");
        routePerm.put("GET:/view/user/editProfile.jsp", "user_access");
        routePerm.put("POST:/view/user/editProfile.jsp", "user_access");
        routePerm.put("GET:/view/user/changePassword.jsp", "user_access");
        routePerm.put("POST:/view/user/changePassword.jsp", "user_access");
        routePerm.put("GET:/view/user/supportFAQ-detail.jsp", "user_access");
        routePerm.put("GET:/view/user/supportFAQ.jsp", "user_access");


        areaGate.put("/admin/", "role_permission_manage"); // Default admin permission for unspecified routes
        areaGate.put("/customerservice/", "cs_access");
        areaGate.put("/techmanager/", "tech_manager_access"); // Tech Manager
        areaGate.put("/technician/", "technician_access"); // Technician
        areaGate.put("/inventory/", "inventory_access"); // Store Keeper
        areaGate.put("/accountant/", "accountant_access"); // Accountant
        areaGate.put("/customer/", "customer_access"); // Customer
        areaGate.put("/user/", "user_access");
        areaGate.put("/invoices/", "accountant_access");// User
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse ss, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) sr;
        HttpServletResponse res = (HttpServletResponse) ss;

        String ctx = req.getContextPath();
        String path = req.getRequestURI().substring(ctx.length());
        path = normalizePath(path);
        String method = req.getMethod();
        String key = method + ":" + path;

        if (isPublic(path)) {
            chain.doFilter(sr, ss);
            return;
        }

        HttpSession session = req.getSession(false);
        Integer userId = (session == null) ? null : (Integer) session.getAttribute("userId");
        if (userId == null || userId <= 0) {
            String back = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
            String encoded = URLEncoder.encode(back, "UTF-8");
            res.sendRedirect(res.encodeRedirectURL(ctx + "/login?back=" + encoded));
            return;
        }

        String required = routePerm.get(key);

        if (required == null) {
            for (Map.Entry<String, String> e : areaGate.entrySet()) {
                if (path.startsWith(e.getKey())) {
                    required = e.getValue();
                    break;
                }
            }
        }

        if (required == null) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission mapping missing for: " + key);
            return;
        }
        if (required != null) {
            try {
                if (!auth.hasPermission(userId, required)) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing permission: " + required);
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        chain.doFilter(sr, ss);
    }

    @Override
    public void destroy() {
    }

    private boolean isPublic(String path) {
        return path.equals("/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/login") ||
                path.startsWith("/logout") ||
                path.startsWith("/Register") ||
                path.startsWith("/public/") ||
                path.startsWith("/mock/") ||
                path.startsWith("/favicon") ||
                path.startsWith("/error") ||
                path.startsWith("/register.jsp") ||
                path.startsWith("/Home") ||
                path.startsWith("/forgotpassword") ||
                path.startsWith("/home.jsp") ||
                path.startsWith("/support-faq") ;
    }

    private String normalizePath(String p) {
        if (p == null || p.isEmpty())
            return "/";

        p = p.replaceAll("/{2,}", "/");

        if (p.length() > 1 && p.endsWith("/"))
            p = p.substring(0, p.length() - 1);
        return p;
    }

}