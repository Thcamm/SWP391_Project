<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/functions"
prefix="fn" %>
<c:set var="uri" value="${pageContext.request.requestURI}" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin/base.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin/sidebar.css" />

<aside class="sidebar">
  <div class="brand">Admin</div>

  <div class="menu-section">
    <div class="menu-title">Main menu</div>

    <a class="menu-item ${activeMenu == 'Home' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">
      Home
    </a>

    <a
      class="menu-item ${activeMenu == 'rbac' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list">
      RBAC / Roles
    </a>

    <a
      class="menu-item ${activeMenu == 'reports' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/reports">
      Reports
    </a>
  </div>
</aside>
