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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@WebServlet(urlPatterns = "/admin/rbac/roles/save")
public class RolePermSaveServlet extends HttpServlet {
    private RbacService rbacService;
    private AuthService authService;
    private PermissionDao permissionDao;

    @Override
    public void init() {
        RoleDao roleDao = new RoleDao();
        this.permissionDao = new PermissionDao();
        MenuDao menuDao = new MenuDao();

        this.authService = new AuthService(roleDao);
        this.rbacService = new RbacService(roleDao, permissionDao, menuDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");


            Integer userIdObj = (Integer) req.getSession().getAttribute("userId");
//            if (userIdObj == null || userIdObj <= 0) {
//                resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/login"));
//                return;
//            }
            int actorUserId = userIdObj;


            int roleId = Integer.parseInt(req.getParameter("roleId"));

            String[] checkedArr = req.getParameterValues("permIds");
            String[] pageArr    = req.getParameterValues("pagePermIds");

            Set<Integer> checked = new HashSet<>();
            if (checkedArr != null) {
                for (String s : checkedArr) checked.add(Integer.parseInt(s));
            }

            Set<Integer> pageSet = new HashSet<>();
            if (pageArr != null) {
                for (String s : pageArr) pageSet.add(Integer.parseInt(s));
            }

            Set<Integer> current = rbacService.getPermissionIdsOfRole(roleId);


            current.removeAll(pageSet);


            current.addAll(checked);


            final String REQUIRED_PERMISSION = "role_permission_manage";
            int managePermId = permissionDao.findPermIdByCode(REQUIRED_PERMISSION);
            int currentUserRoleId = new dao.user.UserDAO().findRoleIdByUserId(actorUserId);

            if (roleId == currentUserRoleId && managePermId > 0 && !current.contains(managePermId)) {

                current.add(managePermId);

            }


            rbacService.assignPermissions(roleId, new ArrayList<>(current), actorUserId);


            resp.sendRedirect(resp.encodeRedirectURL(
                    req.getContextPath() + "/admin/rbac/roles?roleId=" + roleId + "&saved=1"));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role ID or Permission ID");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
