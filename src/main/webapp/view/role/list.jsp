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
    <title>Role Management</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/list.css">
</head>
<body class="role-list-page">

<div class="container">
    <h1 class="page-title">List role</h1>


    <div class="message-container">
        <c:if test = "${not empty sessionScope.flash}">
            <div class="alert success-message">
                    ${sessionScope.flash}
                <c:remove var="flash" scope="session"/>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.error}">
            <div class="alert error-message">
                    ${sessionScope.error}
                <c:remove var="error" scope="session"/>
            </div>
        </c:if>
    </div>


    <div class="action-buttons-container">
        <a href ="${pageContext.request.contextPath}/roles?action=new" class="btn btn-primary">
            Add New Role
        </a>

        <a href="${pageContext.request.contextPath}/rbac/roles" class="btn btn-secondary">
            Manage Role Permissions
        </a>
    </div>


    <form action="${pageContext.request.contextPath}/roles" method="get" class="search-form">
        <input type="hidden" name="action" value="list">

        <div class="form-group search-input-group">
            <label for="keyword">Search </label>
            <input type="text" id="keyword" name="keyword" value="${keyword}" placeholder="Enter name of role" class="form-control">
            <button type="submit" class="btn btn-search">Search</button>
        </div>

        <div class="form-group page-config-group">
            <label for="size">Number/Page: </label>
            <select name="size" id="size" onchange="this.form.submit()" class="form-control select-size">
                <option value="2" ${pager.itemsPerPage == 2 ? 'selected' : ''}>2</option>
                <option value="5" ${pager.itemsPerPage == 5 ? 'selected' : ''}>5</option>
                <option value="10" ${pager.itemsPerPage == 10 ? 'selected' : ''}>10</option>
            </select>
        </div>

        <span class="total-items">Sum: ${pager.totalItems} role(s)</span>
    </form>


    <div class="table-responsive">
        <table class="data-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Name of Role</th>
                <th>NO User(s)</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="role" items="${pager.data}">
                <tr>
                    <td>${role.roleId}</td>
                    <td>${role.roleName}</td>
                    <td>${role.userCount}</td>
                    <td class="action-column">
                        <a href="${pageContext.request.contextPath}/roles?action=edit&id=${role.roleId}" class="action-link edit-link">
                            Edit
                        </a>
                        <a href="${pageContext.request.contextPath}/roles?action=delete&id=${role.roleId}"
                           onclick="return confirm('Are you sure to delete this role');" class="action-link delete-link">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty pager.data}">
                <tr>
                    <td colspan = "4" class="no-data">Not found any role</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>


    <c:if test="${pager.totalPages > 1}">
        <div class="pagination-container">
            <ul class="pagination">
                <c:set var="currentPage" value="${pager.currentPage}" />
                <c:set var="totalPages" value="${pager.totalPages}" />
                <c:set var="pageSize" value="${pager.itemsPerPage}" />
                <c:set var="keywordParam" value="${not empty keyword ? '&keyword=' : ''}"/>
                <c:set var="baseUrl" value="${pageContext.request.contextPath}/roles?action=list&size=${pageSize}${keywordParam}${keyword}"/>

                <li class="page-item <c:if test="${currentPage == 1}">disabled</c:if>">
                    <c:choose>
                        <c:when test="${currentPage == 1}">
                            <span class="page-link">&laquo; Before</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${baseUrl}&page=${currentPage - 1}" class="page-link">&laquo; Before</a>
                        </c:otherwise>
                    </c:choose>
                </li>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item <c:if test="${i == currentPage}">active</c:if>">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span class="page-link">${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${baseUrl}&page=${i}" class="page-link">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>

                <li class="page-item <c:if test="${currentPage == totalPages}">disabled</c:if>">
                    <c:choose>
                        <c:when test="${currentPage == totalPages}">
                            <span class="page-link">After &raquo;</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${baseUrl}&page=${currentPage + 1}" class="page-link">After &raquo;</a>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </div>
    </c:if>
</div>
</body>
</html>
