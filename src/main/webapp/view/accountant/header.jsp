<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accountant Dashboard - Garage Management</title>

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
            font-weight: 700;
            color: #111827;
            display: flex;
            align-items: center;
            gap: 8px;
            text-decoration: none;
        }

        /* Center Navigation Desktop */
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
            transition: 0.2s;
        }
        .nav-link.active {
            background-color: var(--brand-color);
            color: white !important;
        }

        /* Dropdown Styles */
        .dropdown-menu {
            border-radius: 10px;
            padding: 0.5rem;
        }

        /* Mobile Navigation */
        @media (max-width: 992px) {
            .main-nav {
                position: static !important;
                transform: none;
            }
            .navbar-nav {
                flex-direction: column !important;
                border-top: 1px solid #e5e7eb;
                padding-top: 0.75rem;
            }
        }
    </style>
</head>

<body>

<header class="header">
    <div class="d-flex align-items-center justify-content-between position-relative">

        <!-- Logo -->
        <a href="${pageContext.request.contextPath}/accountant/home" class="logo">
            <i class="bi bi-tools"></i><span>Garage Management</span>
        </a>

        <!-- Mobile Toggle -->
        <button class="btn d-lg-none" data-bs-toggle="collapse" data-bs-target="#mobileMenu">
            <i class="bi bi-list" style="font-size: 22px;"></i>
        </button>

        <!-- Navigation -->
        <nav class="main-nav collapse d-lg-block" id="mobileMenu">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/accountant/home"
                       class="nav-link ${pageContext.request.requestURI.contains('/home') ? 'active' : ''}">
                        <i class="bi bi-house-door me-1"></i>Home
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/accountant/invoice"
                       class="nav-link ${pageContext.request.requestURI.contains('/invoice') ? 'active' : ''}">
                        <i class="bi bi-receipt me-1"></i>Invoices
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/accountant/payment"
                       class="nav-link ${pageContext.request.requestURI.contains('/payment') ? 'active' : ''}">
                        <i class="bi bi-cash-coin me-1"></i>Payments
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/accountant/report"
                       class="nav-link ${pageContext.request.requestURI.contains('/report') ? 'active' : ''}">
                        <i class="bi bi-graph-up me-1"></i>Reports
                    </a>
                </li>
            </ul>
        </nav>

        <!-- Right User Menu -->
        <div class="d-flex align-items-center gap-3">

            <!-- Notifications -->
            <div class="dropdown">
                <a class="nav-link" href="#" data-bs-toggle="dropdown">
                    <i class="bi bi-bell" style="font-size: 20px; color:#6b7280;"></i>
                </a>
                <ul class="dropdown-menu dropdown-menu-end" style="min-width: 260px;">
                    <li class="dropdown-header">Notifications</li>
                    <li><hr class="dropdown-divider"></li>
                    <li class="px-3 py-2 text-center text-muted">
                        <i class="bi bi-inbox" style="font-size: 2rem;"></i><br>
                        No new notifications
                    </li>
                </ul>
            </div>

            <!-- User Dropdown -->
            <div class="dropdown">
                <a class="d-flex align-items-center text-decoration-none" href="#" data-bs-toggle="dropdown">
                    <div class="text-end me-2">
                        <div style="font-size: 12px; color:#6b7280;">Welcome back,</div>
                        <div style="font-weight: 600; color:#111827;">${sessionScope.userName != null ? sessionScope.userName : 'User'}</div>
                        <div style="font-size: 12px; color:#6b7280;">${sessionScope.roleName != null ? sessionScope.roleName : 'Accountant'}</div>
                    </div>
                    <i class="bi bi-chevron-down"></i>
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Home?action=logout"><i class="bi bi-box-arrow-right me-2"></i>Log Out</a></li>
                </ul>
            </div>

        </div>
    </div>
</header>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
