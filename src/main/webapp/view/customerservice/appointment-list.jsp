<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Appointment List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/appointment-list.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>

<body>
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- SIDEBAR -->
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- MAIN CONTENT -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                            border: 1px solid #e5e7eb;
                            border-radius: 12px;
                            padding: 2.5rem;
                            min-height: calc(100vh - 64px - 1.25rem);
                            display: flex;
                            flex-direction: column;
                            padding-top: 0;">
                    <div class="container py-4">
                        <jsp:include page="/view/customerservice/result.jsp"/>

                        <!-- HEADER -->
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h3>Appointment List</h3>
                        </div>

                        <!-- SEARCH FORM -->
                        <form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="get" class="card p-4 mb-4">
                            <div class="row g-3 align-items-end">
                                <div class="col-md-4">
                                    <label for="searchName" class="form-label">Customer Name</label>
                                    <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Enter customer name"/>
                                </div>

                                <div class="col-md-3">
                                    <label for="fromDate" class="form-label">From Date</label>
                                    <input type="date" id="fromDate" name="fromDate" value="${param.fromDate}" class="form-control"/>
                                </div>

                                <div class="col-md-3">
                                    <label for="toDate" class="form-label">To Date</label>
                                    <input type="date" id="toDate" name="toDate" value="${param.toDate}" class="form-control"/>
                                </div>

                                <div class="col-md-2">
                                    <label for="sortOrder" class="form-label">Sort Order</label>
                                    <select id="sortOrder" name="sortOrder" class="form-select">
                                        <option value="newest" <c:if test="${param.sortOrder eq 'newest'}">selected</c:if>>Newest</option>
                                        <option value="oldest" <c:if test="${param.sortOrder eq 'oldest'}">selected</c:if>>Oldest</option>
                                    </select>
                                </div>
                            </div>

                            <!-- STATUS FILTER -->
                            <div class="row g-3 mt-3">
                                <div class="col-md-6">
                                    <label class="form-label">Status</label>
                                    <div class="d-flex flex-wrap gap-3">
                                        <c:set var="selectedStatuses" value="${fn:join(paramValues.status, ',')}" />
                                        <c:forEach var="s" items="${['pending','accepted','rejected','cancelled']}">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="status" value="${s}"
                                                       <c:if test="${fn:contains(selectedStatuses, s)}">checked</c:if>/>
                                                <label class="form-check-label text-capitalize">${s}</label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="col-md-6 d-flex align-items-end justify-content-end gap-2">
                                    <button type="submit" class="btn btn-success">Search</button>
                                    <a href="${pageContext.request.contextPath}/customerservice/appointment-list" class="btn btn-secondary">Reset</a>
                                </div>
                            </div>
                        </form>

                        <!-- APPOINTMENT TABLE -->
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <span>List of Appointments</span>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-bordered align-middle mb-0">
                                    <thead class="table-light">
                                    <tr>
                                        <th>No</th>
                                        <th>Customer</th>
                                        <th>Appointment Date</th>
                                        <th>Status</th>
                                        <th>Description</th>
                                        <th>Action</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:choose>
                                        <c:when test="${empty appointmentList.paginatedData}">
                                            <tr class="text-center text-muted">
                                                <td colspan="6">No appointments found.</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${appointmentList.paginatedData}" varStatus="loop">
                                                <c:set var="apm" value="${row.appointment}" />
                                                <tr>
                                                    <td>${(appointmentList.currentPage - 1) * appointmentList.itemsPerPage + loop.index + 1}</td>
                                                    <td>${row.customerName}</td>
                                                    <td>${apm.appointmentDate}</td>

                                                    <!-- STATUS -->
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${apm.status ne 'PENDING'}">
                                                                <span class="badge bg-secondary">${apm.status}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="post" class="d-inline">
                                                                    <input type="hidden" name="appointmentID" value="${apm.appointmentID}">
                                                                    <select name="status" class="form-select form-select-sm d-inline-block w-auto ms-1" onchange="this.form.submit()">
                                                                        <option value="PENDING" selected>PENDING</option>
                                                                        <option value="ACCEPTED">ACCEPTED</option>
                                                                        <option value="REJECTED">REJECTED</option>
                                                                    </select>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <!-- DESCRIPTION -->
                                                    <td>
                                                        <button type="button" class="btn btn-sm btn-outline-primary"
                                                                data-bs-toggle="popover" data-bs-html="false"
                                                                data-bs-content="${fn:escapeXml(apm.description)}"
                                                                title="Description">
                                                            ▶
                                                        </button>
                                                    </td>

                                                    <!-- ACTION -->
                                                    <td>
                                                        <a href="appointment-detail?id=${apm.appointmentID}" class="btn btn-sm btn-outline-primary">Detail</a>

                                                        <c:choose>
                                                            <c:when test="${apm.status eq 'ACCEPTED'}">
                                                                <a href="${pageContext.request.contextPath}/customerservice/createRequest?appointmentId=${apm.appointmentID}&customerId=${apm.customerID}"
                                                                   class="btn btn-sm btn-success ms-2">＋ SO</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="btn btn-sm btn-success ms-2 disabled" tabindex="-1" aria-disabled="true"
                                                                   style="opacity: 0.5; pointer-events: none;">＋ SO</a>
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


                        <!-- PAGINATION -->
                        <c:set var="statusQuery" value="" />
                        <c:if test="${not empty paramValues.status}">
                            <c:forEach var="s" items="${paramValues.status}">
                                <c:set var="statusQuery" value="${statusQuery}&status=${s}" />
                            </c:forEach>
                        </c:if>

                        <jsp:include page="/view/customerservice/pagination.jsp">
                            <jsp:param name="currentPage" value="${appointmentList.currentPage}" />
                            <jsp:param name="totalPages" value="${appointmentList.totalPages}" />
                            <jsp:param name="baseUrl" value="/customerservice/appointment-list" />
                            <jsp:param name="queryString"
                                       value="&searchName=${param.searchName}&fromDate=${param.fromDate}&toDate=${param.toDate}&sortOrder=${param.sortOrder}${statusQuery}" />
                        </jsp:include>

                    </div>
                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl, {
                trigger: 'focus',
                placement: 'right'
            });
        });
    });
</script>
</body>
</html>
