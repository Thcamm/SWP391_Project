<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Appointment History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .appointment-section {
            padding: 40px 0;
            background-color: #f8f9fa;
            min-height: calc(100vh - 130px);
        }

        .appointment-list {
            background: white;
            margin-top: 10px;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }

        /* Filter Form Styling */
        .filter-form {
            background: #ffffff;
            border: 2px solid #007bff;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 2px 8px rgba(0,123,255,0.1);
        }

        .filter-form .form-label {
            font-weight: 600;
            margin-bottom: 8px;
            color: #212529;
            display: flex;
            align-items: center;
            font-size: 0.95rem;
        }

        .filter-form .form-label .icon {
            margin-right: 6px;
            color: #007bff;
        }

        .filter-form input[type="date"],
        .filter-form select {
            height: 42px;
            border: 1px solid #ced4da;
            border-radius: 6px;
        }

        .filter-form input[type="date"]:focus,
        .filter-form select:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
        }

        /* Dropdown checkbox menu - IMPORTANT: Prevent closing */
        .dropdown-menu {
            padding: 15px;
            min-width: 200px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .dropdown-menu .form-check {
            margin-bottom: 12px;
            padding-left: 1.5rem;
        }

        .dropdown-menu .form-check:last-child {
            margin-bottom: 0;
        }

        .dropdown-menu .form-check-label {
            cursor: pointer;
            user-select: none;
            display: block;
            padding: 4px 0;
        }

        .dropdown-menu .form-check-input {
            cursor: pointer;
        }

        /* Show selected count */
        .status-count {
            background-color: #007bff;
            color: white;
            border-radius: 10px;
            padding: 2px 8px;
            font-size: 0.75rem;
            font-weight: bold;
            margin-left: 8px;
        }

        /* Dropdown button */
        .dropdown-toggle {
            height: 42px;
        }

        /* Table Styling */
        .table-responsive {
            margin-top: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 0 8px rgba(0,0,0,0.08);
            border-radius: 8px;
            overflow: hidden;
        }

        th, td {
            padding: 14px 15px;
            border: 1px solid #dee2e6;
            vertical-align: middle;
        }

        th {
            background-color: #007bff;
            color: white;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.875rem;
            letter-spacing: 0.5px;
        }

        tbody tr {
            transition: background-color 0.2s ease;
        }

        tbody tr:hover {
            background-color: #f8f9fa;
        }

        tbody tr:nth-child(even) {
            background-color: #fafbfc;
        }

        /* Status badges */
        .status-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 0.813rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.3px;
        }

        .status-pending {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeaa7;
        }

        .status-accepted {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .status-rejected {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .status-cancelled {
            background-color: #e2e3e5;
            color: #383d41;
            border: 1px solid #d6d8db;
        }

        .status-completed {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }

        /* Header */
        h2 {
            text-align: center;
            margin-bottom: 35px;
            color: #212529;
            font-weight: 700;
            font-size: 2rem;
        }

        /* Alert styling */
        .alert {
            border-radius: 8px;
            padding: 15px 20px;
            margin-bottom: 20px;
        }

        /* Form label */
        .form-label {
            font-weight: 600;
            color: #333;
        }

        /* Button improvements */
        .btn-sm {
            padding: 6px 14px;
            font-size: 0.875rem;
        }

        .btn-outline-primary {
            border-width: 2px;
        }

        .btn-success {
            font-weight: 600;
        }

        .btn-warning {
            font-weight: 600;
        }

        .btn-secondary {
            font-weight: 600;
        }

        /* Form select in table */
        .form-select-sm {
            padding: 4px 8px;
            font-size: 0.875rem;
            min-width: 120px;
        }

        /* Responsive Design */
        @media (max-width: 992px) {
            .appointment-list {
                margin-top: 140px;
            }
        }

        @media (max-width: 768px) {
            .appointment-list {
                margin-top: 100px;
                padding: 20px 15px;
            }

            .filter-form {
                padding: 20px 15px;
            }

            h2 {
                font-size: 1.5rem;
                margin-bottom: 25px;
            }

            table {
                font-size: 0.813rem;
            }

            th, td {
                padding: 10px 8px;
            }

            .status-badge {
                font-size: 0.75rem;
                padding: 4px 10px;
            }
        }

        @media (max-width: 576px) {
            .appointment-list {
                margin-top: 90px;
            }

            .filter-form .col-md-2,
            .filter-form .col-md-3 {
                margin-bottom: 15px;
            }

            .filter-form .d-flex.gap-2 {
                flex-direction: column;
                gap: 10px !important;
            }

            .filter-form .d-flex.gap-2 button,
            .filter-form .d-flex.gap-2 a {
                width: 100%;
            }

            .table-responsive {
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }

            table {
                min-width: 600px;
            }
        }

        /* Popover customization */
        .popover {
            max-width: 300px;
        }

        .popover-body {
            font-size: 0.875rem;
            line-height: 1.5;
        }

        /* Empty state styling */
        tbody tr td[colspan] {
            padding: 40px 20px;
            color: #6c757d;
            font-size: 1rem;
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />

<main class="appointment-section">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <jsp:include page="/view/customerservice/result.jsp" />

                <div class="appointment-list">
                    <h2 style="color: #212529">📅 Appointment History</h2>

                    <!-- Filter Form -->
                    <form action="${pageContext.request.contextPath}/customer/appointment-history"
                          method="get"
                          class="filter-form"
                          id="filterForm">
                        <div class="row g-3">
                            <!-- From Date -->
                            <div class="col-md-3">
                                <label for="fromDate" class="form-label">
                                    <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                    </svg>
                                    From Date
                                </label>
                                <input type="date"
                                       id="fromDate"
                                       name="fromDate"
                                       class="form-control"
                                       value="${param.fromDate}" />
                            </div>

                            <!-- To Date -->
                            <div class="col-md-3">
                                <label for="toDate" class="form-label">
                                    <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                    </svg>
                                    To Date
                                </label>
                                <input type="date"
                                       id="toDate"
                                       name="toDate"
                                       class="form-control"
                                       value="${param.toDate}" />
                            </div>

                            <!-- Status Dropdown -->
                            <div class="col-md-2">
                                <label for="status" class="form-label">Status</label>
                                <select id="status" name="status" class="form-select">
                                    <option value="">-- All --</option>
                                    <option value="PENDING"   ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="ACCEPTED"  ${param.status == 'ACCEPTED' ? 'selected' : ''}>Accepted</option>
                                    <option value="REJECTED"  ${param.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                                    <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                </select>
                            </div>


                            <!-- Sort Order -->
                            <div class="col-md-2">
                                <label for="sortOrder" class="form-label">Sort By</label>
                                <select id="sortOrder" name="sortOrder" class="form-select">
                                    <option value="newest" ${param.sortOrder == 'newest' ? 'selected' : ''}>Newest</option>
                                    <option value="oldest" ${param.sortOrder == 'oldest' ? 'selected' : ''}>Oldest</option>
                                </select>
                            </div>

                            <!-- Action Buttons -->
                            <div class="col-md-2">
                                <label class="form-label d-none d-md-block">&nbsp;</label>
                                <div class="d-flex gap-2">
                                    <button type="submit" class="btn btn-success flex-fill">
                                        🔍
                                    </button>
                                    <a href="${pageContext.request.contextPath}/customer/appointment-history"
                                       class="btn btn-secondary">
                                        Reset
                                    </a>
                                </div>
                            </div>
                        </div>
                    </form>

                    <!-- Error Message -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                                ${error}
                        </div>
                    </c:if>

                    <!-- Appointments Table -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Appointment Date</th>
                                <th>Status</th>
                                <th>Description</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="apm" items="${appointments}">
                                <tr>
                                    <td>${apm.appointmentID}</td>
                                    <td>${apm.appointmentDate}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${apm.status != 'PENDING'}">
                                                <c:choose>
                                                    <c:when test="${apm.status == 'CANCELLED'}">
                                                        <span class="status-badge status-cancelled">CANCELLED</span>
                                                    </c:when>
                                                    <c:when test="${apm.status == 'ACCEPTED'}">
                                                        <span class="status-badge status-accepted">ACCEPTED</span>
                                                    </c:when>
                                                    <c:when test="${apm.status == 'REJECTED'}">
                                                        <span class="status-badge status-rejected">REJECTED</span>
                                                    </c:when>
                                                    <c:when test="${apm.status == 'COMPLETED'}">
                                                        <span class="status-badge status-completed">COMPLETED</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge">${apm.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="${pageContext.request.contextPath}/customer/appointment-history"
                                                      method="post"
                                                      class="d-inline"
                                                      onsubmit="return confirm('Are you sure you want to cancel this appointment?');">
                                                    <input type="hidden" name="appointmentID" value="${apm.appointmentID}">
                                                    <select name="status"
                                                            class="form-select form-select-sm d-inline-block w-auto"
                                                            onchange="this.form.submit()">
                                                        <option value="PENDING" selected>PENDING</option>
                                                        <option value="CANCELLED">CANCEL</option>
                                                    </select>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <button type="button"
                                                class="btn btn-sm btn-outline-primary"
                                                data-bs-toggle="popover"
                                                data-bs-placement="left"
                                                data-bs-trigger="focus"
                                                data-bs-html="true"
                                                title="Description"
                                                data-bs-content="${fn:escapeXml(apm.description)}">
                                            ▶ View
                                        </button>
                                    </td>
                                    <td>
                                        <c:if test="${apm.status == 'PENDING'}">
                                            <a href="${pageContext.request.contextPath}/customer/reschedule-appointment?appointmentID=${apm.appointmentID}"
                                               class="btn btn-sm btn-warning">
                                                📅 Reschedule
                                            </a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty appointments}">
                                <tr>
                                    <td colspan="5" class="text-center py-4">
                                        <i>No appointments found.</i>
                                    </td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // Initialize popovers
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });

        // Handle status checkbox count
        const statusCheckboxes = document.querySelectorAll('.status-checkbox');
        const statusCount = document.getElementById('statusCount');
        const statusBtnText = document.getElementById('statusBtnText');

        function updateStatusCount() {
            const checkedCount = document.querySelectorAll('.status-checkbox:checked').length;
            if (checkedCount > 0) {
                statusCount.textContent = checkedCount;
                statusCount.style.display = 'inline-block';
            } else {
                statusCount.style.display = 'none';
            }
        }

        // Update count on page load
        updateStatusCount();

        // Update count when checkbox changes
        statusCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', updateStatusCount);
        });

        // Prevent dropdown from closing when clicking inside
        document.querySelector('.dropdown-menu').addEventListener('click', function(e) {
            e.stopPropagation();
        });
    });
</script>
</body>
</html>