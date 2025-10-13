package controller.employee.admin.rbac;

import dao.employee.admin.rbac.PermissionDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.employee.admin.rbac.Permission;
import service.rbac.PermissionService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/admin/rbac/permissions")
public class PermissionServlet extends HttpServlet {
    private PermissionService service;

    @Override
    public void init() {
        PermissionDao dao = new PermissionDao();
        this.service = new PermissionService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = safeParam(req, "action");
        try {
            switch (action) {
                case "new":
                    showCreateForm(req, resp);
                    break;
                case "edit":
                    showEditForm(req, resp);
                    break;
                case "delete":
                    deletePermission(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/rbac/roles");
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String idStr = safeParam(req, "id");
        String code = safeParam(req, "code");
        String name = safeParam(req, "name");
        String category = safeParam(req, "category");
        String description = safeParam(req, "description");

        try {
            if (isBlank(idStr)) {
                service.createPermission(code, name, category, description);
            } else {
                int id = parseIntOrDefault(idStr, 0);
                service.updatePermission(id, code, name, category, description);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/rbac/roles?saved=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());

            if (isBlank(idStr)) {
                req.setAttribute("mode", "create");
                req.setAttribute("code", code);
                req.setAttribute("name", name);
                req.setAttribute("category", category);
                req.setAttribute("description", description);
            } else {
                Permission p = new Permission();
                p.permId = parseIntOrDefault(idStr, 0);
                p.code = code;
                p.name = name;
                p.category = category;
                p.description = description;
                req.setAttribute("perm", p);
            }

            req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            if (isBlank(idStr)) {
                req.setAttribute("mode", "create");
                req.setAttribute("code", code);
                req.setAttribute("name", name);
                req.setAttribute("category", category);
                req.setAttribute("description", description);
            } else {
                Permission p = new Permission();
                p.permId = parseIntOrDefault(idStr, 0);
                p.code = code;
                p.name = name;
                p.category = category;
                p.description = description;
                req.setAttribute("perm", p);
            }
            req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
        }
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("mode", "create");
        req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseIntOrDefault(req.getParameter("id"), 0);
        if (id <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rbac/roles");
            return;
        }
        try {
            Permission p = service.getById(id);
            if (p == null) {
                req.getSession().setAttribute("error", "Permission not found.");
                resp.sendRedirect(req.getContextPath() + "/admin/rbac/roles");
                return;
            }
            req.setAttribute("perm", p);
            req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void deletePermission(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseIntOrDefault(req.getParameter("id"), 0);
        try {
            boolean deleted = service.deletePermission(id);
            if (deleted) {
                req.getSession().setAttribute("flash", "Permission has been deleted");
            } else {
                req.getSession().setAttribute("error", "Permission not found or could not be deleted");
            }
        } catch (IllegalArgumentException | SQLException ex) {
            req.getSession().setAttribute("error", "Dont delete permission: " + ex.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/rbac/roles?deleted=1");
    }


    private static String safeParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
