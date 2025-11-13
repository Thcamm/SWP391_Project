<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Theo Dõi Quy Trình Sửa Chữa</title>
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

        /* Đường kẻ dọc của timeline */
        .timeline::before {
            content: '';
            position: absolute;
            left: 30px;
            top: 0;
            bottom: 0;
            width: 3px;
            background: #dee2e6;
        }

        /* Mỗi stage trong timeline */
        .timeline-stage {
            position: relative;
            padding-left: 70px;
            padding-bottom: 40px;
        }
        .timeline-stage:last-child {
            padding-bottom: 0;
        }

        /* Icon chính của stage */
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

        /* Container của stage */
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

        /* Container cho các steps */
        .stage-steps {
            margin-left: 15px;
            border-left: 2px solid #e9ecef;
            padding-left: 20px;
        }

        /* Mỗi step nhỏ */
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

        /* Highlight cho stage đang active */
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

    <h2 class="mb-4 text-center">Theo Dõi Quy Trình Sửa Chữa</h2>
    <h5 class="text-center text-muted mb-5">Yêu Cầu #${journey.serviceRequest.requestID}</h5>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="timeline">

                <%-- Lặp qua tất cả các stages --%>
                <c:forEach var="stage" items="${journey.stages}" varStatus="status">
                    <div class="timeline-stage ${stage.overallStatus == 'active' ? 'active-stage' : ''}">

                            <%-- Icon của stage --%>
                        <div class="stage-icon ${stage.overallStatus}">
                            <i class="${stage.icon}"></i>
                        </div>

                            <%-- Nội dung của stage --%>
                        <div class="stage-content">
                            <div class="stage-title">
                                <i class="${stage.icon}"></i>
                                    ${stage.stageTitle}
                            </div>

                                <%-- Danh sách các steps --%>
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

                                <%-- Action buttons cho các stage cụ thể --%>
                            <c:choose>
                                <%-- Invoice: Xem chi tiết hóa đơn --%>
                                <c:when test="${stage.stageType == 'INVOICE' && journey.invoice != null }">
                                    <div class="stage-actions">
                                        <a href="${pageContext.request.contextPath}/customer/invoice?id=${journey.invoice.invoiceID}"
                                           class="btn btn-primary btn-sm">
                                            <i class="bi bi-eye"></i> Xem Chi Tiết Hóa Đơn
                                        </a>
                                    </div>
                                </c:when>

                                <%-- Feedback: Gửi đánh giá --%>
                                <c:when test="${stage.stageType == 'FEEDBACK'}">
                                    <div class="stage-actions">
                                        <c:choose>
                                            <c:when test="${journey.feedbackAction == 'ALLOW_FEEDBACK'}">
                                                <a href="${pageContext.request.contextPath}/customer/send-feedback?workOrderID=${journey.workOrder.workOrderId}"
                                                   class="btn btn-success btn-sm">
                                                    <i class="bi bi-pencil"></i> Gửi Đánh Giá Ngay
                                                </a>
                                                <c:if test="${journey.feedbackDaysLeft != null}">
                                                    <span class="countdown-badge">
                                                        <i class="bi bi-hourglass-split"></i>
                                                        Còn ${journey.feedbackDaysLeft} ngày
                                                    </span>
                                                </c:if>
                                            </c:when>
                                            <c:when test="${journey.feedbackAction == 'HAS_FEEDBACK'}">
                                                <a href="${pageContext.request.contextPath}/customer/view-feedback?feedbackId=${journey.feedback.feedbackID}"
                                                   class="btn btn-primary btn-sm">
                                                    <i class="bi bi-eye"></i> Xem Đánh Giá Của Bạn
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
                <a href="${pageContext.request.contextPath}/customer/repair-list" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Quay Lại Danh Sách
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>