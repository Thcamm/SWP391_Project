

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Technician Dashboard - Garage Management</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <style>
            :root {
                --brand-color: #111827;
                --bg-light: #f6f7fb;
            }
            body {
                background-color: var(--bg-light);
                font-size: 14px;
                margin: 0;
            }
            .header {
                background: white;
                border-bottom: 1px solid #e5e7eb;
                padding: 0.75rem 1.5rem;
                position: sticky;
                top: 0;
                z-index: 1020;
            }
            .logo {
                font-size: 18px;
                margin: 0;
                font-weight: 700;
                color: #111827;
                display: flex;
                align-items: center;
                gap: 8px;
                text-decoration: none;
            }

            /* Navigation ở giữa */
            .main-nav {
                position: absolute;
                left: 50%;
                transform: translateX(-50%);
            }

            .main-nav .navbar-nav {
                flex-direction: row;
                gap: 0.5rem;
            }

            .nav-link {
                padding: 0.5rem 0.75rem !important;
                border-radius: 10px;
                color: #12161c;
                transition: all 0.2s;
                white-space: nowrap;
            }
            .nav-link:hover {
                background-color: #f3f4f6;
                color: #12161c;
            }
            .nav-link.active {
                background-color: var(--brand-color);
                color: white !important;
            }

            /* Dropdown styles */
            .dropdown-menu {
                border: 1px solid #e5e7eb;
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
                border-radius: 10px;
                padding: 0.5rem;
            }
            .dropdown-item {
                border-radius: 8px;
                padding: 0.5rem 0.75rem;
                transition: all 0.2s;
                display: flex;
                align-items: center;
            }
            .dropdown-item:hover {
                background-color: #f3f4f6;
            }
            .dropdown-header {
                padding: 0.5rem 0.75rem;
                font-weight: 600;
                color: #111827;
            }
            .dropdown-divider {
                margin: 0.5rem 0;
            }

            @media (max-width: 992px) {
                .main-nav {
                    position: static;
                    transform: none;
                }
            }
        </style>
    </head>
    <body>

    <header class="header">
        <div class="d-flex align-items-center justify-content-between position-relative">
            <!-- Logo bên trái -->
            <a href="${pageContext.request.contextPath}/technician/home" class="logo">
                <i class="bi bi-tools"></i>
                <span>Garage Management</span>
            </a>

            <!-- Navigation ở giữa -->
            <nav class="main-nav d-none d-lg-block">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/technician/home" class="nav-link active">Dashboard</a>

                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/technician/tasks" class="nav-link">My Tasks</a>
                    </li>

                </ul>
            </nav>

            <!-- User info bên phải -->
            <div class="d-flex align-items-center gap-3">
                <!-- Notification Bell -->
                <div class="dropdown">
                    <a class="nav-link position-relative" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" style="padding: 0.5rem;">
                        <i class="bi bi-bell" style="font-size: 20px; color: #6b7280;"></i>
                        <!-- Uncomment nếu có notifications -->
                        <%-- <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 10px;">
                            ${notificationCount}
                        </span> --%>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" style="min-width: 280px;">
                        <li><h6 class="dropdown-header">Notifications</h6></li>
                        <li><hr class="dropdown-divider"></li>
                        <li class="px-3 py-2 text-center text-muted">
                            <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                            <p class="mb-0 mt-2">No new notifications</p>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-center small" href="${pageContext.request.contextPath}/accountant/notifications">View all notifications</a></li>
                    </ul>
                </div>

                <!-- User Dropdown -->
                <div class="dropdown">
                    <a class="d-flex align-items-center text-decoration-none dropdown-toggle-custom"
                       href="#"
                       role="button"
                       data-bs-toggle="dropdown"
                       aria-expanded="false"
                       style="padding: 0.5rem 0.75rem; border-radius: 10px; transition: background-color 0.2s;">
                        <div class="text-end">
                            <div style="font-size: 12px; color: #6b7280; margin-bottom: 2px;">Welcome back,</div>
                            <div style="font-weight: 600; color: #111827; line-height: 1.2;">${sessionScope.userName != null ? sessionScope.userName : 'User'}</div>
                            <div style="font-size: 12px; color: #6b7280; margin-top: 2px;">${sessionScope.roleName != null ? sessionScope.roleName : 'Technician'}</div>
                        </div>
                        <i class="bi bi-chevron-down ms-2" style="font-size: 12px; color: #6b7280;"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" style="min-width: 200px;">
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                <i class="bi bi-person me-2"></i>
                                <span>My Profile</span>
                            </a>
                        </li>
<%--                        <li>--%>
<%--                            <a class="dropdown-item" href="${pageContext.request.contextPath}/user/settings">--%>
<%--                                <i class="bi bi-gear me-2"></i>--%>
<%--                                <span>Settings</span>--%>
<%--                            </a>--%>
<%--                        </li>--%>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Home?action=logout">
                                <i class="bi bi-box-arrow-right me-2"></i>
                                <span>Log Out</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </header>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
