<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/header-techmanager.css">

<header class="header techmanager-header">
    <div class="header-left">
        <div class="logo">
            <i class="bi bi-gear-fill"></i>
            <h1>Tech Manager Portal</h1>
        </div>
    </div>

    <nav class="main-nav">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/techmanager/dashboard"
                   class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}">
                    <i class="bi bi-speedometer2"></i>
                    Dashboard
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/techmanager/service-requests"
                   class="nav-link ${activeMenu == 'service-requests' ? 'active' : ''}">
                    <i class="bi bi-clipboard-check"></i>
                    Service Requests
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/techmanager/diagnosis-review"
                   class="nav-link ${activeMenu == 'diagnosis-review' ? 'active' : ''}">
                    <i class="bi bi-file-earmark-text"></i>
                    Diagnosis Review
                </a>
            </li>
        </ul>
    </nav>

    <div class="header-right">
        <div class="user-info">
            <span class="user-name">${sessionScope.user.fullName}</span>
            <span class="user-role">Tech Manager</span>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
            <i class="bi bi-box-arrow-right"></i>
            Logout
        </a>
    </div>
</header>
