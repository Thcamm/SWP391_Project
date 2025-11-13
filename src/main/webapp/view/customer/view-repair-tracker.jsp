<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tracking the Repair Process</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
        body {
            background-color: #f8f9fa;
        }

        .timeline {
            position: relative;
            padding: 20px 0;
        }

        /* Vertical line of the timeline */
        .timeline::before {
            content: '';
            position: absolute;
            left: 30px;
            top: 0;
            bottom: 0;
            width: 3px;
            background: #dee2e6;
        }

        /* Each stage in timeline */
        .timeline-stage {
            position: relative;
            padding-left: 70px;
            padding-bottom: 40px;
        }
        .timeline-stage:last-child {
            padding-bottom: 0;
        }

        /* Main icon of stage */
        .stage-icon {
            position: absolute;
            left: 12px;
            width: 36px;
            height: 36px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 16px;
            z-index: 3;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        }

        .stage-icon.completed { background: #198754; }
        .stage-icon.active { background: #0d6efd; }
        .stage-icon.rejected { background: #dc3545; }

        /* Stage container */
        .stage-content {
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .stage-title {
            font-weight: 600;
            font-size: 1.2rem;
            margin-bottom: 15px;
            color: #212529;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        /* Steps container */
        .stage-steps {
            margin-left: 15px;
            border-left: 2px solid #e9ecef;
            padding-left: 20px;
        }

        /* Each small step */
        .step-item {
            position: relative;
            padding: 12px 0;
            display: flex;
            align-items: flex-start;
            gap: 12px;
        }

        .step-item::before {
            content: '';
            position: absolute;
            left: -26px;
            top: 20px;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: white;
            border: 2px solid currentColor;
        }

        .step-item.completed::before {
            border-color: #198754;
            background: #198754;
        }
        .step-item.active::before {
            border-color: #0d6efd;
            background: #0d6efd;
            animation: pulse 2s ease-in-out infinite;
        }
        .step-item.pending::before {
            border-color: #ffc107;
        }
        .step-item.rejected::before {
            border-color: #dc3545;
            background: #dc3545;
        }

        @keyframes pulse {
            0%, 100% {
                box-shadow: 0 0 0 0 rgba(13, 110, 253, 0.7);
            }
            50% {
                box-shadow: 0 0 0 8px rgba(13, 110, 253, 0);
            }
        }

        .step-icon {
            font-size: 18px;
            margin-top: 2px;
        }

        .step-icon.text-success { color: #198754; }
        .step-icon.text-primary { color: #0d6efd; }
        .step-icon.text-warning { color: #ffc107; }
        .step-icon.text-danger { color: #dc3545; }
        .step-icon.text-secondary { color: #6c757d; }

        .step-text {
            flex: 1;
        }

        .step-status {
            font-weight: 500;
            font-size: 1rem;
            color: #212529;
            margin-bottom: 4px;
        }

        .step-timestamp {
            font-size: 0.875rem;
            color: #6c757d;
        }

        /* Action buttons */
        .stage-actions {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #e9ecef;
        }

        /* Highlight for active stage */
        .timeline-stage.active-stage .stage-content {
            border-color: #0d6efd;
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.15);
        }

        /* Feedback countdown badge */
        .countdown-badge {
            display: inline-block;
            padding: 4px 12px;
            background: #fff3cd;
            color: #856404;
            border-radius: 20px;
            font-size: 0.875rem;
            font-weight: 500;
            margin-left: 8px;
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="container py-5">
    <c:set var="journey" value="${requestScope.journey}" />

    <h2 class="mb-4 text-center" style="color: #5a6268">Tracking the Repair Process</h2>
    <h5 class="text-center text-muted mb-5">Request #${journey.serviceRequest.requestID}</h5>

    <div class="card mb-5 shadow-sm">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0"><i class="bi bi-car-front-fill"></i> Vehicle & Services Information</h5>
        </div>
        <div class="card-body">
            <div class="row mb-3">
                <div class="col-md-6">
                    <h6 class="text-muted">Vehicle Information</h6>
                    <ul class="list-unstyled mb-0">
                        <li><strong>Brand:</strong> ${vehicle.brand}</li>
                        <li><strong>Model:</strong> ${vehicle.model}</li>
                        <li><strong>License Plate:</strong> ${vehicle.licensePlate}</li>
                    </ul>
                </div>

                <div class="col-md-6">
                    <h6 class="text-muted">Requested Services</h6>
                    <ul class="list-group list-group-flush">
                        <c:forEach var="name" items="${serviceName}">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span><i class="bi bi-wrench-adjustable-circle text-primary"></i> ${name}</span>
                                <i class="bi bi-check-circle-fill text-success"></i>
                            </li>
                        </c:forEach>
                        <c:if test="${empty serviceName}">
                            <li class="list-group-item text-muted">No services listed</li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="timeline">

                <%-- Loop through all stages --%>
                <c:forEach var="stage" items="${journey.stages}" varStatus="status">
                    <div class="timeline-stage ${stage.overallStatus == 'active' ? 'active-stage' : ''}">

                            <%-- Stage icon --%>
                        <div class="stage-icon ${stage.overallStatus}">
                            <i class="${stage.icon}"></i>
                        </div>

                            <%-- Stage content --%>
                        <div class="stage-content">
                            <div class="stage-title">
                                <i class="${stage.icon}"></i>
                                    ${stage.stageTitle}
                            </div>

                                <%-- List of steps --%>
                            <div class="stage-steps">
                                <c:forEach var="step" items="${stage.steps}">
                                    <div class="step-item ${step.completed ? 'completed' : 'active'}">
                                        <div class="step-icon text-${step.statusColor}">
                                            <i class="bi bi-${step.statusIcon}-fill"></i>
                                        </div>
                                        <div class="step-text">
                                            <div class="step-status">${step.statusText}</div>
                                            <c:if test="${not empty step.timestamp}">
                                                <div class="step-timestamp">
                                                    <i class="bi bi-clock"></i> ${step.timestamp}
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                                <%-- Action buttons for specific stages --%>
                            <c:choose>

                                <c:when test="${stage.stageType == 'WORK_ORDER'}">
                                    <div class="stage-actions">
                                        <a href="${pageContext.request.contextPath}/customer/diagnostic/tree?requestId=${journey.serviceRequest.requestID}"
                                           class="btn btn-outline-primary btn-sm">
                                            <i class="bi bi-clipboard-check"></i>
                                            Xem chi tiết chẩn đoán &amp; phụ tùng
                                        </a>
                                    </div>
                                </c:when>
                                
                                <%-- Invoice: View invoice details --%>
                                <c:when test="${stage.stageType == 'INVOICE' && journey.invoice != null &&
                                              (journey.invoice.paymentStatus == 'UNPAID' ||
                                               journey.invoice.paymentStatus == 'PARTIALLY_PAID')}">
                                    <div class="stage-actions">
                                        <a href="${pageContext.request.contextPath}/customer/invoice?id=${journey.invoice.invoiceID}"
                                           class="btn btn-primary btn-sm">
                                            <i class="bi bi-eye"></i> View Invoice Details
                                        </a>
                                    </div>
                                </c:when>

                                <%-- Feedback: Submit feedback --%>
                                <c:when test="${stage.stageType == 'FEEDBACK'}">
                                    <div class="stage-actions">
                                        <c:choose>
                                            <c:when test="${journey.feedbackAction == 'ALLOW_FEEDBACK'}">
                                                <a href="${pageContext.request.contextPath}/customer/send-feedback?workOrderID=${journey.workOrder.workOrderId}"
                                                   class="btn btn-success btn-sm">
                                                    <i class="bi bi-pencil"></i> Submit Feedback Now
                                                </a>
                                                <c:if test="${journey.feedbackDaysLeft != null}">
                                                    <span class="countdown-badge">
                                                        <i class="bi bi-hourglass-split"></i>
                                                        ${journey.feedbackDaysLeft} days left
                                                    </span>
                                                </c:if>
                                            </c:when>
                                            <c:when test="${journey.feedbackAction == 'HAS_FEEDBACK'}">
                                                <a href="${pageContext.request.contextPath}/customer/view-feedback?feedbackId=${journey.feedback.feedbackID}"
                                                   class="btn btn-primary btn-sm">
                                                    <i class="bi bi-eye"></i> View Your Feedback
                                                </a>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>

            </div>

            <div class="text-center mt-5">
                <a href="${pageContext.request.contextPath}/customer/repair-list?
          <c:if test='${not empty vehicleId}'>vehicleId=${vehicleId}&</c:if>
          <c:if test='${not empty sortBy}'>sortBy=${sortBy}&</c:if>
          <c:if test='${not empty page}'>page=${page}</c:if>"
                   class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Repair List
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
