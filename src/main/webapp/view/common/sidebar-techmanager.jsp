<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav
  id="sidebarMenu"
  class="col-md-3 col-lg-2 d-md-block bg-dark sidebar collapse"
>
  <div class="position-sticky pt-3">
    <div class="text-center text-white mb-3">
      <i class="bi bi-gear-fill" style="font-size: 2rem"></i>
      <h5>Tech Manager</h5>
      <p class="small">${sessionScope.user.fullName}</p>
    </div>

    <ul class="nav flex-column">
      <!-- Dashboard -->
      <li class="nav-item">
        <a
          class="nav-link text-white"
          href="${pageContext.request.contextPath}/techmanager/dashboard"
        >
          <i class="bi bi-speedometer2"></i> Dashboard
        </a>
      </li>

      <!-- Phase 1: Service Request Approval & Diagnosis Assignment -->
      <li class="nav-item">
        <h6
          class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted"
        >
          <span>Phase 1: Reception & Diagnosis</span>
        </h6>
      </li>
      <li class="nav-item">
        <a
          class="nav-link text-white ${pageContext.request.servletPath == '/view/techmanager/service-requests.jsp' ? 'active bg-primary' : ''}"
          href="${pageContext.request.contextPath}/techmanager/service-requests"
        >
          <i class="bi bi-clipboard-check"></i> Approve Service Requests
        </a>
      </li>
      <li class="nav-item">
        <a
          class="nav-link text-white ${pageContext.request.servletPath == '/view/techmanager/assign-diagnosis.jsp' ? 'active bg-primary' : ''}"
          href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
        >
          <i class="bi bi-person-plus"></i> Assign Diagnosis Tasks
        </a>
      </li>

      <!-- Phase 2: Diagnosis Review & Quote (Coming Soon) -->
      <li class="nav-item">
        <h6
          class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted"
        >
          <span>Phase 2: Review & Quote</span>
        </h6>
      </li>
      <li class="nav-item">
        <a class="nav-link text-white disabled" href="#" tabindex="-1">
          <i class="bi bi-file-earmark-text"></i> Review Diagnosis Reports
          <span class="badge bg-secondary">Soon</span>
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link text-white disabled" href="#" tabindex="-1">
          <i class="bi bi-cash-coin"></i> Create Quotes
          <span class="badge bg-secondary">Soon</span>
        </a>
      </li>

      <!-- Phase 3: Repair Assignment (Coming Soon) -->
      <li class="nav-item">
        <h6
          class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted"
        >
          <span>Phase 3: Repair Assignment</span>
        </h6>
      </li>
      <li class="nav-item">
        <a class="nav-link text-white disabled" href="#" tabindex="-1">
          <i class="bi bi-tools"></i> Assign Repair Tasks
          <span class="badge bg-secondary">Soon</span>
        </a>
      </li>

      <!-- Phase 4: Monitoring & Completion (Coming Soon) -->
      <li class="nav-item">
        <h6
          class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted"
        >
          <span>Phase 4: Monitor & Complete</span>
        </h6>
      </li>
      <li class="nav-item">
        <a class="nav-link text-white disabled" href="#" tabindex="-1">
          <i class="bi bi-list-task"></i> Monitor Work Progress
          <span class="badge bg-secondary">Soon</span>
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link text-white disabled" href="#" tabindex="-1">
          <i class="bi bi-check-circle"></i> Complete WorkOrders
          <span class="badge bg-secondary">Soon</span>
        </a>
      </li>

      <!-- Reports & Settings -->
      <li class="nav-item">
        <h6
          class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted"
        >
          <span>Other</span>
        </h6>
      </li>
      <li class="nav-item">
        <a
          class="nav-link text-white"
          href="${pageContext.request.contextPath}/techmanager/reports"
        >
          <i class="bi bi-bar-chart"></i> Reports
        </a>
      </li>
    </ul>

    <!-- Logout -->
    <div class="position-absolute bottom-0 w-100 mb-3">
      <ul class="nav flex-column">
        <li class="nav-item">
          <a
            class="nav-link text-white"
            href="${pageContext.request.contextPath}/logout"
          >
            <i class="bi bi-box-arrow-right"></i> Logout
          </a>
        </li>
      </ul>
    </div>
  </div>
</nav>

<style>
  .sidebar {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    z-index: 100;
    padding: 48px 0 0;
    box-shadow: inset -1px 0 0 rgba(0, 0, 0, 0.1);
  }

  .sidebar .nav-link {
    font-weight: 500;
    color: #ffffff;
    padding: 0.75rem 1rem;
    border-radius: 5px;
    margin: 0.2rem 0.5rem;
  }

  .sidebar .nav-link:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  .sidebar .nav-link.active {
    background-color: #0d6efd !important;
  }

  .sidebar .nav-link.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .sidebar-heading {
    font-size: 0.75rem;
    text-transform: uppercase;
  }
</style>
