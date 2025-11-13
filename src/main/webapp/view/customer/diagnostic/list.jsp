<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh Sách Chẩn Đoán</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body>
<jsp:include page="/common/header.jsp"/>

<c:set var="result" value="${requestScope.result}" />
<c:set var="vm" value="${result.data}" />

<div class="container py-4">

    <h3 class="mb-3">Danh sách chẩn đoán</h3>

    <c:if test="${not empty sessionScope.flash}">
        <c:set var="flash" value="${sessionScope.flash}" />
        <c:remove var="flash" scope="session"/>
        <div class="alert ${flash.success ? 'alert-success' : 'alert-danger'}">
                ${flash.message}
        </div>
    </c:if>

    <c:if test="${empty vm.rows}">
        <div class="alert alert-info">
            Không có chẩn đoán nào trong yêu cầu này.
        </div>
    </c:if>

    <c:if test="${not empty vm.rows}">
        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>Mã</th>
                <th>Vấn đề</th>
                <th>Trạng thái</th>
                <th>Phụ tùng đã chọn</th>
                <th>Tổng phụ tùng</th>
                <th>Tạo lúc</th>
                <th></th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="d" items="${vm.rows}">
                <tr>
                    <td>#${d.vehicleDiagnosticID}</td>

                    <td>
                        <div class="fw-semibold">${d.issueFound}</div>
                        <div class="text-muted small">${d.vehicleInfo}</div>
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${d.statusString == 'SUBMITTED'}">
                                <span class="badge bg-warning text-dark">Chờ khách duyệt</span>
                            </c:when>
                            <c:when test="${d.statusString == 'APPROVED'}">
                                <span class="badge bg-success">Đã duyệt</span>
                            </c:when>
                            <c:when test="${d.statusString == 'REJECTED'}">
                                <span class="badge bg-danger">Từ chối</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary">${d.statusString}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <span class="fw-semibold text-success">
                                ${vm.partsApproved[d.vehicleDiagnosticID]}
                        </span>
                    </td>

                    <td>
                        <span class="fw-semibold">
                                ${vm.partsTotal[d.vehicleDiagnosticID]}
                        </span>
                    </td>

                    <td>${d.createdAtFormatted}</td>

                    <td class="text-end">

                        <a class="btn btn-outline-primary btn-sm"
                           href="${pageContext.request.contextPath}/customer/diagnostic/detail?requestId=${param.requestId}&diagnosticId=${d.vehicleDiagnosticID}">
                            Xem chi tiết
                        </a>

                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <a href="${pageContext.request.contextPath}/customer/repair-tracker?id=${param.requestId}"
       class="btn btn-secondary mt-3">
        &laquo; Quay về theo dõi sửa chữa
    </a>

</div>

<jsp:include page="/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
