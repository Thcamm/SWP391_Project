<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/sidebar.css">


<div class="sidebar collapsed" id="sidebar">
    <div class="top-menu">
        <button class="hamburger" id="toggleBtn">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none"
                 viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round"
                      stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
        </button>

        <div class="menu">
            <a href="${pageContext.request.contextPath}/customerservice/search-customer" class="menu-item">
                <div class="icon">🏠</div>
                <span class="label">Customer Information</span>
            </a>
            <a href="${pageContext.request.contextPath}/customerservice/appointment-list" class="menu-item">
                <div class="icon">👥</div>
                <span class="label">Appointment Booking</span>
            </a>
            <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="menu-item">
                <div class="icon">🛠️</div>
                <span class="label">Support Request</span>
            </a>
        </div>
    </div>

    <div class="bottom-menu">
        <a href="${pageContext.request.contextPath}/user/profile" class="menu-item">
            <div class="icon">👤</div>
            <span class="label">Profile</span>
        </a>
        <a href="${pageContext.request.contextPath}/Home?action=logout" class="menu-item">
            <div class="icon">🚪</div>
            <span class="label">Logout</span>
        </a>
    </div>
</div>


<script src="${pageContext.request.contextPath}/assets/js/customerservice/sidebar.js"></script>
