package controller.employee.admin.rbac;

import dao.employee.admin.rbac.MenuDao;
import dao.employee.admin.rbac.PermissionDao;
import dao.employee.admin.rbac.RoleDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.auth.AuthService;
import service.rbac.RbacService;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/rbac/roles/save")
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");

            Integer userIdObj = (Integer) req.getSession().getAttribute("userId");

            int actorUserId = userIdObj;

            int roleId = Integer.parseInt(req.getParameter("roleId"));
            String[] permIds = req.getParameterValues("permIds");

            java.util.Set<Integer> selected = new java.util.HashSet<>();
            if (permIds != null) for (String pid : permIds) selected.add(Integer.parseInt(pid));


            final String REQUIRED_PERMISSION = "role_permission_manage";
            int currentUserRoleId = new dao.user.UserDAO().findRoleIdByUserId(actorUserId);
            int managePermId = new PermissionDao().findPermIdByCode(REQUIRED_PERMISSION);
            if (roleId == currentUserRoleId && !selected.contains(managePermId)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Dont remove yourself from role '" + currentUserRoleId + "' permission '" + REQUIRED_PERMISSION + "'");
                return;
            }

            rbacService.assignPermissions(roleId, new java.util.ArrayList<>(selected), actorUserId);
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/admin/rbac/roles?roleId=" + roleId + "&saved=1"));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role ID or Permission ID");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
