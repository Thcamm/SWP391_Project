<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/functions"
prefix="fn" %> <%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/header-techmanager.css" />

<header class="header techmanager-header">
  <div class="header-left">
    <div class="logo">
      <i class="bi bi-gear-fill"></i>
      <h1>Tech Manager Portal</h1>
    </div>
  </div>

  <nav class="main-nav">
    <ul>
      <li>
        <a
          href="${pageContext.request.contextPath}/techmanager/dashboard"
          class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}">
          <i class="bi bi-speedometer2"></i>
          Dashboard
        </a>
      </li>
      <li>
        <a
          href="${pageContext.request.contextPath}/techmanager/service-requests"
          class="nav-link ${activeMenu == 'service-requests' ? 'active' : ''}">
          <i class="bi bi-clipboard-check"></i>
          Service Requests
        </a>
      </li>
    </ul>
  </nav>

  <div class="header-right">
    <div class="user-info">
      <div class="nav-item dropdown">
        <a
          class="nav-link dropdown-toggle"
          href="#"
          id="userDropdown"
          role="button"
          data-bs-toggle="dropdown"
          aria-expanded="false">
          <i class="bi bi-person-circle"></i>
          ${sessionScope.user.fullName}
        </a>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
          <li>
            <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
              <i class="bi bi-person"></i>
              Profile
            </a>
          </li>
          <li><hr class="dropdown-divider" /></li>
          <li>
            <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Home?action=logout">
              <i class="bi bi-box-arrow-right"></i>
              Logout
            </a>
          </li>
        </ul>
      </div>
    </div>
  </div>
</header>
