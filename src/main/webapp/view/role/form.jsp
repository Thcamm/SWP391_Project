<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle"  value="${mode eq 'edit' ? 'Update Role' : 'Create New Role'}" scope="request"/>
<c:set var="activeMenu" value="rbac" scope="request"/>

<c:set var="isEdit"     value="${mode eq 'edit'}"/>
<c:set var="actionUrl"  value="${pageContext.request.contextPath}/admin/rbac/rolesList"/>
<c:set var="formAction" value="${isEdit ? 'update' : 'create'}"/>

<jsp:include page="/view/admin/header.jsp"/>

<!-- CSS riêng cho trang form role -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/role/form.css"/>

<div class="layout">
    <jsp:include page="/view/admin/sidebar.jsp"/>

    <main class="main">
        <div class="page-wrap">

            <div class="form-container card">
                <h1 class="page-title">${pageTitle}</h1>

                <c:if test="${not empty error}">
                    <div class="alert err">Error: ${error}</div>
                </c:if>

                <form action="${actionUrl}" method="post" class="main-form" autocomplete="off" novalidate>
                    <input type="hidden" name="action" value="${formAction}"/>

                    <c:if test="${isEdit}">
                        <input type="hidden" name="id" value="${role.roleId}"/>
                        <div class="meta">
                            <span class="muted">Role ID:</span>
                            <strong>${role.roleId}</strong>
                        </div>
                    </c:if>

                    <!-- Name -->
                    <div class="form-group">
                        <label for="name" class="form-label">Name of role <span class="req">*</span></label>
                        <input
                                type="text"
                                id="name"
                                name="name"
                                required
                                maxlength="50"
                                class="inp"
                                placeholder="Enter name of role"
                                value="<c:out value='${isEdit ? role.roleName : (not empty name ? name : "")}'/>"
                        />
                        <small class="hint">Max 50 chars.</small>
                    </div>

                    <!-- Description -->
                    <div class="form-group">
                        <label for="description" class="form-label">Description</label>
                        <textarea
                                id="description"
                                name="description"
                                rows="5"
                                maxlength="255"
                                class="inp ta"
                                placeholder="Enter description (optional)"><c:out value='${isEdit ? role.description : (not empty description ? description : "")}'/></textarea>
                        <small class="hint">Optional — max 255 chars.</small>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn primary">
                            ${isEdit ? 'Update' : 'Create'}
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list" class="btn ghost">
                            Cancel
                        </a>
                    </div>
                </form>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/view/admin/footer.jsp"/>
