<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 9/30/2025
  Time: 11:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h2>Role -> Permission (RBAC)</h2>
<c:if test = "${param.saved == '1'}">
    <div class="msg"> Save permission successfully!</div>
</c:if>

<c:if test = "${empty roles}">
    <div class="msg danger">
        No roles found in the system. Please contact the administrator.

    </div>
</c:if>

<form class="row" method="get" action="${pageContext.request.contextPath}/rbac/roles">
    <label>
        Role:&nbsp;
        <select name="roleId" onchange="this.form.submit()">
            <c:forEach var="r" items="${roles}">
                <option value="${r.roleId}"
                        <c:if test="${r.roleId == roleId}">selected</c:if>>
                        ${r.roleName}
                </option>
            </c:forEach>
        </select>
    </label>

    <label>
        Keyword:&nbsp;
        <input type="text" name="keyword" value="${param.keyword}" placeholder="Find name permission ...">

    </label>

    <label>
        Category:&nbsp;
        <input type="text" name="category" value="${param.category}" placeholder="eg: USERm, ORDER ...">

    </label>

    <button class="btn outline" type="submit">Filter</button>
    <span class="right muted">
        <c:out value="${fn:length(perms)}"/> permissions found.

    </span>

</form>

<form method="post" action="${pageContext.request.contextPath}/rbac/roles/save">
    <input type="hidden" name="roleId" value="${roleId}"/>

    <table>
        <thead>
        <tr>
            <th>#</th>
            <th>Permission</th>
            <th>Name</th>
            <th>Category</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${perms}" varStatus="st">
            <tr>
                <td>
                    <!-- ĐỔI: name="permIds" và SỬA attribute: checkedPermsIds -->
                    <input type="checkbox" name="permIds" value="${p.permId}"
                           <c:if test="${checkedPermsIds.contains(p.permId)}">checked</c:if> />
                </td>
                <td><b>${p.code}</b></td>
                <td><b>${p.name}</b></td>
                <td><span class="badge"><c:out value="${p.category}"/></span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="toolbar">
        <button class="btn" type="submit">Save</button>
        <a class="btn outline" href="${pageContext.request.contextPath}/rbac/roles?roleId=${roleId}">Reset</a>
    </div>
</form>

</body>
</html>
