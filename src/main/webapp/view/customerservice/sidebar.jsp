<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/sidebar.css">

<div class="sidebar" id="sidebar">
    <div class="sidebar-content">
        <!-- Top Section: Toggle Button + Menu -->
        <div class="top-section">
            <!-- Hamburger Toggle Button -->
            <button class="hamburger" id="toggleBtn" aria-label="Toggle Sidebar">
                <i class="fa-solid fa-bars"></i>
            </button>

            <!-- Navigation Menu -->
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/customerservice/search-customer"
                   class="menu-item"
                   title="Customer Info">
                    <i class="fa-solid fa-users icon"></i>
                    <span class="label">Customer Info</span>
                </a>

                <a href="${pageContext.request.contextPath}/customerservice/appointment-list"
                   class="menu-item"
                   title="Appointment">
                    <i class="fa-solid fa-calendar-check icon"></i>
                    <span class="label">Appointment</span>
                </a>

                <a href="${pageContext.request.contextPath}/customerservice/view-support-request"
                   class="menu-item"
                   title="Support Request">
                    <i class="fa-solid fa-headset icon"></i>
                    <span class="label">Support Request</span>
                </a>

                <a href="${pageContext.request.contextPath}/customerservice/requests"
                   class="menu-item"
                   title="Service Request">
                    <i class="fa-solid fa-envelope-open-text icon"></i>
                    <span class="label">Service Request</span>
                </a>
            </nav>
        </div>

        <!-- Bottom Section: Profile + Logout -->
        <div class="bottom-section">
            <a href="${pageContext.request.contextPath}/user/profile"
               class="menu-item"
               title="Profile">
                <i class="fa-solid fa-user icon"></i>
                <span class="label">Profile</span>
            </a>

            <a href="${pageContext.request.contextPath}/Home?action=logout"
               class="menu-item logout"
               title="Logout">
                <i class="fa-solid fa-right-from-bracket icon"></i>
                <span class="label">Logout</span>
            </a>
        </div>
    </div>
</div>

<!-- Overlay for mobile -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<script src="${pageContext.request.contextPath}/assets/js/customerservice/sidebar.js"></script>