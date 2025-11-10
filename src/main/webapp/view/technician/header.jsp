<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Technician Dashboard - Garage Management</title>

    <!-- Bootstrap CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

    <style>
        :root { --brand-color:#111827; --bg-light:#f6f7fb; }
        body { background-color:var(--bg-light); font-size:14px; margin:0; }

        /* Header = Navbar */
        .header {
            background:#fff;
            border-bottom:1px solid #e5e7eb;
            position:sticky; top:0; z-index:1020;
        }

        .navbar-brand {
            font-weight:700; color:#111827;
            display:flex; align-items:center; gap:.5rem;
            text-decoration:none;
        }

        /* Nav giữa – dùng flex-row thay vì absolute */
        .main-nav .nav-link {
            padding:.5rem .75rem !important;
            border-radius:10px;
            color:#12161c;
            transition:.2s;
            white-space:nowrap;
        }
        .main-nav .nav-link:hover { background:#f3f4f6; color:#12161c; }
        .main-nav .nav-link.active { background:var(--brand-color); color:#fff !important; }

        /* Dropdown & menu (nếu sau dùng) */
        .dropdown-menu {
            border:1px solid #e5e7eb;
            box-shadow:0 4px 6px -1px rgba(0,0,0,.1), 0 2px 4px -1px rgba(0,0,0,.06);
            border-radius:10px; padding:.5rem;
        }
        .dropdown-item { border-radius:8px; padding:.5rem .75rem; transition:.2s; display:flex; align-items:center; }
        .dropdown-item:hover { background:#f3f4f6; }
    </style>
</head>
<body>

<header class="header">
    <nav class="navbar navbar-expand-lg bg-white">
        <div class="container-fluid">
            <!-- Trái: Logo -->
            <a href="${pageContext.request.contextPath}/technician/home" class="navbar-brand">
                <i class="bi bi-tools"></i>
                <span>Garage Management</span>
            </a>

            <!-- Nút toggle cho mobile -->
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#topNav"
                    aria-controls="topNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <!-- Giữa: Navigation -->
            <div class="collapse navbar-collapse" id="topNav">
                <ul class="navbar-nav main-nav mx-lg-auto flex-row gap-2">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/technician/home" class="nav-link active">Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/technician/tasks" class="nav-link">My Tasks</a>
                    </li>
                </ul>

                <!-- Phải: Actions (My Profile, Log Out) -->
                <div class="d-flex ms-lg-auto align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary btn-sm">
                        <i class="bi bi-person me-1"></i> My Profile
                    </a>
                    <a href="${pageContext.request.contextPath}/Home?action=logout" class="btn btn-outline-danger btn-sm">
                        <i class="bi bi-box-arrow-right me-1"></i> Log Out
                    </a>
                </div>
            </div>
        </div>
    </nav>
</header>

<!-- Nội dung trang ở đây -->

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
