<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/support-request-list.css">
</head>

<body class="bg-light">
<jsp:include page="/view/customerservice/sidebar.jsp" />
<jsp:include page="/view/customerservice/result.jsp" />
<div class="container py-4">


    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3>Support Request List</h3>
    </div>


    <form action="${pageContext.request.contextPath}/customerservice/view-support-request" method="get" class="card p-4 mb-4">
        <div class="row g-3 align-items-end">

            <div class="col-md-4">
                <label for="categoryId" class="form-label">Category</label>
                <select id="categoryId" name="categoryId" class="form-select">
                    <option value="">-- Select category --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}" ${param.categoryId == cat.categoryId ? 'selected' : ''}>
                                ${cat.categoryName}
                        </option>
                    </c:forEach>
                </select>
            </div>


            <div class="col-md-3">
                <label for="fromDate" class="form-label">From Date</label>
                <input type="date" id="fromDate" name="fromDate" value="${param.fromDate}" class="form-control" />
            </div>


            <div class="col-md-3">
                <label for="toDate" class="form-label">To Date</label>
                <input type="date" id="toDate" name="toDate" value="${param.toDate}" class="form-control" />
            </div>


            <div class="col-md-2">
                <label for="sortOrder" class="form-label">Sort Order</label>
                <select id="sortOrder" name="sortOrder" class="form-select">
                    <option value="newest" ${param.sortOrder == 'newest' ? 'selected' : ''}>Newest</option>
                    <option value="oldest" ${param.sortOrder == 'oldest' ? 'selected' : ''}>Oldest</option>
                </select>
            </div>
        </div>


        <div class="row g-3 mt-3">
            <div class="col-md-8">
                <label class="form-label">Status</label>
                <div class="d-flex flex-wrap gap-3">
                    <c:forEach var="s" items="${statuses}">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="statuses"
                                   value="${fn:toUpperCase(s)}"
                                   <c:if test="${fn:contains(fn:join(paramValues.statuses, ','), fn:toUpperCase(s))}">checked</c:if> />
                            <label class="form-check-label">${fn:toUpperCase(s)}</label>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="col-md-4 d-flex align-items-end justify-content-end gap-2">
                <button type="submit" class="btn btn-success">🔍 Search</button>
                <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="btn btn-secondary">Reset</a>
            </div>
        </div>
    </form>

    <div class="card">
        <div class="card-header">
            <strong>List of Support Requests</strong>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered align-middle mb-0">
                <thead class="table-light">
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
                <c:choose>
                    <c:when test="${empty supportrequests}">
                        <tr class="text-center text-muted">
                            <td colspan="6">No request found.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="sr" items="${supportrequests}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${categoryMap[sr.categoryId]}</td>


                                <td>
                                    <c:choose>

                                        <c:when test="${sr.status == 'RESOLVED'}">
                                            RESOLVED
                                        </c:when>


                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/customerservice/view-support-request"
                                                  method="post" class="d-inline">
                                                <input type="hidden" name="requestId" value="${sr.requestId}">

                                                <select name="status" class="form-select form-select-sm d-inline-block w-auto"
                                                        onchange="this.form.submit()">
                                                    <option value="PENDING" <c:if test="${sr.status == 'PENDING'}">selected</c:if>>PENDING</option>
                                                    <option value="INPROGRESS" <c:if test="${sr.status == 'INPROGRESS'}">selected</c:if>>INPROGRESS</option>
                                                    <option value="RESOLVED">RESOLVED</option>
                                                </select>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </td>



                                <td>${sr.createdAt}</td>
                                <td>${sr.updatedAt}</td>


                                <td>
                                    <a href="${pageContext.request.contextPath}/customerservice/view-support-request?id=${sr.requestId}"
                                       class="btn btn-sm btn-outline-primary">Detail</a>


                                    <c:choose>
                                        <c:when test="${sr.status == 'INPROGRESS'}">
                                            <a href="${pageContext.request.contextPath}/customerservice/reply-request?id=${sr.requestId}&email=${customerEmailMap[sr.customerId]}"
                                               class="btn btn-sm btn-outline-success ms-2">
                                                Reply
                                            </a>

                                        </c:when>
                                        <c:otherwise>
                                            <a class="btn btn-sm btn-outline-success ms-2 disabled"
                                               style="opacity: 0.5; pointer-events: none;">Reply</a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


<script src="${pageContext.request.contextPath}/assets/js/customerservice/support-request-list.js"></script>

</body>
</html>
