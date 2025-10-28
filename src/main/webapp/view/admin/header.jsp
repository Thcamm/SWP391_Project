<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Admin Header with Bootstrap -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <!-- Logo/Brand -->
    <a
      class="navbar-brand fw-bold"
      href="${pageContext.request.contextPath}/admin/users"
    >
      <i class="bi bi-gear-fill me-2"></i>
      CsCarSpa.vn
    </a>

    <!-- Mobile Toggle Button -->
    <button
      class="navbar-toggler"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#adminNavbar"
      aria-controls="adminNavbar"
      aria-expanded="false"
      aria-label="Toggle navigation"
    >
      <span class="navbar-toggler-icon"></span>
    </button>

    <!-- Navigation Menu -->
    <div class="collapse navbar-collapse" id="adminNavbar">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a
            class="nav-link ${activeMenu == 'Home' ? 'active fw-bold' : ''}"
            href="${pageContext.request.contextPath}/admin/users"
          >
            <i class="bi bi-house-door me-1"></i>Home
          </a>
        </li>

        <!-- RBAC Management Dropdown -->
        <li class="nav-item dropdown">
          <a
            class="nav-link dropdown-toggle ${activeMenu == 'rbac' ? 'active fw-bold' : ''}"
            href="#"
            role="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <i class="bi bi-shield-lock me-1"></i>RBAC Management
          </a>
          <ul class="dropdown-menu">
            <li>
              <a
                class="dropdown-item"
                href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list"
              >
                <i class="bi bi-list-ul me-2"></i>Roles
              </a>
            </li>
            <li>
              <a
                class="dropdown-item"
                href="${pageContext.request.contextPath}/admin/rbac/roles"
              >
                <i class="bi bi-key me-2"></i>Role & Permissions
              </a>
            </li>
          </ul>
        </li>

        <!-- Employees -->
        <li class="nav-item">
          <a
            class="nav-link ${activeMenu == 'employees' ? 'active fw-bold' : ''}"
            href="${pageContext.request.contextPath}/techmanager/service-requests"
          >
            <i class="bi bi-person-badge me-1"></i>Tech Manager
          </a>
        </li>

        <!-- Reports -->
        <li class="nav-item">
          <a
            class="nav-link ${activeMenu == 'reports' ? 'active fw-bold' : ''}"
            href="${pageContext.request.contextPath}/admin/reports"
          >
            <i class="bi bi-graph-up me-1"></i>Reports
          </a>
        </li>
      </ul>

      <!-- User Info & Logout -->
      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <a
            class="nav-link dropdown-toggle"
            href="#"
            role="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <i class="bi bi-person-circle me-1"></i>
            <span class="d-none d-lg-inline">${sessionScope.userName}</span>
            <span class="badge bg-secondary ms-1"
              >${sessionScope.roleName}</span
            >
          </a>
          <ul class="dropdown-menu dropdown-menu-end">
            <li>
              <a
                class="dropdown-item"
                href="${pageContext.request.contextPath}/user/profile"
              >
                <i class="bi bi-person me-2"></i>My Profile
              </a>
            </li>
            <li><hr class="dropdown-divider" /></li>
            <li>
              <a
                class="dropdown-item text-danger"
                href="${pageContext.request.contextPath}/Home?action=logout"
              >
                <i class="bi bi-box-arrow-right me-2"></i>Logout
              </a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
