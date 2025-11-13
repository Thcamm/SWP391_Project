<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
        System.out.println("=== JSP tree.jsp is being rendered ===");
        System.out.println("View object: " + request.getAttribute("view"));
        %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Chẩn Đoán &amp; Phụ Tùng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>
<body>
<%--<jsp:include page="/common/header.jsp"/>--%>
<h1>Debug</h1>
<c:set var="view" value="${requestScope.view}" />

<div class="container py-4">
    <h3 class="mb-3">
        Chẩn đoán &amp; phụ tùng cho Yêu Cầu #${view.request.requestId}
    </h3>

    <p class="text-muted">
        Biển số: <b>${view.vehicle.licensePlate}</b>,
        ${view.vehicle.brand} ${view.vehicle.model}
        <c:if test="${view.vehicle.yearManufacture ne null}">
            (${view.vehicle.yearManufacture})
        </c:if>
    </p>

    <!-- Các service khách đã chọn -->
    <c:if test="${not empty view.requestedServices}">
        <div class="mb-3">
            <span class="fw-semibold">Dịch vụ đã chọn:</span>
            <ul class="mb-0">
                <c:forEach var="s" items="${view.requestedServices}">
                    <li>
                            ${s.serviceName}
                        <c:if test="${not empty s.category}">
                            - ${s.category}
                        </c:if>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <!-- Mỗi ServiceBlock tương ứng 1 WorkOrderDetail -->
    <c:forEach var="block" items="${view.services}">
        <div class="card mb-4">
            <div class="card-header">
                <strong>${block.serviceLabel}</strong>
                <span class="text-muted"> (DetailID: ${block.detailId})</span>
            </div>

            <div class="card-body">

                <c:if test="${empty block.diagnostics}">
                    <p class="text-muted mb-0">Chưa có chẩn đoán nào cho hạng mục này.</p>
                </c:if>

                <c:forEach var="row" items="${block.diagnostics}">
                    <div class="border rounded p-3 mb-3">

                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <div><b>Vấn đề:</b> ${row.diagnostic.issueFound}</div>
                                <div>
                                    <b>Công chẩn đoán:</b>
                                        ${row.diagnostic.estimateCostFormatted}
                                </div>
                                <div>
                                    <b>Tổng ước tính:</b>
                                        ${row.diagnostic.totalEstimateFormatted}
                                </div>
                                <div>
                                    <b>Trạng thái:</b>
                                        ${row.diagnostic.statusString}
                                    <c:if test="${not empty row.diagnostic.rejectReason}">
                                        - Lý do từ chối: ${row.diagnostic.rejectReason}
                                    </c:if>
                                </div>
                                <div>
                                    <b>Tạo lúc:</b> ${row.diagnostic.createdAtFormatted}
                                </div>
                            </div>

                            <!-- Link sang trang detail để khách duyệt từng part -->
                            <div class="text-end">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${pageContext.request.contextPath}/customer/diagnostic/detail?requestId=${view.request.requestId}&diagnosticId=${row.diagnostic.vehicleDiagnosticID}">
                                    Xem &amp; duyệt chi tiết
                                </a>
                            </div>
                        </div>

                        <!-- Bảng parts đề xuất cho diagnostic này -->
                        <c:if test="${not empty row.parts}">
                            <table class="table table-sm mt-2 mb-0 align-middle">
                                <thead>
                                <tr>
                                    <th>Phụ tùng</th>
                                    <th>Loại</th>
<%--                                    <th>Kho</th>--%>
                                    <th class="text-end">SL</th>
                                    <th class="text-end">Đơn giá</th>
                                    <th class="text-end">Thành tiền</th>
                                    <th>Khách chọn?</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="p" items="${row.parts}">
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
<%--                                        <td>--%>
<%--                                            <span class="${p.stockStatusClass}">--%>
<%--                                                    ${p.stockStatusText}--%>
<%--                                            </span>--%>
<%--                                            <div class="small text-muted">--%>
<%--                                                Tồn: ${p.currentStock}--%>
<%--                                            </div>--%>
<%--                                        </td>--%>
                                        <td class="text-end">${p.quantityNeeded}</td>
                                        <td class="text-end">${p.unitPrice}</td>
                                        <td class="text-end">${p.totalPrice}</td>
                                        <td>
                                            <span class="badge bg-${p.approved ? 'success' : 'secondary'}">
                                                    ${p.approved ? 'Đồng ý' : 'Chưa đồng ý'}
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
       class="btn btn-secondary">
        &laquo; Quay lại theo dõi sửa chữa
    </a>
</div>

<%--<jsp:include page="/common/footer.jsp"/>--%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
