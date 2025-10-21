<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/19/2025
  Time: 8:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Technician Page - Garage Management</title>
</head>
<body>
<header class="header">
    <div class="header-left">
        <div class="logo">
            <h1>🔧 Garage Management</h1>
        </div>
    </div>

    <nav class="main-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/technician/home" class="nav-link active">Dashboard</a></li>
            <li><a href="#" class="nav-link">My Tasks</a></li>
            <li><a href="#" class="nav-link">Diagnostics</a>
            </li>
            <li><a href="#" class="nav-link">Parts Requests</a></li>
        </ul>
    </nav>

    <div class="header-right">
        <div class="user-info">
            <span class="user-name">${sessionScope.userFullName}</span>
            <span class="user-role">${sessionScope.userRole}</span>
        </div>
        <a href="${pageContext.request.contextPath}/Home?action=logout" class="logout-btn">Logout</a>
    </div>
</header>


<main class="content">


