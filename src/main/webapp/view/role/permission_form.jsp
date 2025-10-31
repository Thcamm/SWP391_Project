<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- === Page meta for header === --%>
<c:set var="isEdit"     value="${not empty perm}"/>
<c:set var="formTitle"  value="${isEdit ? 'Update Permission' : 'Create New Permission'}"/>
<c:set var="pageTitle"  value="${formTitle}" scope="request"/>
<c:set var="pageCSS"    value="role/permission_form.css" scope="request"/>
<c:set var="activeMenu" value="rbac" scope="request"/>

<jsp:include page="/view/admin/header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/permission_form.css">

<div class="layout"><%-- khung layout chuẩn: sidebar + main --%>
    <jsp:include page="/view/admin/sidebar.jsp"/>

    <main class="main">
        <div class="page-wrap">

            <div class="container form-container permission-form-page">
                <h1 class="page-title">${formTitle}</h1>

                <c:if test="${not empty requestScope.error}">
                    <div class="alert error-message">Error: ${requestScope.error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/admin/rbac/permissions"
                      method="post" class="main-form">
                    <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}"/>

                    <c:if test="${isEdit}">
                        <input type="hidden" name="id" value="${perm.permId}"/>
                        <div class="form-group">
                            <p class="permission-id">Permission ID: <strong>${perm.permId}</strong></p>
                        </div>
                    </c:if>

                    <!-- Permission Code -->
                    <div class="form-group">
                        <label for="code" class="form-label">Permission Code:</label>
                        <input type="text" id="code" name="code" required maxlength="100" class="form-control"
                               placeholder="Enter permission code (e.g., user_view)"
                               value="<c:out value='${isEdit ? perm.code : (not empty code ? code : "")}'/>"/>
                    </div>

                    <!-- Permission Name -->
                    <div class="form-group">
                        <label for="name" class="form-label">Permission Name:</label>
                        <input type="text" id="name" name="name" required maxlength="100" class="form-control"
                               placeholder="Readable permission name"
                               value="<c:out value='${isEdit ? perm.name : (not empty name ? name : "")}'/>"/>
                    </div>

                    <!-- Category -->
                    <div class="form-group">
                        <label for="category" class="form-label">Category:</label>
                        <input type="text" id="category" name="category" maxlength="50" class="form-control"
                               placeholder="e.g., USER, ORDER, SYSTEM"
                               value="<c:out value='${isEdit ? perm.category : (not empty category ? category : "")}'/>"/>
                    </div>

                    <!-- Description -->
                    <div class="form-group">
                        <label for="description" class="form-label">Description:</label>
                        <textarea id="description" name="description" rows="4" maxlength="255"
                                  class="form-control"
                                  placeholder="Enter permission description (optional)"><c:out value='${isEdit ? perm.description : (not empty description ? description : "")}'/></textarea>
                    </div>

                    <!-- Actions -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary form-submit-btn">
                            ${isEdit ? 'Update' : 'Create'}
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/rbac/permissions?action=list"
                           class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/view/admin/footer.jsp"/>
