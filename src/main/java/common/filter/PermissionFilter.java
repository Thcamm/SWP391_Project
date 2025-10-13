package common.filter;



import dao.employee.admin.rbac.RoleDao;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.auth.AuthService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PermissionFilter implements Filter {
    private AuthService auth;
    private Map<String, String> routePerm;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.auth = new AuthService(new RoleDao());
        this.routePerm = new HashMap<>();

        // RBAC (Admin)
        routePerm.put("GET:/admin/rbac/roles", "role_permission_manage");
        routePerm.put("POST:/admin/rbac/roles/save", "role_permission_manage");
        routePerm.put("GET:/admin/rbac/permissions", "role_permission_manage");
        routePerm.put("POST:/admin/rbac/permissions", "role_permission_manage");
        routePerm.put("GET:/admin/rbac/roleList", "role_permission_manage");
        routePerm.put("POST:/admin/rbac/roleList", "role_permission_manage");

        //User (Admin) (Anh em tu dien url va permission code) ung voi phan minh code
        routePerm.put("GET:/admin/users", "user_read");
        routePerm.put("POST:/admin/users/create", "user_create");


        // Customer Service
        routePerm.put("GET:/cs/appointements", "appointment_read");
        routePerm.put("POST:/cs/appointements/create", "appointment_create");
        routePerm.put("POST:/cs/appointements/update", "appointment_update");
        routePerm.put("POST:/cs/appointements/delete", "appointment_delete");


        //Tech manager
        routePerm.put("GET:/tech-manager/orders",     "workorder_read");
        routePerm.put("POST:/tech-manager/orders",    "workorder_create");
        routePerm.put("POST:/tech-manager/assign",    "technician_assign");

        //Technician
        routePerm.put("GET:/technician/jobs",         "job_read");
        routePerm.put("POST:/technician/jobs/update", "job_update_progress");
        routePerm.put("POST:/technician/diagnose",    "diagnostics_run");

        //Storekeeper (Inventory)
        routePerm.put("GET:/inventory/items",         "inventory_read");
        routePerm.put("POST:/inventory/items",        "inventory_create");
        routePerm.put("POST:/inventory/import",       "inventory_import");
        routePerm.put("POST:/inventory/export",       "inventory_export");

        //Accountant
        routePerm.put("GET:/accounting/payments",     "payment_read");
        routePerm.put("POST:/accounting/payments",    "payment_create");
        routePerm.put("POST:/accounting/confirm",     "payment_confirm");

        //Customer
        routePerm.put("GET:/app/bookings",            "booking_read_own");
        routePerm.put("POST:/app/bookings",           "booking_create");
        routePerm.put("GET:/app/vehicles/status",     "view_vehicle_status");
        routePerm.put("POST:/app/comments",           "comment_create_by_customer");



    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String ctx = request.getContextPath();
        final String path = request.getRequestURI().substring(ctx.length());
        final String method = request.getMethod();
        final String key = method + ":" + path;

        String required = routePerm.get(key);
        if (required != null) {
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            if(userId == null || userId <= 0) {
                ((HttpServletResponse) servletResponse).sendRedirect(((HttpServletResponse) servletResponse).encodeRedirectURL(ctx + "/login"));
                return;
            }

            try {
                if(!auth.hasPermission(userId, required)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You have not permission to access this resource");
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
