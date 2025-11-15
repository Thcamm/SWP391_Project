<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Storekeeper Dashboard - Garage Management</title>

    <!-- Bootstrap CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        :root {
            --brand-color:#111827;
            --bg-light:#f6f7fb;
        }
        body {
            background-color:var(--bg-light);
            font-size:14px;
            margin:0;
        }

        /* Header = Navbar */
        .header {
            background:#fff;
            border-bottom:1px solid #e5e7eb;
            position:sticky;
            top:0;
            z-index:1020;
        }

        .navbar-brand {
            font-weight:700;
            color:#111827;
            display:flex;
            align-items:center;
            gap:.5rem;
            text-decoration:none;
        }

        .navbar-brand:hover {
            color:#111827;
        }

        /* Nav */
        .main-nav .nav-link {
            padding:.5rem .75rem !important;
            border-radius:10px;
            color:#12161c;
            transition:.2s;
            white-space:nowrap;
        }
        .main-nav .nav-link:hover {
            background:#f3f4f6;
            color:#12161c;
        }
        .main-nav .nav-link.active {
            background:var(--brand-color);
            color:#fff !important;
        }

        /* Dropdown */
        .dropdown-menu {
            border:1px solid #e5e7eb;
            box-shadow:0 4px 6px -1px rgba(0,0,0,.1), 0 2px 4px -1px rgba(0,0,0,.06);
            border-radius:10px;
            padding:.5rem;
        }
        .dropdown-item {
            border-radius:8px;
            padding:.5rem .75rem;
            transition:.2s;
            display:flex;
            align-items:center;
        }
        .dropdown-item:hover {
            background:#f3f4f6;
        }
    </style>
</head>
<body>

<!-- Header/Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-white header">
    <div class="container-fluid px-3">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/inventory?action=list">
            <i class="bi bi-box-seam"></i>
            <span>Inventory System</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto main-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                       data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-person-circle"></i> ${sessionScope.user.fullName}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                            <i class="bi bi-person"></i> Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Home?action=logout">
                            <i class="bi bi-box-arrow-right"></i> Logout</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

