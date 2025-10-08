<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  File: rbac_manage.jsp
  Description: Role-Based Access Control (RBAC) Management
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Role Permission Management (RBAC)</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/role-page.css">

</head>
<body>
<h2>Role -> Permission (RBAC)</h2>


<a href="${pageContext.request.contextPath}/roles?action=list"
   style="display: inline-block; margin-bottom: 10px; text-decoration: none; background-color: #777; color: white; padding: 6px 12px; border-radius: 4px;">
    ⬅ Back to Role List
</a>
<c:if test = "${param.saved == '1'}">
    <div class="msg"> Save permission successfully!</div>
</c:if>

<div class="container rbac-container">
    <h1 class="page-title">Role -> Permission (RBAC)</h1>

    <div class="back-link-container">
        <a href="${pageContext.request.contextPath}/roles?action=list" class="btn btn-back">
            &laquo; Back to Role List
        </a>
    </div>


    <c:if test = "${param.saved == '1'}">
        <div class="alert success-message"> Save permission successfully!</div>
    </c:if>

    <c:if test = "${empty roles}">
        <div class="alert error-message">
            No roles found in the system. Please contact the administrator.
        </div>
    </c:if>


    <form class="filter-form" method="get" action="${pageContext.request.contextPath}/rbac/roles">

        <div class="form-group role-select-group">
            <label class="form-label">
                Role:&nbsp
                <select name="roleId" onchange="this.form.submit()" class="form-control select-role">
                    <c:forEach var="r" items="${roles}">
                        <option value="${r.roleId}"
                                <c:if test="${r.roleId == roleId}">selected</c:if>>
                                ${r.roleName}
                        </option>
                    </c:forEach>
                </select>
            </label>
        </div>

        <div class="form-group">
            <label class="form-label">
                Keyword:&nbsp
                <input type="text" name="keyword" value="${param.keyword}" placeholder="Find name permission ..." class="form-control">
            </label>
        </div>

        <div class="form-group">
            <label class="form-label">
                Category:&nbsp
                <input type="text" name="category" value="${param.category}" placeholder="eg: USERm, ORDER ..." class="form-control">
            </label>
        </div>

        <button class="btn btn-search" type="submit">Filter</button>

        <span class="total-count right muted">
                <c:out value="${fn:length(perms)}"/> permissions found.
            </span>

    </form>


    <form method="post" action="${pageContext.request.contextPath}/rbac/roles/save" class="permission-form">
        <input type="hidden" name="roleId" value="${roleId}"/>

        <div class="table-responsive">
            <table class="data-table permission-table">
                <thead>
                <tr>
                    <th class="col-checkbox">#</th>
                    <th>Permission</th>
                    <th>Name</th>
                    <th>Category</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${pager.data}" varStatus="st">
                    <tr class="<c:if test="${checkedPermsIds.contains(p.permId)}">checked-row</c:if>">
                        <td class="col-checkbox">
                            <input type="checkbox" name="permIds" value="${p.permId}" class="perm-checkbox"
                                   <c:if test="${checkedPermsIds.contains(p.permId)}">checked</c:if> />
                        </td>
                        <td class="perm-code"><b>${p.code}</b></td>
                        <td class="perm-name">${p.name}</td>
                        <td class="perm-category"><span class="badge badge-category"><c:out value="${p.category}"/></span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="form-toolbar">
            <button class="btn btn-primary" type="submit">Save</button>
            <a class="btn btn-reset" href="${pageContext.request.contextPath}/rbac/roles?roleId=${roleId}">Reset</a>
        </div>
    </form>


    <div class="pagination-area">


        <div class="pagination">
            <c:if test="${pager.totalPages > 1}">
                <c:if test="${pager.currentPage > 1}">
                    <a class="page-link prev-next" href="?roleId=${roleId}&page=${pager.currentPage - 1}&size=${pager.itemsPerPage}&keyword=${param.keyword}&category=${param.category}">&laquo; Previous</a>
                </c:if>

                <c:forEach var="i" begin="1" end="${pager.totalPages}">
                    <c:choose>
                        <c:when test="${i == pager.currentPage}">
                            <span class="page-link current-page">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a class="page-link" href="?roleId=${roleId}&page=${i}&size=${pager.itemsPerPage}&keyword=${param.keyword}&category=${param.category}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${pager.currentPage < pager.totalPages}">
                    <a class="page-link prev-next" href="?roleId=${roleId}&page=${pager.currentPage + 1}&size=${pager.itemsPerPage}&keyword=${param.keyword}&category=${param.category}">Next &raquo;</a>
                </c:if>
            </c:if>
        </div>


        <form method="get" action="${pageContext.request.contextPath}/rbac/roles" class="size-config-form">
            <input type="hidden" name="roleId" value="${roleId}"/>
            <input type="hidden" name="keyword" value="${param.keyword}">
            <input type="hidden" name="category" value="${param.category}">
            <label for="size">Show per page:</label>
            <select name="size" id="size" onchange="this.form.submit()" class="form-control select-size">
                <option value="5" ${pager.itemsPerPage == 5 ? 'selected' : ''}>5</option>
                <option value="10" ${pager.itemsPerPage == 10 ? 'selected' : ''}>10</option>
                <option value="20" ${pager.itemsPerPage == 20 ? 'selected' : ''}>20</option>
            </select>
        </form>

    </div>
</div>
</body>
</html>