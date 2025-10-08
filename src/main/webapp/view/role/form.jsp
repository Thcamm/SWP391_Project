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
<c:set var="formTitle" value="${isEdit ? 'update role': 'create new role'}"/>
<html>
<head>
    <title>${formTitle}</title>
</head>
<body>

<h1>
    ${formTitle}
</h1>

<c:if test="${not empty error}">
    <div style="color: red; border: 1px solid red; padding: 10px; margin-bottom: 15px;">
        Error: ${error}
    </div>
</c:if>

<form action="${actionUrl}" method="post">
    <input type="hidden" name="action" value="${formAction}">

    <c:if test="${isEdit}">
        <input type="hidden" name="id" value="${role.roleId}">
        <p>Id role: <strong>${role.roleId}</strong></p>
    </c:if>

    <label for="name">Name of role:</label> <br>
    <input type="text" id="name" name="name" required maxlength="50"
           value="<c:out value="${isEdit ? role.roleName : (not empty name ? name : '')}"/>">
    <br><br>
    <input type="submit" value="${isEdit ? 'Update' : 'Create new'}">
    <a href="${pageContext.request.contextPath}/roles?action=list">Cancel</a>

</form>

</body>
</html>
