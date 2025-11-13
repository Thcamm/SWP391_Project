<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Chẩn Đoán</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>
<body>
<%--<jsp:include page="/common/header.jsp"/>--%>

<c:set var="result" value="${requestScope.result}" />
<c:set var="detail" value="${result.data}" />
<c:set var="diag" value="${detail.diag}" />

<div class="container py-4">

    <h3 class="mb-3">Chi tiết chẩn đoán</h3>

    <!-- Nếu có flash message -->
    <c:if test="${not empty sessionScope.flash}">
        <c:set var="flash" value="${sessionScope.flash}" />
        <c:remove var="flash" scope="session"/>

        <div class="alert ${flash.success ? 'alert-success' : 'alert-danger'}">
            <!-- tuỳ ServiceResult chị hiển thị message gì -->
                ${flash.message}
        </div>
    </c:if>

    <!-- Thông tin chẩn đoán -->
    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title">
                Vấn đề: ${diag.issueFound}
            </h5>
            <p class="mb-1">
                <b>Trạng thái:</b> ${diag.statusString}
                <c:if test="${diag.rejected}">
                    <br/>
                    <b>Lý do từ chối:</b> ${diag.rejectReason}
                </c:if>
            </p>
            <p class="mb-1">
                <b>Công chẩn đoán:</b> ${diag.estimateCostFormatted}
            </p>
            <p class="mb-1">
                <b>Tổng chi phí phụ tùng đã chọn:</b> ${diag.approvedPartsCost}
            </p>
            <p class="mb-0">
                <b>Tổng ước tính:</b> ${diag.totalEstimateFormatted}
            </p>
        </div>
    </div>

    <!-- Danh sách phụ tùng đề xuất -->
    <div class="card mb-4">
        <div class="card-header">
            <b>Phụ tùng đề xuất</b>
        </div>
        <div class="card-body">

            <c:if test="${empty detail.parts}">
                <p class="text-muted mb-0">Không có phụ tùng nào trong chẩn đoán này.</p>
            </c:if>

            <c:if test="${not empty detail.parts}">
                <table class="table table-sm align-middle">
                    <thead>
                    <tr>
                        <th>Phụ tùng</th>
                        <th>Loại</th>
<%--                        <th>Kho</th>--%>
                        <th class="text-end">SL</th>
                        <th class="text-end">Đơn giá</th>
                        <th class="text-end">Thành tiền</th>
                        <th class="text-center">Khách chọn?</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${detail.parts}">
                        <tr>
                            <td>
                                <div class="fw-semibold">${p.partName}</div>
                                <div class="text-muted small">
                                    Mã: ${p.partCode}
                                    <c:if test="${not empty p.sku}">
                                        - SKU: ${p.sku}
                                    </c:if>
                                </div>
                                <c:if test="${not empty p.reasonForReplacement}">
                                    <div class="small">
                                        Lý do: ${p.reasonForReplacement}
                                    </div>
                                </c:if>
                            </td>
                            <td>
                                <span class="badge ${p.partCondition.badgeClass}">
                                        ${p.partCondition.displayText}
                                </span>
                            </td>
<%--                            <td>--%>
<%--                                <span class="${p.stockStatusClass}">--%>
<%--                                        ${p.stockStatusText}--%>
<%--                                </span>--%>
<%--                                <div class="small text-muted">--%>
<%--                                    Tồn: ${p.currentStock}--%>
<%--                                </div>--%>
<%--                            </td>--%>
                            <td class="text-end">${p.quantityNeeded}</td>
                            <td class="text-end">${p.unitPrice}</td>
                            <td class="text-end">${p.totalPrice}</td>
                            <td class="text-center">
                                <!-- Form toggle chọn/bỏ cho từng part -->
                                <form method="post"
                                      action="${pageContext.request.contextPath}/customer/diagnostic/part"
                                      class="d-inline">
                                    <input type="hidden" name="diagnosticPartId"
                                           value="${p.diagnosticPartID}"/>
                                    <input type="hidden" name="diagnosticId"
                                           value="${diag.vehicleDiagnosticID}"/>
                                    <input type="hidden" name="requestId"
                                           value="${param.requestId}"/>
                                    <!-- Nếu đang approved thì gửi approved=false, ngược lại gửi true -->
                                    <input type="hidden" name="approved"
                                           value="${p.approved ? 'false' : 'true'}"/>

                                    <button type="submit"
                                            class="btn btn-sm ${p.approved ? 'btn-outline-danger' : 'btn-outline-success'}"
                                        ${diag.submitted ? '' : 'disabled'}>
                                        <c:choose>
                                            <c:when test="${p.approved}">
                                                Bỏ chọn
                                            </c:when>
                                            <c:otherwise>
                                                Chọn
                                            </c:otherwise>
                                        </c:choose>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>

    <!-- Khu vực chốt quyết định -->
    <div class="card mb-4">
        <div class="card-header">
            <b>Chốt quyết định</b>
        </div>
        <div class="card-body">

            <c:if test="${!diag.submitted}">
                <p class="text-muted mb-0">
                    Chẩn đoán đã được chốt trước đó, không thể thay đổi nữa.
                </p>
            </c:if>

            <c:if test="${diag.submitted}">
                <p class="text-muted">
                    Sau khi bấm <b>Chấp nhận chẩn đoán</b>, hệ thống sẽ tạo công việc sửa chữa
                    tương ứng từ các phụ tùng bạn đã chọn.
                </p>

                <div class="d-flex flex-column flex-md-row gap-3">
                    <!-- Form APPROVE -->
                    <c:if test="${detail.canApprove}">
                        <form method="post"
                              action="${pageContext.request.contextPath}/customer/diagnostic/finalize">
                            <input type="hidden" name="diagnosticId"
                                   value="${diag.vehicleDiagnosticID}"/>
                            <input type="hidden" name="requestId"
                                   value="${param.requestId}"/>
                            <input type="hidden" name="approve" value="true"/>

                            <button type="submit" class="btn btn-success">
                                Chấp nhận chẩn đoán
                            </button>
                        </form>
                    </c:if>

                    <!-- Form REJECT -->
                    <c:if test="${detail.canReject}">
                        <form method="post"
                              action="${pageContext.request.contextPath}/customer/diagnostic/finalize"
                              class="flex-fill">
                            <input type="hidden" name="diagnosticId"
                                   value="${diag.vehicleDiagnosticID}"/>
                            <input type="hidden" name="requestId"
                                   value="${param.requestId}"/>
                            <input type="hidden" name="approve" value="false"/>

                            <div class="mb-2">
                                <label for="rejectReason" class="form-label">
                                    Lý do từ chối (tuỳ chọn)
                                </label>
                                <textarea id="rejectReason" name="rejectReason"
                                          class="form-control" rows="2"
                                          placeholder="Ví dụ: Chi phí quá cao, muốn trì hoãn..."></textarea>
                            </div>

                            <button type="submit" class="btn btn-outline-danger">
                                Từ chối chẩn đoán
                            </button>
                        </form>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>

    <a href="${pageContext.request.contextPath}/customer/diagnostic/tree?requestId=${param.requestId}"
       class="btn btn-secondary">
        &laquo; Quay lại danh sách chẩn đoán
    </a>
</div>

<%--<jsp:include page="/common/footer.jsp"/>--%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
