<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/6/2025
  Time: 10:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="isEdit" value="${mode == 'edit'}"/>
<c:set var = "actionUrl" value="${pageContext.request.contextPath}/roles"/>
<c:set var="formAction" value="${isEdit ? 'update':'create'}"/>
<c:set var="formTitle" value="${isEdit ? 'Update role': 'Create new role'}"/>
<html>
<head>
    <title>${formTitle}</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/form.css">
</head>
<body class="role-form-page">

<div class="container form-container">
    <h1 class="page-title">
        ${formTitle}
    </h1>

    <c:if test="${not empty error}">
        <div class="alert error-message">
            Error: ${error}
        </div>
    </c:if>

    <form action="${actionUrl}" method="post" class="main-form">
        <input type="hidden" name="action" value="${formAction}">

        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${role.roleId}">
            <div class="form-group">
                <p class="role-id">Role ID: <strong>${role.roleId}</strong></p>
            </div>
        </c:if>

        <div class="form-group">
            <label for="name" class="form-label">Name of role:</label>
            <input type="text" id="name" name="name" required maxlength="50" class="form-control"
                   value="<c:out value="${isEdit ? role.roleName : (not empty name ? name : '')}"/>"
                   placeholder="Enter name of role">
        </div>

        <div class="form-group">
            <label for="description" class="form-label">Description: </label>
            <textarea id="description" name="description" rows="4" maxlength="255" class="form-control"
                      placeholder="Enter description (optional)"><c:out value="${isEdit ? role.description : (not empty description ? description : '')}"/></textarea>

        </div>

        <div class="form-actions">
            <input type="submit" value="${isEdit ? 'update' : 'create'}" class="btn btn-primary form-submit-btn">
            <a href="${pageContext.request.contextPath}/roles?action=list" class="btn btn-secondary">Cancel</a>
        </div>

    </form>
</div>

</body>
</html>
