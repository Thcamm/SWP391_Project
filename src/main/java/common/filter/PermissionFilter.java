package common.filter;

import dao.rbac.RoleDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.auth.AuthService;

import java.io.IOException;
import java.util.Set;

//@WebFilter(urlPatterns = "/*")
public class PermissionFilter implements Filter {

    private AuthService authService;


    @Override
    public void init(FilterConfig filterConfig){
        RoleDao roleDao = new RoleDao();
        this.authService = new AuthService(roleDao);
    }

    @Override
    public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        if(isPublicResourcePath(path)){
            chain.doFilter(request, response);
            return;
        }

        Integer userId = (Integer) req.getSession().getAttribute("userId");
        if(userId == null || userId <= 0) {
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/LoginServlet"));
            return;

        }

        try{
            Set<String> userPermissions = authService.getPermissionCodesOfUser(userId);
            req.setAttribute("userPermissions", userPermissions);
            chain.doFilter(request, response);


        }catch (Exception e){
            throw new ServletException(e.getMessage());
        }
    }

    private boolean isPublicResourcePath(String path) {

        return path.startsWith("/public")
                || path.startsWith("/login")
                || path.startsWith("/register");
    }


}
