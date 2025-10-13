package controller.employee.admin.rbac;

import dao.employee.admin.rbac.RoleDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.pagination.PaginationResponse;
import model.employee.admin.rbac.Role;
import service.role.RoleService;

import java.io.IOException;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;


@WebServlet("/admin/rbac/rolesList")
public class RoleServlet extends HttpServlet {
    private RoleService roleService;

    @Override
    public void init() {
        RoleDao roleDao = new RoleDao();
        this.roleService = new RoleService(roleDao);
    }

    public RoleServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if(action == null) action = "list";

        try {
            switch (action) {
                case "new":
                    showCreateForm(req, resp);
                    break;

                case "edit":
                    showEditForm(req, resp);
                    break;
                case "delete":
                    deleteRole(req, resp);
                    break;
                case "list":
                default:
                    listRoles(req, resp);
                    break;
            }
        }catch (Exception ex){
            throw new ServletException(ex);
        }
    }

    private void listRoles(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = parseIntOrDefault(req.getParameter("page"), 1);
        int size = parseIntOrDefault(req.getParameter("size"), 10);
        String keyword = req.getParameter("keyword");



        try {
            PaginationResponse<Role> pager = roleService.getRolePaginated(page, size, keyword);
            

            req.setAttribute("pager", pager);
            req.setAttribute("keyword", keyword);

            req.getRequestDispatcher("/view/role/list.jsp").forward(req, resp);
        }catch (Exception ex){
            ex.printStackTrace();
            req.setAttribute("error","Dont load role list: " +  ex.getMessage());
            req.getRequestDispatcher("/view/error.jsp").forward(req, resp);
        }


    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = parseIntOrDefault(req.getParameter("id"), 0);

        if(id <= 0){
            resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");
            return;
        }


        try{
            Role role =  roleService.getRoleById(id);
            if(role == null){
                req.setAttribute("error","Dont find role with id = " + id);
                resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");
                return;
            }
            req.setAttribute("mode","edit");
            req.setAttribute("role", role);
            req.getRequestDispatcher("/view/role/form.jsp").forward(req, resp);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new ServletException("Dont load role infor" + ex);


        }

    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("mode","create");
        req.getRequestDispatcher("/view/role/form.jsp").forward(req, resp);

    }

    private void createRole(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");

        try{
            roleService.createRole(name, description);
            req.getSession().setAttribute("flash", "Role has been created");
            resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");

        }catch (IllegalArgumentException ex){
            req.setAttribute("mode", "create");
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("name",name);
            req.setAttribute("description",description);
            req.getRequestDispatcher("/view/role/form.jsp").forward(req, resp);
        }


    }

    private void deleteRole(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = parseIntOrDefault(req.getParameter("id"), 0);

        if(id <= 0){
            resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");
            return;
        }

        try{
            roleService.deleteRole(id);
            req.getSession().setAttribute("flash", "Role has been deleted");
        }catch (IllegalArgumentException | SQLException ex){
            req.getSession().setAttribute("error", "Dont delete role: " + ex.getMessage());
        }
        resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");
    }

    public void updateRole(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        int id = parseIntOrDefault(req.getParameter("id"), 0);
        String name = req.getParameter("name");
        String description = req.getParameter("description");

        if(id <= 0){
            resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");
            return;
        }

        try{
            roleService.renameRole(id, name, description);
            req.getSession().setAttribute("flash", "Role has been updated");
            resp.sendRedirect(req.getContextPath()+"/admin/rbac/rolesList?action=list");

        }catch (IllegalArgumentException ex){
            req.setAttribute("mode", "edit");
            req.setAttribute("error", ex.getMessage());
            Role r = new Role();
            r.setRoleId(id);
            r.setRoleName(name);
            r.setDescription(description);
            req.setAttribute("role",r);
            req.getRequestDispatcher("/view/role/form.jsp").forward(req, resp);
        }
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            switch (action) {
                case "create":
                    createRole(req, resp);
                    break;
                case "update":
                    updateRole(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/rbac/rolesList?action=list");
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
