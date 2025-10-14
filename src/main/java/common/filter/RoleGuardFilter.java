//package common.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//
//public class RoleGuardFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        final String ctx = request.getContextPath();
//        final String path = request.getRequestURI().substring(ctx.length());
//
//        if(path.equals("/")||
//        path.startsWith("/assets/")||
//        path.startsWith("/login")||
//        path.startsWith("/logout")||
//        path.startsWith("/register")||
//        path.startsWith("/public/")||
//        path.startsWith("/mock/")) {
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        HttpSession ss = request.getSession(false);
//        Integer userId = (ss == null) ? null : (Integer)ss.getAttribute("userId");
//        if(userId == null || userId <= 0) {
//            ((HttpServletResponse) servletResponse).sendRedirect(response.encodeRedirectURL(ctx + "/login"));
//            return;
//        }
//
//        String role = (String) ss.getAttribute("roleCode");
//        role = role == null ? "" : role.toUpperCase();
//
//        if (path.startsWith("/admin/")) {
//            if (!role.equals("ADMIN")) { response.sendError(403); return; }
//        } else if (path.startsWith("/cs/")) {
//            if (!(role.equals("ADMIN") || role.equals("CS"))) { response.sendError(403); return; }
//        } else if (path.startsWith("/tech-manager/")) {
//            if (!(role.equals("ADMIN") || role.equals("TECH_MANAGER"))) { response.sendError(403); return; }
//        } else if (path.startsWith("/technician/")) {
//            if (!(role.equals("ADMIN") || role.equals("TECHNICIAN"))) { response.sendError(403); return; }
//        } else if (path.startsWith("/inventory/")) {
//            if (!(role.equals("ADMIN") || role.equals("STORE_KEEPER"))) { response.sendError(403); return; }
//        } else if (path.startsWith("/accounting/")) {
//            if (!(role.equals("ADMIN") || role.equals("ACCOUNTANT"))) { response.sendError(403); return; }
//        } else if (path.startsWith("/app/")) {
//            if (!(role.equals("ADMIN") || role.equals("CUSTOMER"))) { response.sendError(403); return; }
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//
//    }
//
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}
