<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    System.out.println("=== JSP tree.jsp is being rendered ===");
    System.out.println("View object: " + request.getAttribute("view"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Diagnostic &amp; Parts Details</title>
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

        .request-meta {
            background: #ffffff;
            border-radius: 0.75rem;
            padding: 0.75rem 1rem;
            box-shadow: 0 2px 4px rgba(15, 23, 42, 0.06);
        }

        .request-meta b {
            font-weight: 600;
        }

        .selected-services {
            background: #ffffff;
            border-radius: 0.75rem;
            padding: 0.75rem 1rem;
            box-shadow: 0 2px 4px rgba(15, 23, 42, 0.06);
        }

        .selected-services ul {
            padding-left: 1.1rem;
            margin-bottom: 0;
        }

        .selected-services li {
            margin-bottom: 0.2rem;
        }

        .service-card {
            border: none;
            border-radius: 0.85rem;
            box-shadow: 0 4px 10px rgba(15, 23, 42, 0.08);
            overflow: hidden;
        }

        .service-card .card-header {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            color: #fff;
            padding: 0.85rem 1.25rem;
        }

        .service-card .card-header .detail-id {
            font-size: 0.8rem;
            opacity: 0.85;
        }

        .diagnostic-block {
            background: #f9fafb;
            border-radius: 0.75rem;
            border: 1px solid #e5e7eb;
            padding: 1rem 1.1rem;
        }

        .diagnostic-block + .diagnostic-block {
            margin-top: 0.75rem;
        }

        .diagnostic-info b {
            font-weight: 600;
        }

        .diagnostic-info div {
            margin-bottom: 0.15rem;
        }

        .diagnostic-status {
            font-weight: 500;
        }

        .btn-view-detail {
            min-width: 180px;
            font-size: 0.85rem;
        }

        /* Table styling */
        .parts-table {
            margin-top: 0.75rem;
            border-radius: 0.75rem;
            overflow: hidden;
            border: 1px solid #e5e7eb;
        }

        .parts-table thead {
            background: #f3f4f6;
            font-size: 0.85rem;
        }

        .parts-table th {
            border-bottom-width: 1px;
            text-transform: uppercase;
            letter-spacing: 0.03em;
        }

        .parts-table tbody tr td {
            vertical-align: middle;
        }

        .parts-table tbody tr:hover {
            background: #f9fafb;
        }

        .part-name {
            font-weight: 600;
        }

        .part-meta {
            font-size: 0.8rem;
        }

        .badge-part-condition {
            font-size: 0.8rem;
            padding: 0.4em 0.6em;
            border-radius: 999px;
        }

        .badge-approval {
            font-size: 0.75rem;
            padding: 0.3em 0.6em;
            border-radius: 999px;
        }

        .empty-text {
            font-style: italic;
        }

        /* Back button */
        .btn-back {
            border-radius: 999px;
            padding-inline: 1.25rem;
        }

        @media (max-width: 768px) {
            .diagnostic-layout {
                flex-direction: column;
                gap: 0.75rem;
            }

            .btn-view-detail {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<c:set var="view" value="${requestScope.view}" />

<div class="container page-wrapper py-4"  style="background:#ffffff; border-radius:8px; padding:12px 16px;
            color:#111 !important">
    <h3 class="mb-3 page-title">
        Diagnostics &amp; Parts for Request
        <span class="text-primary">#${view.request.requestId}</span>
    </h3>

    <!-- Vehicle & request info -->
    <div class="request-meta mb-3"
         style="background:#ffffff; border-radius:8px; padding:12px 16px;
            color:#111 !important; font-size:15px;">

        <p class="mb-1" style="color:#111 !important;">
            <span style="font-weight:600; color:#111 !important;">License plate:</span>
            <span class="badge bg-dark rounded-pill px-3 py-1" style="font-size:13px;">
                ${view.vehicle.licensePlate}
            </span>
        </p>

        <p class="mb-0" style="color:#111 !important;">
            ${view.vehicle.brand} ${view.vehicle.model}
            <c:if test="${view.vehicle.yearManufacture ne null}">
                · Year: <b style="color:#111 !important;">${view.vehicle.yearManufacture}</b>
            </c:if>
        </p>

    </div>


    <!-- Selected services -->
    <c:if test="${not empty view.requestedServices}">
        <div class="selected-services mb-4"
             style="background:#ffffff; border-radius:8px; padding:14px 18px;
                box-shadow:0 2px 6px rgba(0,0,0,0.06); color:#111 !important;
                font-size:15px;">

            <div class="d-flex align-items-center mb-1" style="color:#111 !important;">

            <span class="fw-semibold me-2" style="color:#111 !important;">
                Selected services:
            </span>

                <span class="badge bg-light text-dark border border-1"
                      style="font-size:13px;">
                ${fn:length(view.requestedServices)} items
            </span>
            </div>

            <ul style="margin:0; padding-left:18px; color:#111 !important;">
                <c:forEach var="s" items="${view.requestedServices}">
                    <li style="color:#111 !important; margin-bottom:3px;">
                    <span class="fw-semibold" style="color:#111 !important;">
                            ${s.serviceName}
                    </span>

                        <c:if test="${not empty s.category}">
                        <span style="color:#555 !important;">
                            - ${s.category}
                        </span>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>

        </div>
    </c:if>


    <!-- Each ServiceBlock corresponds to one WorkOrderDetail -->
    <c:forEach var="block" items="${view.services}">
        <div class="card service-card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <div>
                    <strong>${block.serviceLabel}</strong>
                </div>
                <div class="detail-id">
                    Detail ID: <span class="fw-semibold">${block.detailId}</span>
                </div>
            </div>

            <div class="card-body">
                <c:if test="${empty block.diagnostics}">
                    <p class="text-muted empty-text mb-0">
                        There are no diagnostics for this item yet.
                    </p>
                </c:if>

                <c:forEach var="row" items="${block.diagnostics}">
                    <div class="diagnostic-block mb-3">

                        <div class="diagnostic-layout d-flex justify-content-between align-items-start mb-2">
                            <div class="diagnostic-info">
                                <div><b>Issue:</b> ${row.diagnostic.issueFound}</div>
                                <div>
                                    <b>Diagnostic labor:</b>
                                    <span class="text-primary fw-semibold">
                                            ${row.diagnostic.estimateCostFormatted}
                                    </span>
                                </div>
                                <div>
                                    <b>Total estimate:</b>
                                    <span class="text-danger fw-semibold">
                                            ${row.diagnostic.totalEstimateFormatted}
                                    </span>
                                </div>
                                <div class="diagnostic-status">
                                    <b>Status:</b>
                                    <span>${row.diagnostic.statusString}</span>
                                    <c:if test="${not empty row.diagnostic.rejectReason}">
                                        <span class="text-danger">
                                            · Rejection reason: ${row.diagnostic.rejectReason}
                                        </span>
                                    </c:if>
                                </div>
                                <div class="text-muted small">
                                    <b>Created at:</b> ${row.diagnostic.createdAtFormatted}
                                </div>
                            </div>

                            <!-- Link to detail page where customer reviews each part -->
                            <div class="text-end">
                                <a class="btn btn-sm btn-outline-primary btn-view-detail"
                                   href="${pageContext.request.contextPath}/customer/diagnostic/detail?requestId=${view.request.requestId}&diagnosticId=${row.diagnostic.vehicleDiagnosticID}">
                                    View &amp; approve details
                                </a>
                            </div>
                        </div>

                        <!-- Parts table suggested for this diagnostic -->
                        <c:if test="${not empty row.parts}">
                            <table class="table table-sm parts-table align-middle mb-0">
                                <thead>
                                <tr>
                                    <th>Part</th>
                                    <th>Type</th>
                                    <th class="text-end">Qty</th>
                                    <th class="text-end">Unit price</th>
                                    <th class="text-end">Amount</th>
                                    <th class="text-center">Customer approved?</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="p" items="${row.parts}">
                                    <tr>
                                        <td>
                                            <div class="part-name">${p.partName}</div>
                                            <div class="text-muted part-meta">
                                                Code: ${p.partCode}
                                                <c:if test="${not empty p.sku}">
                                                    · SKU: ${p.sku}
                                                </c:if>
                                            </div>
                                            <c:if test="${not empty p.reasonForReplacement}">
                                                <div class="part-meta">
                                                    Reason: ${p.reasonForReplacement}
                                                </div>
                                            </c:if>
                                        </td>
                                        <td>
                                            <span class="badge badge-part-condition ${p.partCondition.badgeClass}">
                                                    ${p.partCondition.displayText}
                                            </span>
                                        </td>
                                        <td class="text-end">${p.quantityNeeded}</td>
                                        <td class="text-end">${p.unitPrice}</td>
                                        <td class="text-end fw-semibold">${p.totalPrice}</td>
                                        <td class="text-center">
                                            <span class="badge badge-approval bg-${p.approved ? 'success' : 'secondary'}">
                                                    ${p.approved ? 'Approved' : 'Not approved'}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:if>

                    </div>
                </c:forEach>

            </div>
        </div>
    </c:forEach>

    <a href="${pageContext.request.contextPath}/customer/repair-tracker?id=${view.request.requestId}"
       class="btn btn-outline-secondary btn-back mt-2">
        &laquo; Back to repair tracker
    </a>
</div>

<jsp:include page="/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
