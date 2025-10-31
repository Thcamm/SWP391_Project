<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/header.css">


<header class="header">
    <div class="header-left">
        <div class="logo"><h1>🐣Garage Management</h1></div>
    </div>

    <nav class="main-nav">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/admin/users"
                   class="nav-link ${activeMenu == 'Home' ? 'active' : ''}">
                    Home
                </a>
            </li>
        </ul>
    </nav>

    <div class="header-right">
        <div class="user-info">
            <span class="user-name">${sessionScope.userName}</span>
            <span class="user-role">${sessionScope.roleName}</span>
        </div>
        <a href="${pageContext.request.contextPath}/Home?action=logout" class="logout-btn">Logout</a>
    </div>
</header>
