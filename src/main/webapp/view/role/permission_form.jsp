<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/11/2025
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="isEdit" value="${not empty perm}"/>
<c:set var="formTitle" value="${isEdit ? 'Update Permission' : 'Create New Permission'}"/>
<html>
<head>
    <title>${formTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/permission_form.css">

</head>
<body>
<div class="container form-container">
    <h1 class="page-title">${formTitle}</h1>

    <c:if test="${not empty error}">
        <div class="alert error-message">
            Error: ${error}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/permissions" method="post" class="main-form">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">

        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${perm.permId}">
            <div class="form-group">
                <p class="permission-id">Permission ID: <strong>${perm.permId}</strong></p>
            </div>
        </c:if>

        <div class="form-group">
            <label for="code" class="form-label">Permission Code:</label>
            <input type="text" id="code" name="code" required maxlength="100" class="form-control"
                   value="<c:out value="${isEdit ? perm.code : (not empty code ? code : '')}"/>"
                   placeholder="Enter permission code e.g. , user_view">
        </div>

        <div class="form-group">
            <label for="name" class="form-label">Permission Name:</label>
            <input type="text" id="name" name="name" required maxlength="100" class="form-control"
                   value="<c:out value='${isEdit ? perm.name : (not empty name ? name : "")}'/>"
                   placeholder="Enter readable name for permission">
        </div>


        <div class="form-group">
            <label for="category" class="form-label">Category:</label>
            <input type="text" id="category" name="category" maxlength="50" class="form-control"
                   value="<c:out value='${isEdit ? perm.category : (not empty category ? category : "")}'/>"
                   placeholder="e.g., USER, ORDER, SYSTEM">
        </div>


        <div class="form-group">
            <label for="description" class="form-label">Description:</label>
            <textarea id="description" name="description" rows="4" maxlength="255" class="form-control"
                      placeholder="Enter permission description (optional)"><c:out
                    value="${isEdit ? perm.description : (not empty description ? description : '')}"/></textarea>
        </div>


        <div class="form-group">
            <label for="active" class="form-label">Active:</label>
            <input type="checkbox" id="active" name="active"
                   <c:if test="${isEdit ? perm.active : true}">checked</c:if> />
            <span class="note">(Check to make permission active)</span>
        </div>


        <div class="form-actions">
            <input type="submit" value="${isEdit ? 'Update' : 'Create'}" class="btn btn-primary form-submit-btn">
            <a href="${pageContext.request.contextPath}/rbac/roles" class="btn btn-secondary">Cancel</a>
        </div>

    </form>
</div>

</body>
</html>
