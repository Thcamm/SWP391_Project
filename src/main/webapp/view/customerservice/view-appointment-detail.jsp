<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Appointment Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .detail-card {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 2.5rem;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .detail-item {
            padding: 0.75rem 0;
            border-bottom: 1px dashed #e5e7eb;
        }
        .detail-item:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: 600;
            color: #4b5563; /* Gray-600 */
        }
        .detail-value {
            color: #111827; /* Gray-900 */
        }
        /* CSS cho Status Badges */
        .badge {
            font-size: 0.8rem;
            padding: 0.4em 0.7em;
            font-weight: 600;
        }
        .status-pending {
            background-color: #fef3c7;
            color: #92400e;
        }
        .status-accepted {
            background-color: #d1fae5;
            color: #065f46;
        }
        .status-rejected {
            background-color: #fee2e2;
            color: #991b1b;
        }
        .status-cancelled {
            background-color: #e5e7eb;
            color: #374151;
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
                <div class="container py-4" style="padding: 0 !important;">
                    <%-- Giả định có file result.jsp để hiển thị thông báo/lỗi --%>
                    <jsp:include page="/view/customerservice/result.jsp"/>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 style="margin: 0; font-size: 24px; font-weight: 700; color: #111827;">
                                <i class="bi bi-calendar-event me-2"></i>Appointment Details
                            </h2>
                            <p style="margin: 0.5rem 0 0 0; color: #6b7280; font-size: 14px;">Review and manage Appointment ID: **${appointmentDetail.appointmentID}**</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/customerservice/appointment-list" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i> Back to List
                        </a>
                    </div>

                    <c:choose>
                        <c:when test="${appointmentDetail != null}">
                            <div class="detail-card">
                                <h4 class="mb-4 pb-2" style="border-bottom: 1px solid #e5e7eb;">Customer & Appointment Info</h4>

                                <div class="row">
                                    <div class="col-md-6">
                                            <%-- Giả định Appointment model có các phương thức getCustomerName, getCustomerEmail, v.v. hoặc dùng DTO --%>
                                        <div class="detail-item">
                                            <div class="detail-label">Customer Name:</div>
                                            <div class="detail-value fs-5">${customerDetail.fullName}</div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label">Email:</div>
                                            <div class="detail-value">${customerDetail.email}</div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label">Phone:</div>
                                            <div class="detail-value">${customerDetail.phoneNumber}</div>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="detail-item">
                                            <div class="detail-label">Appointment Date:</div>
                                            <div class="detail-value fs-5 text-primary">
                                                <i class="bi bi-calendar-date me-1"></i>
                                                    <%-- Giả định appointmentDate là LocalDateTime (cần xử lý toString() hoặc parse Date/String) --%>
                                                <fmt:parseDate value="${appointmentDetail.appointmentDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate"/>
                                                <fmt:formatDate value="${parsedDate}" pattern="HH:mm dd/MM/yyyy"/>
                                            </div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label">Status:</div>
                                            <div class="detail-value">
                                                <c:set var="status" value="${appointmentDetail.status}"/>
                                                <c:choose>
                                                    <c:when test="${status eq 'PENDING'}">
                                                        <form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="post" class="d-inline">
                                                            <input type="hidden" name="appointmentID" value="${appointmentDetail.appointmentID}">
                                                            <input type="hidden" name="redirectUrl" value="${pageContext.request.contextPath}/customerservice/appointment-detail?id=${appointmentDetail.appointmentID}" />
                                                            <select name="status" class="form-select form-select-sm d-inline-block w-auto ms-1" onchange="this.form.submit()">
                                                                <option value="PENDING" selected>PENDING</option>
                                                                <option value="ACCEPTED">ACCEPTED</option>
                                                                <option value="REJECTED">REJECTED</option>
                                                            </select>
                                                        </form>
                                                    </c:when>
                                                    <c:when test="${status eq 'ACCEPTED'}">
                                                        <span class="badge status-accepted">${appointmentDetail.status}</span>
                                                    </c:when>
                                                    <c:when test="${status eq 'REJECTED'}">
                                                        <span class="badge status-rejected">${appointmentDetail.status}</span>
                                                    </c:when>
                                                    <c:when test="${status eq 'CANCELLED'}">
                                                        <span class="badge status-cancelled">${appointmentDetail.status}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${appointmentDetail.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label">Created At:</div>
                                            <div class="detail-value">${appointmentDetail.createdAt}</div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row mt-4">
                                    <div class="col-12">
                                        <div class="detail-item">
                                            <div class="detail-label">Description:</div>
                                            <div class="detail-value fst-italic">
                                                    ${appointmentDetail.description}
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="d-flex gap-3">
                                        <%-- 1. Create Service Order (SO) --%>
                                    <c:if test="${appointmentDetail.status eq 'ACCEPTED'}">
                                        <a href="${pageContext.request.contextPath}/customerservice/createRequest?appointmentId=${appointmentDetail.appointmentID}&customerId=${appointmentDetail.customerID}"
                                           class="btn btn-success btn-lg">
                                            <i class="bi bi-plus-circle me-1"></i> Create Service Order (SO)
                                        </a>
                                    </c:if>

                                        <%-- 2. Reschedule (Accept) - Cho phép CS chấp nhận/đề xuất lịch mới --%>
                                    <c:if test="${appointmentDetail.status eq 'PENDING' || appointmentDetail.status eq 'REJECTED' || appointmentDetail.status eq 'CANCELLED'}">
                                        <button type="button" class="btn btn-warning btn-lg" data-bs-toggle="modal" data-bs-target="#rescheduleModal">
                                            <i class="bi bi-arrow-repeat me-1"></i> Reschedule
                                        </button>
                                    </c:if>


                                        <%-- NÚT CANCEL ĐÃ ĐƯỢC LOẠI BỎ THEO YÊU CẦU --%>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-danger text-center py-5" role="alert">
                                <h4 class="alert-heading"><i class="bi bi-exclamation-triangle me-2"></i>Appointment Not Found</h4>
                                <p>The appointment ID you are looking for does not exist.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<div class="modal fade" id="rescheduleModal" tabindex="-1" aria-labelledby="rescheduleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg"> <%-- Mở rộng kích thước Modal --%>
        <div class="modal-content">
            <div class="modal-header bg-warning text-white">
                <h5 class="modal-title" id="rescheduleModalLabel"><i class="bi bi-arrow-repeat me-2"></i>Reschedule & Accept</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form action="${pageContext.request.contextPath}/customerservice/appointment-detail" method="post">
                <input type="hidden" name="action" value="reschedule_accept">
                <input type="hidden" name="appointmentID" value="${appointmentDetail.appointmentID}">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.contextPath}/customerservice/appointment-detail?id=${appointmentDetail.appointmentID}" />

                <div class="modal-body">
                    <p class="lead text-dark">Confirm rescheduling and updating status to **ACCEPTED**.</p>

                    <div class="row mb-4">
                        <div class="col-md-6">
                            <%-- THÔNG TIN CŨ (READ-ONLY) --%>
                            <label for="oldDate" class="form-label fw-bold text-muted">Current Appointment Date</label>
                            <input type="text" class="form-control" id="oldDate" readonly
                                   value="<fmt:formatDate value="${parsedDate}" pattern="HH:mm dd/MM/yyyy"/>">
                        </div>
                        <div class="col-md-6">
                            <%-- THÔNG TIN MỚI (CHỈNH SỬA) --%>
                            <label for="newAppointmentDate" class="form-label fw-bold text-primary">New Appointment Date/Time <span class="text-danger">*</span></label>
                            <input type="datetime-local" class="form-control" name="newAppointmentDate" id="newAppointmentDate" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="newDescription" class="form-label fw-bold text-dark">Notes for Update (Optional)</label>
                        <textarea class="form-control" name="description" id="newDescription" rows="2" placeholder="Enter any notes related to the rescheduling process...">${appointmentDetail.description}</textarea>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-warning"><i class="bi bi-check-circle me-1"></i> Confirm Reschedule</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Set min date/time cho input datetime-local để tránh chọn ngày quá khứ
    document.addEventListener("DOMContentLoaded", function() {
        const datetimeInput = document.getElementById('newAppointmentDate');

        // Logic cũ: Set min date/time cho input datetime-local
        if (datetimeInput) {
            const now = new Date();
            now.setMinutes(now.getMinutes() + 5);

            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');

            const minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
            datetimeInput.min = minDateTime;
        }
    });
</script>

</body>
</html>