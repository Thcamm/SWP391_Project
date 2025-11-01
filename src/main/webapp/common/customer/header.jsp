<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.user.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- FontAwesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<!-- Custom Chatbot CSS -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/user/chatbot.css">

<style>
    :root {
        --primary-color: #ffffff;
        --secondary-color: #adb5bd;
        --dark-bg: #111111;
        --button-dark: #212529;
        --button-hover: #343a40;
        --brand-red: #dc3545;
    }


    /* --- Header Top Bar --- */
    .header-top {
        background-color: var(--dark-bg);
        border-bottom: 1px solid #2a2a2a;
        font-size: 0.85rem;
        padding: 0.5rem 0;
        color: var(--secondary-color);
    }

    .header-top a {
        color: var(--secondary-color);
        text-decoration: none;
    }

    .header-top a:hover {
        color: var(--primary-color);
    }

    .header-top .info-item i {
        margin-right: 8px;
    }

    .header-top .btn-contact-top {
        background-color: var(--button-dark);
        border: 1px solid #444;
        color: var(--primary-color);
        padding: 0.4rem 1rem;
        font-size: 0.8rem;
        font-weight: 500;
        transition: all 0.3s ease;
    }

    .header-top .btn-contact-top:hover {
        background-color: var(--button-hover);
        border-color: #555;
    }

    /* --- Main Navigation --- */
    .main-navbar {
        background-color: rgba(17, 17, 17, 0.8);
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        padding: 1rem 0;
    }

    .navbar-brand {
        font-size: 2rem;
        font-weight: 900;
        color: var(--primary-color) !important;
        letter-spacing: -1px;
    }

    .navbar-brand span {
        font-weight: 400;
        font-size: 0.6em;
        vertical-align: middle;
        margin-left: 2px;
    }

    .nav-icons .nav-icon {
        font-size: 1.2rem;
        color: var(--primary-color);
        margin-left: 1.5rem;
        cursor: pointer;
        transition: color 0.3s ease;
    }

    .nav-icons .nav-icon:hover {
        color: var(--secondary-color);
    }

    /* --- Sidebar Menu --- */
    .menu-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.7);
        z-index: 9998;
        opacity: 0;
        visibility: hidden;
        transition: opacity 0.3s ease, visibility 0.3s ease;
    }

    .menu-overlay.active {
        opacity: 1;
        visibility: visible;
    }

    .sidebar-menu {
        position: fixed;
        top: 0;
        right: -350px;
        width: 350px;
        height: 100%;
        background-color: #ffffff;
        z-index: 9999;
        transition: right 0.3s ease;
        overflow-y: auto;
    }

    .sidebar-menu.active {
        right: 0;
    }

    .sidebar-header {
        padding: 20px;
        border-bottom: 1px solid #e0e0e0;
        display: flex;
        justify-content: flex-end;
    }

    .close-btn {
        background: none;
        border: none;
        font-size: 1.5rem;
        color: #111;
        cursor: pointer;
        transition: color 0.3s ease;
    }

    .close-btn:hover {
        color: var(--brand-red);
    }

    .sidebar-nav {
        padding: 30px 0;
    }

    .menu-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 30px;
        color: #111;
        text-decoration: none;
        font-weight: 500;
        font-size: 0.95rem;
        letter-spacing: 0.5px;
        border-bottom: 1px solid #f0f0f0;
        transition: background-color 0.3s ease;
    }

    .menu-item:hover {
        background-color: #f8f9fa;
    }

    .menu-item i {
        color: #adb5bd;
        font-size: 0.8rem;
    }

    .welcome-user {
        color: var(--primary-color);
        font-weight: 500;
        font-size: 0.9rem;
    }

    .welcome-user i {
        color: var(--brand-red);
        margin-right: 8px;
    }

    .header-top .btn-logout {
        background-color: var(--brand-red);
        border: 1px solid var(--brand-red);
        color: var(--primary-color);
        padding: 0.4rem 1rem;
        font-size: 0.8rem;
        font-weight: 500;
        transition: all 0.3s ease;
    }

    .header-top .btn-logout:hover {
        background-color: #c82333;
        border-color: #bd2130;
    }

    .header-top .btn-logout i {
        margin-right: 5px;
    }
