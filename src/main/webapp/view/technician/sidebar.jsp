<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="uri" value="${pageContext.request.requestURI}" />
<style>
    .sidebar{padding:20px;border-right:1px solid var(--border);background:#fff}
    .brand{font-weight:700;font-size:18px;margin-bottom:20px}
    .menu-section{margin-top:10px}
    .menu-title{font-size:12px;color:var(--muted);margin:12px 0 6px}
    .menu-item{display:block;padding:10px 12px;border-radius:10px;margin-bottom:6px}
    .menu-item:hover{background:#f2f3f8}
    .menu-item.active{background:#111827;color:#fff}
    .sidebar-footer{position:sticky;top:100%;padding-top:16px}
    .btn-logout{width:100%;padding:10px 12px;border:1px solid var(--border);background:#fff;border-radius:10px;cursor:pointer}
    .btn-logout:hover{background:#f9fafb}

</style>
<aside class="sidebar">
    <div class="brand">SealsCRM</div>

    <div class="menu-section">
        <div class="menu-title">Main menu</div>
        <a class="menu-item ${fn:contains(uri,'/technician/home') ? 'active' : ''}"
           href="${pageContext.request.contextPath}/technician/home">Home</a>
        <a class="menu-item ${fn:contains(uri,'/technician/tasks') ? 'active' : ''}"
           href="${pageContext.request.contextPath}/technician/tasks">Tasks</a>

    </div>
</aside>
