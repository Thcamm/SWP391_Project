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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

            // must be logged in
            Integer userIdObj = (Integer) req.getSession().getAttribute("userId");
//            if (userIdObj == null || userIdObj <= 0) {
//                resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/login"));
//                return;
//            }
            int actorUserId = userIdObj;

            // role and permissions from form
            int roleId = Integer.parseInt(req.getParameter("roleId"));

            String[] checkedArr = req.getParameterValues("permIds");     // các checkbox đang check trên trang hiện tại
            String[] pageArr    = req.getParameterValues("pagePermIds");  // TẤT CẢ permId đang hiển thị ở trang hiện tại

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

            //khong tu go quyen quan li cua admin
            final String REQUIRED_PERMISSION = "role_permission_manage";
            int managePermId = permissionDao.findPermIdByCode(REQUIRED_PERMISSION);
            int currentUserRoleId = new dao.user.UserDAO().findRoleIdByUserId(actorUserId);
            if (roleId == currentUserRoleId && managePermId > 0 && !current.contains(managePermId)) {
                current.add(managePermId);
            }

            // luu
            rbacService.assignPermissions(roleId, new ArrayList<>(current), actorUserId);

            // giu nguyn trang thai paging/filter sau khi  save
            String keyword  = strOrEmpty(req.getParameter("keyword"));
            String category = strOrEmpty(req.getParameter("category"));
            int page = parseIntOrDefault(req.getParameter("page"), 1);
            int size = parseIntOrDefault(req.getParameter("size"), 10);

            String qs = "roleId=" + roleId +
                    "&saved=1" +
                    "&page=" + page +
                    "&size=" + size +
                    "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                    "&category=" + URLEncoder.encode(category, StandardCharsets.UTF_8);

            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/admin/rbac/roles?" + qs));

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role ID or Permission ID");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception ex) { return def; }
    }

    private String strOrEmpty(String s) {
        return (s == null) ? "" : s;
    }
}
