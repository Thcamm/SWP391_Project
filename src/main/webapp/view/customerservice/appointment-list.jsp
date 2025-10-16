<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Appointment List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background-color: #fafafa;
        }
        table {
            width: 95%;
            margin: auto;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 0 6px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 10px 15px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }
        th {
            background-color: #007bff;
            color: white;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        h2 {
            text-align: center;
            margin-bottom: 25px;
        }
        a.detail-link {
            text-decoration: none;
            color: #007bff;
            font-weight: bold;
        }
        a.detail-link:hover {
            text-decoration: underline;
        }
        .error {
            color: red;
            text-align: center;
            margin-bottom: 15px;
        }
    </style>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="get" class="card p-4 mb-4">
    <div class="mt-4 d-flex justify-content-between align-items-center">
        <div class="col-md-4">
            <label for="searchName" class="form-label">Customer Name</label>
            <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Enter customer name" />
        </div>
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
            <button class="btn btn-outline-primary dropdown-toggle" type="button" id="statusDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                Status
            </button>
            <ul class="dropdown-menu p-3" aria-labelledby="statusDropdown" style="min-width: 200px;">
                <li>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="status" value="pending"
                               <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'pending')}">checked</c:if> />
                        <label class="form-check-label">Pending</label>
                    </div>
                </li>
                <li>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="status" value="accepted"
                               <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'accepted')}">checked</c:if> />
                        <label class="form-check-label">Accepted</label>
                    </div>
                </li>
                <li>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="status" value="rejected"
                               <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'rejected')}">checked</c:if> />
                        <label class="form-check-label">Rejected</label>
                    </div>
                </li>
                <li>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="status" value="cancelled"
                               <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'cancelled')}">checked</c:if> />
                        <label class="form-check-label">Cancelled</label>
                    </div>
                </li>
            </ul>
        </div>

        <select id="sortOrder" name="sortOrder" class="form-select w-auto">
            <option value="newest" ${param.sortOrder == 'newest' ? 'selected' : ''}>Newest</option>
            <option value="oldest" ${param.sortOrder == 'oldest' ? 'selected' : ''}>Oldest</option>
        </select>
        <button type="submit" class="btn btn-success">
            🔍
        </button>
        <a href="${pageContext.request.contextPath}/customerservice/appointment-list" class="btn btn-secondary">Reset</a>
    </div>
</form>

<h2>📅 Appointment List</h2>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Customer</th>
        <th>Vehicle</th>
        <th>Appointment Date</th>
        <th>Status</th>
        <th>Description</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="row" items="${appointments}">
        <c:set var="apm" value="${row.appointment}" />
        <tr>
            <td>${apm.appointmentID}</td>
            <td>${row.customerName}</td>
            <td>${apm.vehicleID}</td>
            <td>${apm.appointmentDate}</td>

                <%-- Cột STATUS --%>
            <td>
                <c:choose>
                    <%-- Nếu không phải PENDING thì chỉ hiển thị text và icon, không có dropdown --%>
                <c:when test="${apm.status != 'PENDING'}">
                <c:choose>
                <c:when test="${apm.status == 'CANCELLED'}"> CANCELLED</c:when>
                <c:when test="${apm.status == 'ACCEPTED'}"> ACCEPTED</c:when>
                <c:when test="${apm.status == 'REJECTED'}"> REJECTED</c:when>
                <c:when test="${apm.status == 'COMPLETED'}"> COMPLETED</c:when>
                <c:otherwise>🔘 ${apm.status}</c:otherwise>
                </c:choose>
                </c:when>

                <c:otherwise>
                <form action="${pageContext.request.contextPath}/customerservice/appointment-list"
                      method="post" class="d-inline">
                    <input type="hidden" name="appointmentID" value="${apm.appointmentID}">

                    ⏳
                    <select name="status" class="form-select form-select-sm d-inline-block w-auto ms-1"
                            onchange="this.form.submit()">
                        <option value="PENDING" selected>Pending</option>
                        <option value="ACCEPTED">Accepted</option>
                        <option value="REJECTED">Rejected</option>
                    </select>
                </form>
                </c:otherwise>
                </c:choose>

</td>
            <td>${empty apm.description ? '-' : apm.description}</td>

            <td>
                <a href="appointment-detail?id=${apm.appointmentID}" class="btn btn-sm btn-outline-primary">
                    Detail
                </a>

                <c:choose>
                    <c:when test="${apm.status == 'ACCEPTED'}">
                        <a href="create-service-order?appointmentID=${apm.appointmentID}"
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

    <c:if test="${empty appointments}">
        <tr>
            <td colspan="7" class="text-center">No appointments found.</td>
        </tr>
    </c:if>
    </tbody>
</table>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


<script>
    document.querySelector('form').addEventListener('submit', function(e) {
        const fromDate = document.querySelector('input[name="fromDate"]').value;
        const toDate = document.querySelector('input[name="toDate"]').value;

        if (fromDate && toDate && new Date(fromDate) > new Date(toDate)) {
            e.preventDefault();
            alert("The start date cannot be greater than the end date!");
        }
    });
</script>
</body>
</html>