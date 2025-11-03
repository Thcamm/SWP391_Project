<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- Get active menu
parameter --%>
<c:set var="activeMenu" value="${param.activeMenu}" />

<%-- Link CSS file --%>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/assets/css/techmanager/sidebar-techmanager.css"
/>

<aside class="sidebar">
  <!-- Header -->
  <div class="sidebar-header">
    <div style="text-align: center">
      <i class="bi bi-gear-fill" style="font-size: 2rem"></i>
      <h5>Tech Manager</h5>
      <div class="user-role">${sessionScope.user.fullName}</div>
    </div>
  </div>

  <!-- Navigation -->
  <nav class="sidebar-nav">
    <!-- Dashboard -->
    <div class="nav-section">
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/dashboard"
          class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}"
        >
          <i class="bi bi-speedometer2"></i>
          <span>Dashboard</span>
        </a>
      </div>
    </div>

    <!-- Phase 1: Reception & Diagnosis -->
    <div class="nav-section">
      <div class="nav-section-title">
        <span>Phase 1: Reception & Diagnosis</span>
        <span class="phase-badge active-1">Active</span>
      </div>
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/service-requests"
          class="nav-link ${activeMenu == 'service-requests' ? 'active' : ''}"
        >
          <i class="bi bi-clipboard-check"></i>
          <span>Service Requests</span>
        </a>
      </div>
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
          class="nav-link ${activeMenu == 'assign-diagnosis' ? 'active' : ''}"
        >
          <i class="bi bi-person-plus"></i>
          <span>Assign Diagnosis</span>
        </a>
      </div>
    </div>

    <!-- Phase 2: Review & Quote -->
    <div class="nav-section">
      <div class="nav-section-title">
        <span>Phase 2: Review & Quote</span>
        <span class="phase-badge active-2">Active</span>
      </div>
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/diagnosis-review"
          class="nav-link ${activeMenu == 'diagnosis-review' ? 'active' : ''}"
        >
          <i class="bi bi-file-earmark-text"></i>
          <span>Review Diagnosis</span>
        </a>
      </div>
    </div>

    <!-- Phase 3: Repair Assignment -->
    <div class="nav-section">
      <div class="nav-section-title">
        <span>Phase 3: Repair Assignment</span>
        <span class="phase-badge active-2">Active</span>
      </div>
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/assign-repair"
          class="nav-link ${activeMenu == 'assign-repair' ? 'active' : ''}"
        >
          <i class="bi bi-tools"></i>
          <span>Assign Repair Tasks</span>
        </a>
      </div>
    </div>

    <!-- Phase 4: Monitor & Complete -->
    <div class="nav-section">
      <div class="nav-section-title">
        <span>Phase 4: Monitor & Complete</span>
        <span class="phase-badge soon">Soon</span>
      </div>
      <div class="nav-item">
        <a href="#" class="nav-link disabled">
          <i class="bi bi-list-task"></i>
          <span>Monitor Progress</span>
          <span class="badge-soon">Soon</span>
        </a>
      </div>
      <div class="nav-item">
        <a href="#" class="nav-link disabled">
          <i class="bi bi-check-circle"></i>
          <span>Complete WorkOrders</span>
          <span class="badge-soon">Soon</span>
        </a>
      </div>
    </div>

    <!-- Management -->
    <div class="nav-section">
      <div class="nav-section-title">
        <span>Management</span>
      </div>
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/rejected-tasks"
          class="nav-link ${activeMenu == 'rejected-tasks' ? 'active' : ''}"
        >
          <i class="bi bi-x-circle text-danger"></i>
          <span>Rejected Tasks</span>
        </a>
      </div>
      <div class="nav-item">
        <a
          href="${pageContext.request.contextPath}/techmanager/reports"
          class="nav-link ${activeMenu == 'reports' ? 'active' : ''}"
        >
          <i class="bi bi-bar-chart"></i>
          <span>Reports</span>
        </a>
      </div>
    </div>
  </nav>

  <!-- Footer -->
  <div class="sidebar-footer">
    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/Home?action=logout"
        class="nav-link"
      >
        <i class="bi bi-box-arrow-right"></i>
        <span>Logout</span>
      </a>
    </div>
  </div>
</aside>
