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
<jsp:include page="/common/employee/component/header.jsp" />
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <jsp:include page="/common/employee/component/sidebar.jsp" />

        <div class="layout-page">
            <jsp:include page="/common/employee/component/navbar.jsp" />

            <div class="content-wrapper">
                <div class="container-fluid flex-grow-1 container-p-y">

                    <div class="main-content">
                        <div class="container py-4">
                            <jsp:include page="/view/customerservice/result.jsp" />
                            <!-- HEADER -->
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h3>Appointment List</h3>
                            </div>

                            <!-- SEARCH FORM -->
                            <form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="get" class="card p-4 mb-4">
                                <div class="row g-3 align-items-end">

                                    <div class="col-md-4">
                                        <label for="searchName" class="form-label">Customer Name</label>
                                        <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Enter customer name" />
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
                                    <div class="col-md-6">
                                        <label class="form-label">Status</label>
                                        <div class="d-flex flex-wrap gap-3">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="status" value="pending"
                                                       <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'pending')}">checked</c:if> />
                                                <label class="form-check-label">Pending</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="status" value="accepted"
                                                       <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'accepted')}">checked</c:if> />
                                                <label class="form-check-label">Accepted</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="status" value="rejected"
                                                       <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'rejected')}">checked</c:if> />
                                                <label class="form-check-label">Rejected</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="status" value="cancelled"
                                                       <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'cancelled')}">checked</c:if> />
                                                <label class="form-check-label">Cancelled</label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-6 d-flex align-items-end justify-content-end gap-2">
                                        <button type="submit" class="btn btn-success">🔍 Search</button>
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
                                            <c:when test="${empty appointments}">
                                                <tr class="text-center text-muted">
                                                    <td colspan="7">No appointments found.</td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="row" items="${appointments}" varStatus="loop">
                                                    <c:set var="apm" value="${row.appointment}" />
                                                    <tr>
                                                        <td>${loop.index + 1}</td>
                                                        <td>${row.customerName}</td>
                                                        <td>${apm.appointmentDate}</td>

                                                        <!-- STATUS -->
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${apm.status != 'PENDING'}">
                                                                    <c:choose>
                                                                        <c:when test="${apm.status == 'CANCELLED'}">CANCELLED</c:when>
                                                                        <c:when test="${apm.status == 'ACCEPTED'}">ACCEPTED</c:when>
                                                                        <c:when test="${apm.status == 'REJECTED'}">REJECTED</c:when>
                                                                        <c:when test="${apm.status == 'COMPLETED'}">COMPLETED</c:when>
                                                                        <c:otherwise>${apm.status}</c:otherwise>
                                                                    </c:choose>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <form action="${pageContext.request.contextPath}/customerservice/appointment-list"
                                                                          method="post" class="d-inline">
                                                                        <input type="hidden" name="appointmentID" value="${apm.appointmentID}">
                                                                        <select name="status" class="form-select form-select-sm d-inline-block w-auto ms-1"
                                                                                onchange="this.form.submit()">
                                                                            <option value="PENDING" selected>PENDING</option>
                                                                            <option value="ACCEPTED">ACCEPTED</option>
                                                                            <option value="REJECTED">REJECTED</option>
                                                                        </select>
                                                                    </form>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <td>
                                                            <button type="button"
                                                                    class="btn btn-sm btn-outline-primary"
                                                                    data-bs-toggle="popover"
                                                                    data-bs-html="true"
                                                                    data-bs-content="${apm.description}">
                                                                ▶
                                                            </button>
                                                        </td>
                                                        <td>
                                                            <a href="appointment-detail?id=${apm.appointmentID}" class="btn btn-sm btn-outline-primary">
                                                                Detail
                                                            </a>

                                                            <c:choose>
                                                                <c:when test="${apm.status == 'ACCEPTED'}">
                                                                    <a href="${pageContext.request.contextPath}/customerservice/createRequest?appointmentId=${apm.appointmentID}&customerId=${apm.customerID}" class="btn btn-sm btn-success ms-2">＋ SO</a>
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
                        </div>
                    </div>

                </div>
                <jsp:include page="/common/employee/component/footer.jsp" />

                <div class="content-backdrop fade"></div>
            </div>
        </div>
    </div>
    <div class="layout-overlay layout-menu-toggle"></div>
</div>
<jsp:include page="/common/employee/component/script.jsp" />

</body>


<script src="${pageContext.request.contextPath}/assets/js/customerservice/appointment-list.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl, {
                trigger: 'focus', // click sẽ hiển thị, click ra ngoài sẽ ẩn
                placement: 'right'
            })
        })
    });
</script>

</html>
