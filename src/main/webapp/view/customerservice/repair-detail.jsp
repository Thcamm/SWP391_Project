<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Repair Process Tracking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
        body {
            background-color: #f8f9fa;
        }
        .star-rating i {
            color: #f5b301;
            font-size: 18px;
        }
        .timeline {
            position: relative;
            padding: 20px 0;
        }

        /* Vertical line of timeline */
        .timeline::before {
            content: '';
            position: absolute;
            left: 30px;
            top: 0;
            bottom: 0;
            width: 3px;
            background: #dee2e6;
        }

        /* Each stage in the timeline */
        .timeline-stage {
            position: relative;
            padding-left: 70px;
            padding-bottom: 40px;
        }
        .timeline-stage:last-child {
            padding-bottom: 0;
        }

        /* Stage icon */
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

        /* Stage content container */
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

        /* Each step */
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
            0%, 100% { box-shadow: 0 0 0 0 rgba(13, 110, 253, 0.7); }
            50% { box-shadow: 0 0 0 8px rgba(13, 110, 253, 0); }
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

        /* Highlight active stage */
        .timeline-stage.active-stage .stage-content {
            border-color: #0d6efd;
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.15);
        }

        /* Countdown badge for feedback */
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

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           align-items: center;">
                    <div class="container py-5">
                        <c:set var="journey" value="${requestScope.journey}" />

                        <h2 class="mb-4 text-center">Repair Process Tracking</h2>
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
                                                    <c:when test="${stage.stageType eq 'WORK_ORDER' && journey.workOrder != null}">
                                                        <div class="stage-actions">
                                                            <a href="${pageContext.request.contextPath}/customerservice/workorder-detail?id=${journey.workOrder.workOrderId}"
                                                               class="btn btn-primary btn-sm">
                                                                <i class="bi bi-eye"></i> View Repair Details
                                                            </a>
                                                        </div>
                                                    </c:when>

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

                                                    <c:when test="${stage.stageType == 'FEEDBACK'}">
                                                        <div class="stage-actions">
                                                            <c:choose>
                                                                <c:when test="${journey.feedbackAction == 'HAS_FEEDBACK'}">
                                                                    <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#feedbackModal">
                                                                        <i class="bi bi-eye"></i> View Feedback
                                                                    </button>
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
                                    <%-- Go back to previous page in browser history --%>
                                    <a href="javascript:history.back()" class="btn btn-secondary">
                                        <i class="bi bi-arrow-left"></i> Go Back
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<!-- Feedback Modal -->
<div class="modal fade" id="feedbackModal" tabindex="-1" aria-labelledby="feedbackModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" style="color: #0b0f14">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="feedbackModalLabel">Feedback Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="feedback-info">
                    <p><strong>Customer:</strong> ${customerName}</p>
                    <p><strong>Work Order ID:</strong> ${feedback.workOrderID}</p>
                    <p><strong>Rating:</strong>
                        <span class="star-rating">
                            <c:forEach var="i" begin="1" end="5">
                                <i class="bi ${i <= feedback.rating ? 'bi-star-fill' : 'bi-star'} text-warning"></i>
                            </c:forEach>
                        </span>
                    </p>
                    <p><strong>Comment:</strong> ${feedback.feedbackText}</p>
                    <p class="feedback-date">
                        <i class="fa fa-clock"></i> Submitted on: ${feedback.feedbackDate}
                    </p>
                    <c:if test="${not empty feedback.replyText}">
                        <hr>
                        <p><strong>Garage Reply:</strong> ${feedback.replyText}</p>
                        <p class="feedback-date">
                            <i class="bi bi-reply"></i> Replied on: ${feedback.replyDate}
                        </p>
                    </c:if>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
