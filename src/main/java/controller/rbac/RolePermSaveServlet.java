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

@WebServlet(urlPatterns = "/rbac/roles/save")
public class RolePermSaveServlet extends HttpServlet {
    private RbacService rbacService;
    private AuthService authService;


    @Override public void init(){
        RoleDao roleDao = new RoleDao();
        PermissionDao permissionDao = new PermissionDao();
        MenuDao menuDao = new MenuDao();

        this.authService = new AuthService(roleDao);
        this.rbacService = new RbacService(roleDao, permissionDao, menuDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws jakarta.servlet.ServletException, java.io.IOException {
        try {
            req.setCharacterEncoding("UTF-8");

            final String REQUIRED_PERMISSION = "role_permission_manage";
            Integer userIdObj = (Integer) req.getSession().getAttribute("userId");
            if (userIdObj == null || userIdObj <= 0) {
                resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/login"));
                return;
            }
            int actorUserId = userIdObj;

            if (!authService.hasPermission(actorUserId, REQUIRED_PERMISSION)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "You have not permission to access role manage");
                return;
            }

            int roleId = Integer.parseInt(req.getParameter("roleId"));


            String[] permIds = req.getParameterValues("permIds");

            java.util.Set<Integer> selected = new java.util.HashSet<>();
            if (permIds != null) {
                for (String pid : permIds) selected.add(Integer.parseInt(pid));
            }

            int currentUserRoleId = new dao.user.UserDAO().findRoleIdByUserId(actorUserId); // viết hàm đơn giản
            int managePermId = new dao.rbac.PermissionDao().findPermIdByCode(REQUIRED_PERMISSION);
            if (roleId == currentUserRoleId && !selected.contains(managePermId)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Dont remove yourself from role '" + currentUserRoleId + "' permission '" + REQUIRED_PERMISSION + "'");
                return;
            }

            rbacService.assignPermissions(roleId, new java.util.ArrayList<>(selected), actorUserId);

            resp.sendRedirect(resp.encodeRedirectURL(
                    req.getContextPath() + "/rbac/roles?roleId=" + roleId + "&saved=1"));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role ID or Permission ID");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
