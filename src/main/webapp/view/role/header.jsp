<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Admin Dashboard'} - Garage Management</title>

    <!-- Common CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/footer.css">

    <!-- Page-specific CSS (optional) -->
    <c:if test="${not empty pageCSS}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/${pageCSS}">
    </c:if>
</head>
<body class="admin-page">
<!-- Header -->
<header class="admin-header">
    <div class="header-container">
        <div class="header-left">
            <div class="logo">
                <a href="#">
                    <h1>Garage Admin</h1>
                </a>
            </div>
        </div>

        <!-- Main Navigation -->
        <nav class="main-nav">
            <ul class="nav-menu">
<%--                <li class="nav-item ${activeMenu == 'dashboard' ? 'active' : ''}">--%>
<%--                    <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">--%>
<%--                        Dashboard--%>
<%--                    </a>--%>
<%--                </li>--%>

                <li class="nav-item dropdown ${activeMenu == 'rbac' ? 'active' : ''}">
                    <a href="#" class="nav-link dropdown-toggle">
                        RBAC Management
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list">
                                Roles
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/admin/rbac/roles">
                                Role&Permissions
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item ${activeMenu == 'users' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">
                        Users
                    </a>
                </li>

                <li class="nav-item ${activeMenu == 'employees' ? 'active' : ''}">
                    <a href="#" class="nav-link">
                        Employees
                    </a>
                </li>

                <li class="nav-item ${activeMenu == 'reports' ? 'active' : ''}">
                    <a href="#" class="nav-link">
                        Reports
                    </a>
                </li>
            </ul>
        </nav>

        <!-- Right Side: User Info & Logout -->
        <div class="header-right">
            <div class="user-info">
                <span class="user-name">${sessionScope.userName}</span>
                <span class="user-role">Admin</span>
            </div>
            <a href="${pageContext.request.contextPath}/Home/logout" class="logout-btn">Logout</a>
        </div>
    </div>
</header>


<!-- Main Content -->
<main class="admin-content">