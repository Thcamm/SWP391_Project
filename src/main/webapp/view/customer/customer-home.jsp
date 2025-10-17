<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carspa | Dịch Vụ Chăm Sóc Xe Tốt Nhất</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="model.user.User" %>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chatbot.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/customer-home.css">

</head>
<body>

<!-- SECTION: Header -->
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
                <a href="tel:0909579579" class="info-item"><i class="fas fa-phone-alt"></i> Hotline: 0909 579 579 (Mr.
                    Hưng)</a>
            </div>
            <div class="d-flex align-items-center gap-3">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/user/profile" class="welcome-user" style="text-decoration: none; color: white;">        <span>
            <i class="fas fa-user-circle"></i>
            Xin chào, ${sessionScope.user.fullName}
        </span>
                        </a>
                        <a href="Home?action=logout" class="btn btn-logout">
                            <i class="fas fa-sign-out-alt"></i> Đăng xuất
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="login" class="btn btn-contact-top" name="action" value="login">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>

    <!-- Main navigation bar -->
    <nav class="main-navbar">
        <div class="container d-flex justify-content-between align-items-center">
            <a class="navbar-brand" href="#">
                CS<span>CARSPA.VN</span>
            </a>
            <div class="nav-icons d-flex align-items-center">
                <i class="fas fa-search nav-icon"></i>
                <i class="fas fa-bars nav-icon"></i>
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
        <a href="${pageContext.request.contextPath}" class="menu-item">
            <span>DỊCH VỤ</span>
        </a>
        <a href="${pageContext.request.contextPath}/customer/" class="menu-item">
            <span>THƯ VIỆN DỰ ÁN</span>
        </a>
        <a href="${pageContext.request.contextPath}/customer/appointment-history" class="menu-item">
            <span>APPOINTMENT HISTORY</span>
        </a>
        <a href="${pageContext.request.contextPath}/support-faq" class="menu-item">
            <span>SUPPORT</span>
        </a>
        <a href="AppointmentScheduling" class="menu-item">
            <span>Lien he dat lich</span>
        </a>
        <c:if test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/customer/garage" class="menu-item">
                <span>QUẢN LÝ XE (MY GARAGE)</span>
            </a>
        </c:if>
    </nav>
</div>


<!-- SECTION: Hero -->
<main class="hero-section">
    <div class="container">
        <div class="row">
            <div class="col-lg-8">
                <div class="hero-content">
                    <p class="sub-heading">Carspa. Khắc phục mọi vấn đề cho xe</p>
                    <h1>Trung tâm sửa chữa ô tô <strong>chuyên nghiệp.</strong></h1>
                    <a href="#" class="btn btn-view-services">
                        XEM TẤT CẢ DỊCH VỤ <i class="fas fa-arrow-right ms-2"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>


<!-- SECTION: Chat Widget (Gemini AI) -->
<!-- Nút toggle chat -->
<button id="chat-toggle-btn">
    <i class="fas fa-comment-dots"></i>
</button>

<div id="chatbox">
    <div id="chat-header">
        Garacuabuituanduong
    </div>
    <div id="chat-messages">
    </div>
    <div id="chat-input-area">
        <input type="text" id="user-input" placeholder="Aa"/>
        <button id="send-btn"><i class="fas fa-paper-plane"></i></button>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
<!-- Bootstrap JS -->
<script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
<!-- Chat widget inline script (Gemini UI behaviour) -->
<script src="${pageContext.request.contextPath}/assets/js/customer/customer-home.js"></script>

</body>
</html>
