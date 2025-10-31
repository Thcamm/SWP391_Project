<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="uri" value="${pageContext.request.requestURI}" />
<aside class="sidebar">
    <div class="brand">SealsCRM</div>

    <div class="menu-section">
        <div class="menu-title">Main menu</div>
        <a class="menu-item ${fn:contains(uri,'/technician/home') ? 'active' : ''}"
           href="${pageContext.request.contextPath}/technician/home">Home</a>
        <a class="menu-item ${fn:contains(uri,'/technician/tasks') ? 'active' : ''}"
           href="${pageContext.request.contextPath}/technician/tasks">Tasks</a>
        <a class="menu-item ${fn:contains(uri,'/technician/parts') ? 'active' : ''}"
           href="${pageContext.request.contextPath}/technician/parts">Parts</a>
        <a class="menu-item" href="${pageContext.request.contextPath}/technician/contacts">Contacts</a>
        <a class="menu-item" href="${pageContext.request.contextPath}/technician/settings">Settings</a>
    </div>
</aside>
