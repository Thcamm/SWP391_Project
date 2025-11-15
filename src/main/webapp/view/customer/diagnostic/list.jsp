<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Diagnostics List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <!-- Custom CSS -->
    <style>
        body {
            background: #f4f6f9;
            font-size: 0.95rem;
            color: #111827;
        }

        .page-wrapper {
            max-width: 1100px;
        }

        h3.page-title {
            font-weight: 600;
            letter-spacing: 0.02em;
        }

        .flash-alert {
            border-radius: 0.75rem;
            box-shadow: 0 2px 6px rgba(15, 23, 42, 0.12);
        }

        .table-card {
            background: #ffffff;
            border-radius: 0.9rem;
            box-shadow: 0 4px 10px rgba(15, 23, 42, 0.08);
            overflow: hidden;
        }

        .table-header {
            padding: 0.9rem 1.25rem;
            border-bottom: 1px solid #e5e7eb;
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            color: #ffffff;
        }

        .table-header h5 {
            margin: 0;
            font-weight: 600;
        }

        .table-wrapper {
            padding: 0.75rem 1rem 1rem;
        }

        .table {
            margin-bottom: 0;
        }

        .table thead {
            background: #f3f4f6;
            font-size: 0.85rem;
        }

        .table thead th {
            border-bottom-width: 1px;
            text-transform: uppercase;
            letter-spacing: 0.03em;
        }

        .table tbody tr td {
            vertical-align: middle;
        }

        .table tbody tr:hover {
            background: #f9fafb;
        }

        .diag-issue {
            font-weight: 600;
        }

        .diag-vehicle {
            font-size: 0.8rem;
        }

        .status-badge {
            font-size: 0.8rem;
            padding: 0.35em 0.7em;
            border-radius: 999px;
        }

        .cell-strong {
            font-weight: 600;
        }

        .btn-view-detail {
            min-width: 140px;
            font-size: 0.85rem;
        }

        .empty-alert {
            border-radius: 0.8rem;
            background: #e0f2fe;
            border-color: #bfdbfe;
            color: #1e3a8a;
        }

        .btn-back {
            border-radius: 999px;
            padding-inline: 1.25rem;
        }

        @media (max-width: 768px) {
            .btn-view-detail {
                width: 100%;
            }
        }
    </style>
</head>

<body>
<jsp:include page="/common/header.jsp"/>

<c:set var="result" value="${requestScope.result}" />
<c:set var="vm" value="${result.data}" />

<div class="container page-wrapper py-4">

    <h3 class="mb-3 page-title">Diagnostics List</h3>

    <!-- Flash message -->
    <c:if test="${not empty sessionScope.flash}">
        <c:set var="flash" value="${sessionScope.flash}" />
        <c:remove var="flash" scope="session"/>
        <div class="alert ${flash.success ? 'alert-success' : 'alert-danger'} flash-alert">
                ${flash.message}
        </div>
    </c:if>

    <!-- Empty state -->
    <c:if test="${empty vm.rows}">
        <div class="alert alert-info empty-alert">
            There are no diagnostics in this request.
        </div>
    </c:if>

    <!-- Diagnostics table -->
    <c:if test="${not empty vm.rows}">
        <div class="table-card">
            <div class="table-header">
                <h5 class="mb-0">All Diagnostics</h5>
            </div>
            <div class="table-wrapper">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Issue</th>
                        <th>Status</th>
                        <th>Approved parts</th>
                        <th>Total parts</th>
                        <th>Created at</th>
                        <th class="text-end"></th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="d" items="${vm.rows}">
                        <tr>
                            <td class="cell-strong">#${d.vehicleDiagnosticID}</td>

                            <td>
                                <div class="diag-issue">${d.issueFound}</div>
                                <div class="text-muted diag-vehicle">${d.vehicleInfo}</div>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${d.statusString == 'SUBMITTED'}">
                                        <span class="badge bg-warning text-dark status-badge">
                                            Pending customer approval
                                        </span>
                                    </c:when>
                                    <c:when test="${d.statusString == 'APPROVED'}">
                                        <span class="badge bg-success status-badge">
                                            Approved
                                        </span>
                                    </c:when>
                                    <c:when test="${d.statusString == 'REJECTED'}">
                                        <span class="badge bg-danger status-badge">
                                            Rejected
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary status-badge">
                                                ${d.statusString}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <span class="cell-strong text-success">
                                        ${vm.partsApproved[d.vehicleDiagnosticID]}
                                </span>
                            </td>

                            <td>
                                <span class="cell-strong">
                                        ${vm.partsTotal[d.vehicleDiagnosticID]}
                                </span>
                            </td>

                            <td>${d.createdAtFormatted}</td>

                            <td class="text-end">
                                <a class="btn btn-outline-primary btn-sm btn-view-detail"
                                   href="${pageContext.request.contextPath}/customer/diagnostic/detail?requestId=${param.requestId}&diagnosticId=${d.vehicleDiagnosticID}">
                                    View details
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/customer/repair-tracker?id=${param.requestId}"
       class="btn btn-outline-secondary mt-3 btn-back">
        &laquo; Back to repair tracker
    </a>

</div>

<jsp:include page="/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
