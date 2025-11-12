<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%-- Get active menu parameter --%>
<c:set var="activeMenu" value="${param.activeMenu}" />

<%-- Link CSS file --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/sidebar-techmanager.css" />

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
          class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}">
          <i class="bi bi-speedometer2"></i>
          <span>Dashboard</span>
        </a>
      </div>
    </div>

    <div class="nav-section-title"></div>
    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/service-requests"
        class="nav-link ${activeMenu == 'service-requests' ? 'active' : ''}">
        <i class="bi bi-clipboard-check"></i>
        <span>Service Requests</span>
      </a>
    </div>
    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
        class="nav-link ${activeMenu == 'assign-diagnosis' ? 'active' : ''}">
        <i class="bi bi-person-plus"></i>
        <span>Assign Diagnosis</span>
      </a>
    </div>

    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/assign-repair"
        class="nav-link ${activeMenu == 'assign-repair' ? 'active' : ''}">
        <i class="bi bi-tools"></i>
        <span>Assign Repair Tasks</span>
      </a>
    </div>

    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/team-management"
        class="nav-link ${activeMenu == 'team-management' ? 'active' : ''}">
        <i class="bi bi-people-fill"></i>
        <span>Team Management</span>
      </a>
    </div>

    <!-- Task Management Section -->
    <div class="nav-section-title">Task Management</div>

    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/overdue-tasks"
        class="nav-link ${activeMenu == 'overdue-tasks' ? 'active' : ''}">
        <i class="bi bi-exclamation-octagon text-danger"></i>
        <span>Overdue Tasks</span>
      </a>
    </div>

    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/declined-tasks"
        class="nav-link ${activeMenu == 'declined-tasks' ? 'active' : ''}">
        <i class="bi bi-person-x text-warning"></i>
        <span>Declined Tasks</span>
      </a>
    </div>

    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/reassign-tasks"
        class="nav-link ${activeMenu == 'reassign-tasks' ? 'active' : ''}">
        <i class="bi bi-arrow-repeat text-info"></i>
        <span>Reassign Tasks</span>
      </a>
    </div>

    <div class="nav-item">
      <a
        href="${pageContext.request.contextPath}/techmanager/rejected-tasks"
        class="nav-link ${activeMenu == 'rejected-tasks' ? 'active' : ''}">
        <i class="bi bi-x-circle text-danger"></i>
        <span>Rejected Tasks</span>
      </a>
    </div>
    <div class="nav-item">
      <a href="#" class="nav-link ${activeMenu == 'rejected-tasks' ? 'active' : ''}">
        <i class="bi bi-check-circle"></i>
        <span>Complete WorkOrders</span>
      </a>
    </div>
  </nav>

  <!-- Footer -->
  <div class="sidebar-footer">
    <div class="nav-item">
      <a href="${pageContext.request.contextPath}/Home?action=logout" class="nav-link">
        <i class="bi bi-box-arrow-right"></i>
        <span>Logout</span>
      </a>
    </div>
  </div>
</aside>
