<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="RBAC_BASE" value="/admin/rbac" />
<c:set var="pageTitle" value="Role Permission Management (RBAC)" scope="request" />
<c:set var="activeMenu" value="rbac" scope="request" />
<c:set var="pageCSS" value="role/rbac.css" scope="request" />


<jsp:include page="/view/admin/header.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/rbac.css"/>


<div class="layout">
    <jsp:include page="/view/admin/sidebar.jsp" />

    <!-- Main content -->
    <main class="main rbac-page">
        <div class="page-wrap">

            <div class="container rbac-container">
                <h1 class="page-title">Role → Permission (RBAC)</h1>

                <!-- Flash Messages -->
                <c:if test="${not empty sessionScope.flash}">
                    <div class="alert success-message">
                            ${sessionScope.flash}
                        <c:remove var="flash" scope="session" />
                    </div>
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert error-message">
                            ${sessionScope.error}
                        <c:remove var="error" scope="session" />
                    </div>
                </c:if>

                <c:if test="${param.saved == '1'}">
                    <div class="alert success-message">Saved permissions successfully!</div>
                </c:if>

                <c:if test="${empty roles}">
                    <div class="alert error-message">
                        No roles found in the system. Please contact the administrator.
                    </div>
                </c:if>

                <!-- Back Link -->
                <div class="back-link-container">
                    <a href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list" class="btn btn-back">
                        &laquo; Back to Role List
                    </a>
                </div>

                <!-- Filter Form -->
                <form class="filter-form" method="get" action="${pageContext.request.contextPath}/admin/rbac/roles">
                    <div class="form-row">
                        <div class="form-group role-select-group">
                            <label class="form-label">
                                Role:
                                <select name="roleId" onchange="this.form.submit()" class="form-control select-role">
                                    <c:forEach var="r" items="${roles}">
                                        <option value="${r.roleId}" <c:if test="${r.roleId == roleId}">selected</c:if>>
                                                ${r.roleName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </label>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Keyword:</label>
                            <input type="text" name="keyword" value="${param.keyword}" placeholder="Find permission..." class="form-control" />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Category:</label>
                            <input type="text" name="category" value="${param.category}" placeholder="e.g., USER, ORDER..." class="form-control" />
                        </div>

                        <button class="btn btn-search" type="submit">Filter</button>

                        <span class="total-count muted right">
              ${pager.totalItems} permissions found.
            </span>
                    </div>
                </form>

                <!-- Create Button -->
                <div class="toolbar">
                    <a href="${pageContext.request.contextPath}/admin/rbac/permissions?action=new" class="btn btn-primary">
                        + Create New Permission
                    </a>
                </div>

                <!-- Permission Table -->
                <form method="post" action="${pageContext.request.contextPath}/admin/rbac/roles/save" class="permission-form">
                    <input type="hidden" name="roleId"   value="${roleId}" />
                    <input type="hidden" name="page"     value="${pager.currentPage}" />
                    <input type="hidden" name="size"     value="${pager.itemsPerPage}" />
                    <input type="hidden" name="keyword"  value="${param.keyword}" />
                    <input type="hidden" name="category" value="${param.category}" />

                    <c:if test="${not empty managePermId}">
                        <input type="hidden" name="permIds" value="${managePermId}" />
                    </c:if>

                    <div class="table-responsive">
                        <table class="data-table permission-table">
                            <thead>
                            <tr>
                                <th class="col-checkbox">#</th>
                                <th>Permission</th>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="p" items="${pager.data}" varStatus="st">
                                <tr class="<c:if test='${checkedPermsIds.contains(p.permId)}'>checked-row</c:if>">
                                    <td class="col-checkbox">
                                        <input type="hidden" name="pagePermIds" value="${p.permId}" />
                                        <input type="checkbox" name="permIds" value="${p.permId}" class="perm-checkbox"
                                               <c:if test="${checkedPermsIds.contains(p.permId)}">checked</c:if> />
                                    </td>
                                    <td class="perm-code"><b>${p.code}</b></td>
                                    <td class="perm-name">${p.name}</td>
                                    <td class="perm-category">
                                        <span class="badge badge-category"><c:out value="${p.category}" /></span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/rbac/permissions?action=edit&id=${p.permId}" class="btn btn-edit">Edit</a>
                                        <a href="${pageContext.request.contextPath}/admin/rbac/permissions?action=delete&id=${p.permId}"
                                           class="btn btn-delete"
                                           onclick="return confirm('Delete this permission?');">Delete</a>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty pager.data}">
                                <tr><td colspan="5" class="no-data">No permissions found.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>

                    <div class="form-toolbar">
                        <button class="btn btn-primary" type="submit">Save</button>
                        <a class="btn btn-reset" href="${pageContext.request.contextPath}/admin/rbac/roles?roleId=${roleId}">Reset</a>
                    </div>
                </form>

                <!-- Pagination -->
                <div class="pagination-area">
                    <div class="pagination">
                        <c:if test="${pager.totalPages > 1}">
                            <c:if test="${pager.currentPage > 1}">
                                <a class="page-link prev-next"
                                   href="?roleId=${roleId}&page=${pager.currentPage - 1}&size=${pager.itemsPerPage}&keyword=${param.keyword}&category=${param.category}">&laquo; Prev</a>
                            </c:if>

                            <c:forEach var="i" begin="1" end="${pager.totalPages}">
                                <c:choose>
                                    <c:when test="${i == pager.currentPage}">
                                        <span class="page-link current-page">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="page-link"
                                           href="?roleId=${roleId}&page=${i}&size=${pager.itemsPerPage}&keyword=${param.keyword}&category=${param.category}">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${pager.currentPage < pager.totalPages}">
                                <a class="page-link prev-next"
                                   href="?roleId=${roleId}&page=${pager.currentPage + 1}&size=${pager.itemsPerPage}&keyword=${param.keyword}&category=${param.category}">Next &raquo;</a>
                            </c:if>
                        </c:if>
                    </div>

                    <form method="get" action="${pageContext.request.contextPath}/admin/rbac/roles" class="size-config-form">
                        <input type="hidden" name="roleId"   value="${roleId}" />
                        <input type="hidden" name="keyword"  value="${param.keyword}" />
                        <input type="hidden" name="category" value="${param.category}" />
                        <label for="size">Show per page:</label>
                        <select name="size" id="size" onchange="this.form.submit()" class="form-control select-size">
                            <option value="5"  ${pager.itemsPerPage == 5  ? 'selected' : ''}>5</option>
                            <option value="10" ${pager.itemsPerPage == 10 ? 'selected' : ''}>10</option>
                            <option value="20" ${pager.itemsPerPage == 20 ? 'selected' : ''}>20</option>
                        </select>
                    </form>
                </div>
            </div><!-- /.container -->

        </div><!-- /.page-wrap -->
    </main>
</div><!-- /.layout -->

<!-- Footer chung -->
<jsp:include page="/view/admin/footer.jsp" />
