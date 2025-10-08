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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws jakarta.servlet.ServletException, java.io.IOException {
        try {
            Integer userIdObj = (Integer)req.getSession().getAttribute("userId");
            final String REQUIRED_PERMISSION = "role_permission_manage";

            if(userIdObj == null || userIdObj <= 0){
                resp.sendRedirect(req.getContextPath() + "/LoginServlet");
                return;
            }
            int userId = userIdObj;

            // kiem tra phan quyen
            if(!authService.hasPermission(userId, REQUIRED_PERMISSION)){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You have not permission to access role manage");
                return;
            }
            var roles = rbacService.getAllRoles();
            Integer roleId = req.getParameter("roleId") == null
                    ? (roles.isEmpty() ? -1 : roles.get(0).roleId)
                    : Integer.parseInt(req.getParameter("roleId"));

            var perms = rbacService.getAllPermissions(req.getParameter("keyword"), req.getParameter("category"));
            var checkedPermIds = (roleId == null) ? java.util.Set.of() : rbacService.getPermissionIdsOfRole(roleId);
            req.setAttribute("roles", roles);
            req.setAttribute("roleId", roleId);
            req.setAttribute("perms", perms);
            req.setAttribute("checkedPermsIds", checkedPermIds);
            req.getRequestDispatcher("/view/role-page.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws jakarta.servlet.ServletException, java.io.IOException {
        doGet(req, resp);
    }
}
