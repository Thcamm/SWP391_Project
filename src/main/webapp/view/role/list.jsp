<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<c:set var="pageTitle"  value="Role Management" scope="request"/>
<c:set var="activeMenu" value="rbac"           scope="request"/>


<jsp:include page="/view/admin/header.jsp"/>


<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/list.css"/>

<div class="layout">

    <jsp:include page="/view/admin/sidebar.jsp"/>

    <!-- Main -->
    <main class="main">
        <div class="page-wrap">
            <h1 class="page-title">Role Management</h1>

            <!-- Flash / Error -->
            <div class="msg-area">
                <c:if test="${not empty sessionScope.flash}">
                    <div class="alert ok">${sessionScope.flash}</div>
                    <c:remove var="flash" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert err">${sessionScope.error}</div>
                    <c:remove var="error" scope="session"/>
                </c:if>
            </div>

            <!-- Actions -->
            <div class="toolbar">
                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=new">
                    Add New Role
                </a>
                <a class="btn btn-ghost"
                   href="${pageContext.request.contextPath}/admin/rbac/roles">
                    Manage Role Permissions
                </a>
            </div>

            <!-- Search / Page size -->
            <form class="filter" action="${pageContext.request.contextPath}/admin/rbac/rolesList" method="get">
                <input type="hidden" name="action" value="list"/>

                <label class="f-row">
                    <span>Search</span>
                    <input class="inp" type="text" id="keyword" name="keyword"
                           value="${keyword}" placeholder="Enter role name"/>
                </label>

                <label class="f-row">
                    <span>Items / page</span>
                    <select class="inp" name="size" id="size" onchange="this.form.submit()">
                        <option value="2"  ${pager.itemsPerPage == 2  ? 'selected' : ''}>2</option>
                        <option value="5"  ${pager.itemsPerPage == 5  ? 'selected' : ''}>5</option>
                        <option value="10" ${pager.itemsPerPage == 10 ? 'selected' : ''}>10</option>
                        <option value="20" ${pager.itemsPerPage == 20 ? 'selected' : ''}>20</option>
                    </select>
                </label>

                <div class="f-row f-grow">
                    <span>Total</span>
                    <strong>${pager.totalItems}</strong>&nbsp;role(s)
                </div>

                <button class="btn btn-search" type="submit">Search</button>
            </form>

            <!-- Table -->
            <div class="table-wrap">
                <table class="tbl">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Role Name</th>
                        <th>Users Count</th>
                        <th>Description</th>
                        <th class="col-actions">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="role" items="${pager.data}">
                        <tr>
                            <td>${role.roleId}</td>
                            <td><strong>${role.roleName}</strong></td>
                            <td><span class="badge info">${role.userCount}</span></td>
                            <td><c:out value="${not empty role.description ? role.description : 'N/A'}"/></td>
                            <td class="col-actions">
                                <a class="link edit"
                                   href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=edit&id=${role.roleId}">
                                    ✏ Edit
                                </a>
                                <a class="link del"
                                   href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=delete&id=${role.roleId}"
                                   onclick="return confirm('Are you sure you want to delete this role?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty pager.data}">
                        <tr>
                            <td colspan="5" class="no-data">
                                <div class="empty">No roles found</div>
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${pager.totalPages > 1}">
                <div class="pagination">
                    <c:set var="currentPage" value="${pager.currentPage}" />
                    <c:set var="totalPages" value="${pager.totalPages}" />
                    <c:set var="pageSize"    value="${pager.itemsPerPage}" />
                    <c:set var="keywordParam" value="${not empty keyword ? '&keyword=' : ''}"/>
                    <c:set var="baseUrl"
                           value="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list&size=${pageSize}${keywordParam}${keyword}"/>

                    <a class="page ${currentPage == 1 ? 'disabled' : ''}"
                       href="${currentPage == 1 ? '#' : baseUrl.concat('&page=').concat((currentPage-1))}">
                        &laquo; Prev
                    </a>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a class="page ${i == currentPage ? 'active' : ''}"
                           href="${i == currentPage ? '#' : baseUrl.concat('&page=').concat(i)}">${i}</a>
                    </c:forEach>

                    <a class="page ${currentPage == totalPages ? 'disabled' : ''}"
                       href="${currentPage == totalPages ? '#' : baseUrl.concat('&page=').concat((currentPage+1))}">
                        Next &raquo;
                    </a>
                </div>
            </c:if>
        </div>
    </main>
</div>

<!-- Footer -->
<jsp:include page="/view/admin/footer.jsp"/>
