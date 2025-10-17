<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request List</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/support-request-list.css">

    <style>

    </style>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<form action="${pageContext.request.contextPath}/customerservice/view-support-request" method="get" class="card p-4 mb-4">
    <div class="mt-4 d-flex justify-content-between align-items-center">
        <select name="categoryId">
            <option value="">-- Select category --</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.categoryId}">${cat.categoryName}</option>
            </c:forEach>
        </select>
        <label>
            <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            From Date
        </label>
        <input type="date" name="fromDate" value="${param.fromDate}" placeholder="dd/mm/yyyy" />

        <label>
            <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            To Date
        </label>
        <input type="date" name="toDate" value="${param.toDate}" placeholder="dd/mm/yyyy" />


        <div class="dropdown">
            <button class="btn btn-outline-primary dropdown-toggle" type="button" id="statusDropdown"
                    data-bs-toggle="dropdown" aria-expanded="false">
                Status
            </button>

            <ul class="dropdown-menu p-3" aria-labelledby="statusDropdown" style="min-width: 200px;">
                <c:forEach var="s" items="${statuses}">
                    <li>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="statuses"
                                   value="${fn:toUpperCase(s)}"
                                   <c:if test="${fn:contains(fn:join(paramValues.status, ','), fn:toUpperCase(s))}">checked</c:if> />
                            <label class="form-check-label">${s}</label>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </div>


        <select id="sortOrder" name="sortOrder" class="form-select w-auto">
            <option value="newest" ${param.sortOrder == 'newest' ? 'selected' : ''}>Newest</option>
            <option value="oldest" ${param.sortOrder == 'oldest' ? 'selected' : ''}>Oldest</option>
        </select>
        <button type="submit" class="btn btn-success">
            🔍
        </button>
        <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="btn btn-secondary">Reset</a>
    </div>
</form>

<h2>📅 Support Request List</h2>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<table>
    <thead>
    <tr>
        <th>No</th>
        <th>Category</th>
        <th>Status</th>
        <th>Created At</th>
        <th>Updated At</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="sr" items="${supportrequests}" varStatus="loop" >
        <tr>
            <td>${loop.index + 1}</td>
            <td>${categoryMap[sr.categoryId]}</td>
            <td>
                <c:choose>
                    <%-- Nếu không phải PENDING thì chỉ hiển thị text và icon, không có dropdown --%>
                    <c:when test="${sr.status != 'PENDING'}">
                        <c:choose>
                            <c:when test="${sr.status == 'INPROGRESS'}"> INPROGRESS</c:when>
                            <c:when test="${sr.status == 'RESOLVED'}"> RESOLVED</c:when>
                            <c:when test="${sr.status == 'CLOSED'}"> CLOSED</c:when>
                        </c:choose>
                    </c:when>

                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/customerservice/view-support-request"
                              method="post" class="d-inline">
                            <input type="hidden" name="requestId" value="${sr.requestId}">

                            ⏳
                            <select name="status" class="form-select form-select-sm d-inline-block w-auto ms-1"
                                    onchange="this.form.submit()">
                                <option value="PENDING" selected>PENDING</option>
                                <option value="INPROGRESS">INPROGRESS</option>
                                <option value="RESOLVED">RESOLVED</option>
                                <option value="CLOSED">CLOSED</option>
                            </select>
                        </form>
                    </c:otherwise>
                </c:choose>

            </td>
            <td>${sr.createdAt}</td>
            <td>${sr.updatedAt}</td>

            <td>
                <a href="support-request-detail?id=${sr.requestId}" class="btn btn-sm btn-outline-primary">
                    Detail
                </a>

                <c:choose>
                    <c:when test="${sr.status == 'INPROGRESS'}">
                        <a href="mailto:${customerEmailMap[sr.customerId]}" class="btn btn-sm btn-outline-primary">
                            ✉️ Reply
                        </a>

                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-sm btn-success ms-2 disabled" tabindex="-1" aria-disabled="true"
                           style="opacity: 0.5; pointer-events: none;">Reply</a>
                    </c:otherwise>
                </c:choose>

            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty supportrequests}">
        <tr>
            <td colspan="7" class="text-center">No request found.</td>
        </tr>
    </c:if>
    </tbody>
</table>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/customerservice/support-request-list.js"></script>

</body>
</html>