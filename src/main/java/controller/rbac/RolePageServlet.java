package controller.rbac;

import dao.rbac.MenuDao;
import dao.rbac.PermissionDao;
import dao.rbac.RoleDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.auth.AuthService;
import service.rbac.RbacService;

@WebServlet(urlPatterns = "/rbac/roles")
public class RolePageServlet extends HttpServlet {
    private RbacService rbacService;
    private AuthService authService;

    @Override
    public void init() {
        RoleDao roleDao = new RoleDao();
        PermissionDao permissionDao = new PermissionDao();
        MenuDao menuDao = new MenuDao();

        this.rbacService = new RbacService(roleDao, permissionDao, menuDao);
        this.authService = new AuthService(roleDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        try {
            Integer userIdObj = (Integer) req.getSession().getAttribute("userId");
            final String REQUIRED_PERMISSION = "role_permission_manage";

            if (userIdObj == null || userIdObj <= 0) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            int userId = userIdObj;

            // kiem tra phan quyen
            if (!authService.hasPermission(userId, REQUIRED_PERMISSION)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You have not permission to access role manage");
                return;
            }

            String keyword = req.getParameter("keyword");
            String category = req.getParameter("category");
            int page = parseIntOrDefault(req.getParameter("page"), 1);
            int size = parseIntOrDefault(req.getParameter("size"), 10);

            var roles = rbacService.getAllRoles();
            Integer roleId = req.getParameter("roleId") == null
                    ? (roles.isEmpty() ? -1 : roles.get(0).roleId)
                    : Integer.parseInt(req.getParameter("roleId"));

            var pager = rbacService.getPermissionsPaged(page, size, keyword, category);

            var checkedPermIds = (roleId == null) ? java.util.Collections.<Integer>emptySet()
                    : rbacService.getPermissionIdsOfRole(roleId);
            req.setAttribute("roles", roles);
            req.setAttribute("roleId", roleId);
            req.setAttribute("pager", pager);
            req.setAttribute("perms", pager.getData());
            req.setAttribute("keyword", keyword);
            req.setAttribute("category", category);
            req.setAttribute("checkedPermsIds", checkedPermIds);
            req.getRequestDispatcher("/view/role-page.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int parseIntOrDefault(String page, int i) {
        try {
            return Integer.parseInt(page);
        } catch (Exception ex) {
            return i;
        }
    }

    @Override
    public void doPost(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        doGet(req, resp);
    }
}
