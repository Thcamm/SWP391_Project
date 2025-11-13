<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Work Order Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
        body {
            background-color: #f8f9fa;
        }

        .info-card {
            background: white;
            border-radius: 12px;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .card-title {
            font-size: 1.25rem;
            font-weight: 600;
            margin-bottom: 20px;
            color: #212529;
            display: flex;
            align-items: center;
            gap: 10px;
            padding-bottom: 12px;
            border-bottom: 2px solid #e9ecef;
        }

        .info-row {
            display: flex;
            padding: 12px 0;
            border-bottom: 1px solid #f1f3f5;
        }

        .info-row:last-child {
            border-bottom: none;
        }

        .info-label {
            flex: 0 0 180px;
            font-weight: 500;
            color: #6c757d;
        }

        .info-value {
            flex: 1;
            color: #212529;
        }

        .status-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.875rem;
            font-weight: 500;
        }

        .detail-section {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 16px;
        }

        .detail-header {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 12px;
            color: #212529;
        }

        .task-item {
            background: white;
            border-left: 4px solid #0d6efd;
            padding: 12px 16px;
            margin-bottom: 12px;
            border-radius: 4px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .part-item {
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 12px;
        }

        .summary-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-top: 20px;
        }

        .summary-item {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 12px;
            text-align: center;
        }

        .summary-item.primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .summary-item.success {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
        }

        .summary-item.warning {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        .summary-item.info {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        .summary-value {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .summary-label {
            font-size: 0.875rem;
            opacity: 0.9;
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <main class="main" style="padding: 1.25rem;">
                <div class="container-fluid">
                    <c:set var="view" value="${requestScope.workOrderView}" />
                    <c:set var="wo" value="${view.workOrder}" />
                    <c:set var="summary" value="${view.summary}" />

                    <h2 class="mb-4">Work Order Details #${wo.workOrderId}</h2>

                    <!-- Summary Statistics -->
                    <div class="info-card">
                        <div class="card-title">
                            <i class="bi bi-graph-up"></i>
                            Summary Statistics
                        </div>

                        <div class="summary-grid">
                            <div class="summary-item primary">
                                <div class="summary-value">${summary.completedDetails}/${summary.totalDetails}</div>
                                <div class="summary-label">Completed Work Items</div>
                            </div>

                            <div class="summary-item success">
                                <div class="summary-value">${summary.completedTasks}/${summary.totalTasks}</div>
                                <div class="summary-label">Completed Tasks</div>
                            </div>

                            <div class="summary-item warning">
                                <div class="summary-value">${summary.pendingApprovals}</div>
                                <div class="summary-label">Pending Approvals</div>
                            </div>

                            <div class="summary-item info">
                                <div class="summary-value">${summary.deliveredParts}/${summary.totalParts}</div>
                                <div class="summary-label">Delivered Parts</div>
                            </div>
                        </div>
                    </div>

                    <!-- Work Details -->
                    <div class="info-card">
                        <div class="card-title">
                            <i class="bi bi-list-check"></i>
                            Work Details
                        </div>

                        <c:forEach var="detail" items="${view.details}" varStatus="status">
                            <div class="detail-section">
                                <div class="detail-header">
                                    <i class="bi bi-wrench"></i>
                                        ${detail.sourceLabel} - ${detail.detail.taskDescription}
                                    <span class="status-badge bg-${detail.statusColor} text-white ms-2">
                                            ${detail.statusLabel}
                                    </span>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-md-4">
                                        <small class="text-muted">Estimated Cost:</small><br>
                                        <strong><fmt:formatNumber value="${detail.detail.estimateAmount}" type="currency" currencySymbol="₫" /></strong>
                                    </div>

                                    <div class="col-md-4">
                                        <small class="text-muted">Approval Status:</small><br>
                                        <span class="badge bg-${detail.approvalStatusColor}">${detail.approvalStatusLabel}</span>
                                        <c:if test="${not empty detail.approvedByName}">
                                            <br><small class="text-muted">By: ${detail.approvedByName}</small>
                                        </c:if>
                                    </div>
                                </div>

                                <c:if test="${not empty detail.tasks}">
                                    <div class="mt-3">
                                        <strong>Assigned Tasks:</strong>
                                        <c:forEach var="task" items="${detail.tasks}">
                                            <div class="task-item">
                                                <div class="d-flex justify-content-between align-items-start mb-2">
                                                    <div>
                                                        <strong>${task.taskTypeLabel}</strong>: ${task.task.taskDescription}
                                                    </div>
                                                    <span class="badge bg-${task.statusColor}">${task.statusLabel}</span>
                                                </div>

                                                <div class="row small">
                                                    <div class="col-md-4">
                                                        <i class="bi bi-person"></i> ${task.technicianName}
                                                    </div>
                                                    <div class="col-md-4">
                                                        <span class="badge bg-${task.priorityColor}">${task.priorityLabel}</span>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <c:if test="${task.task.startAt != null}">
                                                            <i class="bi bi-clock"></i> <fmt:formatDate value="${task.task.startAt}" pattern="dd/MM HH:mm" />
                                                        </c:if>
                                                    </div>
                                                </div>

                                                <c:if test="${task.task.progressPercentage != null}">
                                                    <div class="progress mt-2" style="height: 8px;">
                                                        <div class="progress-bar" style="width: ${task.task.progressPercentage}%"></div>
                                                    </div>
                                                    <small class="text-muted">${task.task.progressPercentage}% completed</small>
                                                </c:if>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Parts Used -->
                    <c:if test="${not empty view.parts}">
                        <div class="info-card">
                            <div class="card-title">
                                <i class="bi bi-gear"></i>
                                Parts Used
                            </div>

                            <c:forEach var="part" items="${view.parts}">
                                <div class="part-item">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <strong>${part.partName}</strong>
                                            <br>
                                            <small class="text-muted">Quantity: ${part.part.quantityUsed}</small>
                                        </div>
                                        <span class="status-badge bg-${part.statusColor} text-white">${part.statusLabel}</span>
                                    </div>

                                    <div class="row small">
                                        <div class="col-md-6">
                                            <i class="bi bi-currency-dollar"></i>
                                            <fmt:formatNumber value="${part.part.unitPrice}" type="currency" currencySymbol="₫" /> / unit
                                        </div>
                                        <div class="col-md-6">
                                            <i class="bi bi-person"></i> Requested by: ${part.requestedByName}
                                        </div>
                                    </div>

                                    <c:if test="${part.part.requestedAt != null}">
                                        <small class="text-muted">
                                            <i class="bi bi-clock"></i>
                                            <fmt:formatDate value="${part.part.requestedAt}" pattern="dd/MM/yyyy HH:mm" />
                                        </small>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>

                    <!-- Back Button -->
                    <div class="text-center mt-4">
                        <a href="javascript:history.back()" class="btn btn-secondary">
                            <i class="bi bi-arrow-left"></i> Back
                        </a>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
