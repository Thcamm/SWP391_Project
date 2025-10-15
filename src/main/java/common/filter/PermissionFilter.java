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
//        routePerm.put("GET:/admin/rbac/roles", "role_permission_manage");
//        routePerm.put("POST:/admin/rbac/roles/save", "role_permission_manage");
//        routePerm.put("GET:/admin/rbac/permissions", "role_permission_manage");
//        routePerm.put("POST:/admin/rbac/permissions", "role_permission_manage");
//        routePerm.put("GET:/admin/rbac/rolesList", "role_permission_manage");
//        routePerm.put("POST:/admin/rbac/rolesList", "role_permission_manage");
        routePerm.put("GET:/view/role/rbac.jsp", "role_permission_manage");
        routePerm.put("GET:/view/role/list.jsp", "role_permission_manage");
        routePerm.put("GET:/view/role/permission_form.jsp", "role_permission_manage");
        routePerm.put("GET:/view/role/form.jsp", "role_permission_manage");
        routePerm.put("POST:/view/role/form.jsp", "role_permission_manage");



        // User (Admin) (Anh em tu dien url va permission code) ung voi phan minh code
        routePerm.put("GET:/admin/users", "user_read");
        routePerm.put("GET:/admin/users/create", "user_create");
        routePerm.put("POST:/admin/users/create", "user_create");
        routePerm.put("GET:/admin/users.jsp", "user_read");



        // Customer Service
        routePerm.put("GET:/cs/appointements", "appointment_read");
        routePerm.put("POST:/cs/appointements/create", "appointment_create");
        routePerm.put("POST:/cs/appointements/update", "appointment_update");
        routePerm.put("POST:/cs/appointements/delete", "appointment_delete");
        routePerm.put("GET:/customerservice/appointment-list", "cs_access");
        routePerm.put("POST:/customerservice/appointment-list", "cs_access");
        routePerm.put("GET:/customerservice/create-customer", "cs_access");
        routePerm.put("POST:/customerservice/create-customer", "cs_access");
        routePerm.put("POST:/customerservice/search-customer", "cs_access");
        routePerm.put("GET:/customerservice/view-support-request", "cs_access");
        routePerm.put("POST:/customerservice/view-support-request", "cs_access");


        // Tech manager
        routePerm.put("GET:/tech-manager/orders", "workorder_read");
        routePerm.put("POST:/tech-manager/orders", "workorder_create");
        routePerm.put("POST:/tech-manager/assign", "technician_assign");

        // Technician
        routePerm.put("GET:/technician/jobs", "job_read");
        routePerm.put("POST:/technician/jobs/update", "job_update_progress");
        routePerm.put("POST:/technician/diagnose", "diagnostics_run");

        // Storekeeper (Inventory)
        routePerm.put("GET:/inventory/items", "inventory_read");
        routePerm.put("POST:/inventory/items", "inventory_create");
        routePerm.put("POST:/inventory/import", "inventory_import");
        routePerm.put("POST:/inventory/export", "inventory_export");

        // Accountant
        routePerm.put("GET:/accounting/payments", "payment_read");
        routePerm.put("POST:/accounting/payments", "payment_create");
        routePerm.put("POST:/accounting/confirm", "payment_confirm");

        // Customer
        routePerm.put("GET:/app/bookings", "booking_read_own");
        routePerm.put("POST:/app/bookings", "booking_create");
        routePerm.put("GET:/app/vehicles/status", "view_vehicle_status");
        routePerm.put("POST:/app/comments", "comment_create_by_customer");
        routePerm.put("GET:/app/create-support-request", "customer_access");
        routePerm.put("POST:/app/create-support-request", "customer_access");
        routePerm.put("GET:/customer/garage", "customer_access");
        routePerm.put("POST:/customer/garage", "customer_access");
        routePerm.put("GET:/view/vehicle/garage.jsp", "customer_access");
        routePerm.put("GET:/customer/addVehicle", "customer_access");
        routePerm.put("POST:/customer/addVehicle", "customer_access");
        routePerm.put(" GET:/view/vehicle/addVehicle.jsp", "customer_access");
        routePerm.put("GET:/customer/editVehicle", "customer_access");
        routePerm.put("POST:/customer/editVehicle", "customer_access");
        routePerm.put("GET:view/vehicle/editVehicle.jsp", "customer_access");
        routePerm.put("POST:/customer/deleteVehicle", "customer_access");



        areaGate.put("/admin/", "role_permission_manage");
        areaGate.put("/customerservice/",            "cs_access");              // Customer Service
        areaGate.put("/tech-manager/",  "tech_manager_access");    // Tech Manager
        areaGate.put("/technician/",    "technician_access");      // Technician
        areaGate.put("/inventory/",     "inventory_access");       // Store Keeper
        areaGate.put("/accounting/",    "accounting_access");      // Accountant
        areaGate.put("/app/",           "customer_access");

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

        if (isPublic(path)) { chain.doFilter(sr, ss); return; }

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
                if(path.startsWith(e.getKey())) {
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



        res.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
        res.setHeader("Pragma","no-cache");
        res.setDateHeader("Expires",0);

        chain.doFilter(sr, ss);
    }

    @Override public void destroy() {}

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
