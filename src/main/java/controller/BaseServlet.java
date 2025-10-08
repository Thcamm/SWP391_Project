package controller;

import dao.rbac.RoleDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.auth.AuthService;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService(new RoleDao());

    }

    protected boolean requirePerm(HttpServletRequest request, HttpServletResponse resp, String perm) throws ServletException, IOException {
        Integer uid = (Integer)request.getSession().getAttribute("userId");
        if(uid == null) {
            resp.sendRedirect(request.getContextPath() + "/login");
            return false;

        }

        try{
            if(!authService.hasPermission(uid, perm)){
                resp.sendError(403, "You have not permission to access this resource");
                return false;
            }
        }catch (Exception e){
            throw new IOException(e.getMessage());

        }

        return true;
    }
}
