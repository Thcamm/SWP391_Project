package controller.rbac;

import dao.rbac.PermissionDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.rbac.Permission;
import service.rbac.PermisisonService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet ("/permissions")
public class PermissionServlet extends HttpServlet {
    private PermisisonService service;

    @Override
    public void init() {
        PermissionDao dao = new PermissionDao();
        this.service = new PermisisonService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

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
                    resp.sendRedirect(req.getContextPath() + "/rbac/roles");
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String category = req.getParameter("category");
        String description = req.getParameter("description");
        boolean active = "on".equals(req.getParameter("active"));

        try {
            if (idStr == null || idStr.isEmpty()) {
                Permission newP = service.createPermission(code, name, category, description, active);

            } else {
                int id = Integer.parseInt(idStr);
                service.updatePermission(id, code, name, category, description, active);

            }
            resp.sendRedirect(req.getContextPath() + "/rbac/roles?saved=1");
        } catch (IllegalArgumentException e){
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
        }catch (Exception e){
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
        }

    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("mode", "create");
        req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        try {
            Permission p = service.getById(id);
            req.setAttribute("perm", p);
            req.getRequestDispatcher("/view/role/permission_form.jsp").forward(req, resp);
        }catch (SQLException e){
            throw new ServletException(e);
        }
    }

    private void deletePermission(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        try {
            boolean deleted = service.deletePermission(id);
            if(deleted){
                req.getSession().setAttribute("flash", "Permission has been deleted");
            }else{
                req.getSession().setAttribute("error", "Permission not found or could not be deleted");
            }
        }catch (IllegalArgumentException | SQLException ex){
            req.getSession().setAttribute("error", "Dont delete permission: " + ex.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/rbac/roles?deleted=1");

    }

}