</style>

<!-- HEADER -->
<header class="fixed-top">
    <!-- Top contact bar -->
    <div class="header-top d-none d-lg-block">
        <div class="container d-flex justify-content-between align-items-center">
            <div class="d-flex gap-4">
                <a href="https://www.google.com/maps/search/?api=1&query=757+Huỳnh+Tấn+Phát,+Phú+Thuận,+Q7"
                   target="_blank" class="info-item">
                    <i class="fas fa-map-marker-alt"></i> 757 Huỳnh Tấn Phát, Phú Thuận, Q7
                </a>

                <span class="info-item"><i class="fas fa-clock"></i> Thứ 2 - 7 / 08.00AM - 17.00PM</span>
                <a href="tel:0909579579" class="info-item">
                    <i class="fas fa-phone-alt"></i> Hotline: 0909 579 579 (Mr. Hưng)
                </a>
            </div>

            <!-- Nếu user đã đăng nhập -->
            <c:if test="${not empty sessionScope.user}">
                <div class="d-flex align-items-center gap-3">
                    <span class="welcome-user">
                        <i class="fas fa-user"></i> Xin chào, ${sessionScope.user.fullName}
                    </span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-logout">
                        <i class="fas fa-sign-out-alt"></i> Đăng xuất
                    </a>
                </div>
            </c:if>

            <!-- Nếu chưa đăng nhập -->
            <c:if test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-contact-top">Đăng nhập</a>
            </c:if>
        </div>
    </div>

    <!-- Main Navbar -->
    <nav class="main-navbar">
        <div class="container d-flex justify-content-between align-items-center">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/Home">
                CS<span>CARSPA.VN</span>
            </a>
            <div class="nav-icons d-flex align-items-center">
                <i class="fas fa-search nav-icon"></i>
                <i class="fas fa-bars nav-icon" id="menuToggle"></i>
            </div>
        </div>
    </nav>
</header>

<!-- Overlay -->
<div class="menu-overlay" id="menuOverlay"></div>

<!-- Sidebar Menu -->
<div class="sidebar-menu" id="sidebarMenu">
    <div class="sidebar-header">
        <button class="close-btn" id="closeMenu">
            <i class="fas fa-times"></i>
        </button>
    </div>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/customer/appointment-history" class="menu-item">
            <span>Appointment List</span>
        </a>
        <a href="${pageContext.request.contextPath}/customer/AppointmentService" class="menu-item">
            <span>Create Appointment</span>
        </a>
        <a href="${pageContext.request.contextPath}/customer/create-support-request" class="menu-item">
            <span>Create Support Request</span>
        </a>
        <a href="${pageContext.request.contextPath}/support-faq" class="menu-item">
            <span>Frequently Asked Questions</span>
        </a>
        <a href="${pageContext.request.contextPath}/customer/garage" class="menu-item">
            <span>QUẢN LÝ XE (MY GARAGE)</span>
        </a>
    </nav>
</div>

<!-- Toggle Menu Script -->
<script>
    const menuToggle = document.getElementById('menuToggle');
    const sidebarMenu = document.getElementById('sidebarMenu');
    const menuOverlay = document.getElementById('menuOverlay');
    const closeMenuBtn = document.getElementById('closeMenu');

    menuToggle.addEventListener('click', () => {
        sidebarMenu.classList.add('active');
        menuOverlay.classList.add('active');
    });

    closeMenuBtn.addEventListener('click', () => {
        sidebarMenu.classList.remove('active');
        menuOverlay.classList.remove('active');
    });

    menuOverlay.addEventListener('click', () => {
        sidebarMenu.classList.remove('active');
        menuOverlay.classList.remove('active');
    });
</script>
