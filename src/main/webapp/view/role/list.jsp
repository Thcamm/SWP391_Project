<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/6/2025
  Time: 10:01 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<h1>List Roles</h1>

<c:if test = "${not empty sessionScope.flash}">
    <div style="color: green; font-weight: bold;">
        ${sessionScope.flash}
        <c:remove var="flash" scope="session"/>
    </div>
</c:if>

<c:if test="${not empty sessionScope.error}">
    <div style="color: red; font-weight: bold;">
        ${sessionScope.error}
        <c:remove var="error" scope="session"/>
    </div>

</c:if>
<div style="margin-bottom: 15px;">
    <p>
        <a href ="${pageContext.request.contextPath}/roles?action=new">
            Add New Role

        </a>

        <a href="${pageContext.request.contextPath}/rbac/roles"
        style="text-decoration: none; background-color: #2196F3; color: antiquewhite; padding: 6px 12px; border-radius: 5px;">
            Manage RBAC
        </a>
    </p>
</div>





<table border="1" style="width: 100%", border-collapse: collapse;>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Users</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="role" items="${pager.data}">
        <tr>
            <td>${role.roleId}</td>
            <td>${role.roleName}</td>
            <td>${role.userCount}</td>

            <td>
                <a href="${pageContext.request.contextPath}/roles?action=edit&id=${role.roleId}">
                    Edit
                </a>

                <a href="${pageContext.request.contextPath}/roles?action=delete&id=${role.roleId}"
                   onclick="return confirm('Are you sure you want to delete this role?');">
                    Delete

                </a>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty pager.data}">
        <tr>
            <td colspan = "3">Not found any roles</td>
        </tr>
    </c:if>
    </tbody>

</table>

<form action="${pageContext.request.contextPath}/roles" method="get" style="margin-bottom: 15px;">
    <input type="hidden" name="action" value="list">

    <label for="keyword">Search role: </label>
    <input type="text" id="keyword" name="keyword" value="${keyword}" placeholder="Enter role name..." style="padding: 3px; margin-right: 10px; ">
    <button type="submit">Search</button>

    <br>
    <label for="size">Show per page: </label>

    <select name="size" id="size" onchange="this.form.submit()">
        <option value="2" ${pager.itemsPerPage == 2 ? 'selected' : ''}>2</option>
        <option value="5" ${pager.itemsPerPage == 5 ? 'selected' : ''}>5</option>
        <option value="10" ${pager.itemsPerPage == 10 ? 'selected' : ''}>10</option>

    </select>

    <span> | Total: ${pager.totalItems} roles</span>

</form>
<div>"
    <c:if test="${pager.totalPages > 1}">
    <div class="pagination-container">
        <ul class="pagination">
            <c:set var="currentPage" value="${pager.currentPage}" />
            <c:set var="totalPages" value="${pager.totalPages}" />
                <c:set var="pageSize" value="${pager.itemsPerPage}" />
                <c:set var="baseUrl" value="${pageContext.request.contextPath}/roles?action=list&size=${pageSize}"/>

                <li class="<c:if test="${currentPage == 1}">disable</c:if>">
                    <c:choose>
                    <c:when test="${currentPage == 1}">
                        <span>&laquo; Before</span>
                        </c:when>
                        <c:otherwise>
                        <a href="${baseUrl}&page=${currentPage - 1}">&laquo; Before</a>
                        </c:otherwise>
                        </c:choose>
                </li>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="<c:if test="${i == currentPage}">active</c:if>">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span>${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${baseUrl}&page=${i}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>

                <li class="<c:if test="${currentPage == totalPages}">disable</c:if>">
                    <c:choose>
                        <c:when test="${currentPage == totalPages}">
                            <span>Next &raquo;</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${baseUrl}&page=${currentPage + 1}">Next &raquo;</a>
                        </c:otherwise>
                    </c:choose>
                </li>
        </ul>
    </div>
    </c:if>
</body>
</html>
