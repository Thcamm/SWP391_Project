<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Set page title and active menu -->
<c:set var="pageTitle" value="Role Management" scope="request"/>
<c:set var="pageCSS" value="role/list.css" scope="request"/>
<c:set var="activeMenu" value="rbac" scope="request"/>

<!-- Include Header -->
<jsp:include page="/view/role/header.jsp" />

<!-- Page Content -->
<div class="container role-list-container">
    <h1 class="page-title">Role Management</h1>

    <!-- Message Display -->
    <div class="message-container">
        <c:if test="${not empty sessionScope.flash}">
            <div class="alert alert-success">
                <span class="alert-icon"></span>
                <span class="alert-text">${sessionScope.flash}</span>
            </div>
            <c:remove var="flash" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert-error">
                <span class="alert-icon"></span>
                <span class="alert-text">${sessionScope.error}</span>
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons-container">
        <a href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=new" class="btn btn-primary">
             Add New Role
        </a>

        <a href="${pageContext.request.contextPath}/admin/rbac/roles" class="btn btn-secondary">
             Manage Role Permissions
        </a>
    </div>

    <!-- Search Form -->
    <form action="${pageContext.request.contextPath}/admin/rbac/rolesList" method="get" class="search-form">
        <input type="hidden" name="action" value="list">

        <div class="form-group search-input-group">
            <label for="keyword">Search:</label>
            <input type="text" id="keyword" name="keyword" value="${keyword}"
                   placeholder="Enter role name" class="form-control">
            <button type="submit" class="btn btn-search">Search</button>
        </div>

        <div class="form-group page-config-group">
            <label for="size">Items per Page:</label>
            <select name="size" id="size" onchange="this.form.submit()" class="form-control select-size">
                <option value="2" ${pager.itemsPerPage == 2 ? 'selected' : ''}>2</option>
                <option value="5" ${pager.itemsPerPage == 5 ? 'selected' : ''}>5</option>
                <option value="10" ${pager.itemsPerPage == 10 ? 'selected' : ''}>10</option>
                <option value="20" ${pager.itemsPerPage == 20 ? 'selected' : ''}>20</option>
            </select>
        </div>

        <span class="total-items">Total: <strong>${pager.totalItems}</strong> role(s)</span>
    </form>

    <!-- Data Table -->
    <div class="table-responsive">
        <table class="data-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Role Name</th>
                <th>Users Count</th>
                <th>Description</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="role" items="${pager.data}">
                <tr>
                    <td>${role.roleId}</td>
                    <td><strong>${role.roleName}</strong></td>
                    <td>
                        <span class="badge badge-info">${role.userCount}</span>
                    </td>
                    <td>
                        <c:out value="${not empty role.description ? role.description : 'N/A'}"/>
                    </td>
                    <td class="action-column">
                        <a href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=edit&id=${role.roleId}"
                           class="action-link edit-link" title="Edit">
                            ✏Edit
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=delete&id=${role.roleId}"
                           onclick="return confirm('Are you sure you want to delete this role?');"
                           class="action-link delete-link" title="Delete">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty pager.data}">
                <tr>
                    <td colspan="5" class="no-data">
                        <div class="empty-state">
                            <p>No roles found</p>
                        </div>
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <c:if test="${pager.totalPages > 1}">
        <div class="pagination-container">
            <ul class="pagination">
                <c:set var="currentPage" value="${pager.currentPage}" />
                <c:set var="totalPages" value="${pager.totalPages}" />
                <c:set var="pageSize" value="${pager.itemsPerPage}" />
                <c:set var="keywordParam" value="${not empty keyword ? '&keyword=' : ''}"/>
                <c:set var="baseUrl" value="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list&size=${pageSize}${keywordParam}${keyword}"/>

                <!-- Previous Button -->
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <c:choose>
                        <c:when test="${currentPage == 1}">
                            <span class="page-link">&laquo; Previous</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${baseUrl}&page=${currentPage - 1}" class="page-link">&laquo; Previous</a>
                        </c:otherwise>
                    </c:choose>
                </li>

                <!-- Page Numbers -->
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
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

                <!-- Next Button -->
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <c:choose>
                        <c:when test="${currentPage == totalPages}">
                            <span class="page-link">Next &raquo;</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${baseUrl}&page=${currentPage + 1}" class="page-link">Next &raquo;</a>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </div>
    </c:if>
</div>

<!-- Include Footer -->
<jsp:include page="/view/role/footer.jsp" />