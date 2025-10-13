<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Tìm kiếm khách hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>

<body class="bg-light">
<div class="container py-4">

    <!-- HEADER -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3>Tìm kiếm khách hàng</h3>
        <a href="${pageContext.request.contextPath}/customer/create-customer.jsp" class="btn btn-primary">
            <i class="bi bi-plus-circle"></i> Tạo khách hàng
        </a>
    </div>

    <!-- SEARCH FORM -->
    <form action="${pageContext.request.contextPath}/search-customer" method="get" class="card p-4 mb-4">
        <div class="row g-3">
            <div class="col-md-4">
                <label for="searchName" class="form-label">Tên khách hàng</label>
                <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Nhập tên khách hàng" />
            </div>

            <div class="col-md-4">
                <label for="searchLicensePlate" class="form-label">Biển số xe</label>
                <input type="text" id="searchLicensePlate" name="searchLicensePlate" value="${param.searchLicensePlate}" class="form-control" placeholder="Nhập biển số xe" />
            </div>

            <div class="col-md-4">
                <label for="searchEmail" class="form-label">Email / SĐT</label>
                <input type="text" id="searchEmail" name="searchEmail" value="${param.searchEmail}" class="form-control" placeholder="Nhập email hoặc SĐT" />
            </div>
        </div>

        <div class="mt-4 d-flex justify-content-between align-items-center">
            <button type="submit" class="btn btn-success">
                🔍 Tìm kiếm
            </button>
            <label>
                <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                Tạo ngày
            </label>
            <input type="date" name="fromDate" value="${param.fromDate}" placeholder="dd/mm/yyyy" />

            <label>
                <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                Đến ngày
            </label>
            <input type="date" name="toDate" value="${param.toDate}" placeholder="dd/mm/yyyy" />

            <select id="sortOrder" name="sortOrder" class="form-select w-auto">
                <option value="newest" ${param.sortOrder == 'newest' ? 'selected' : ''}>Mới nhất</option>
                <option value="oldest" ${param.sortOrder == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
            </select>
        </div>
    </form>

    <!-- CUSTOMER TABLE -->
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span>Danh sách khách hàng</span>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th>STT</th>
                    <th>Tên khách hàng</th>
                    <th>Biển số xe</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${empty customerList}">
                        <tr class="text-center text-muted">
                            <td colspan="5">Chưa có dữ liệu hiển thị</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="c" items="${customerList}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/customer-detail?id=${customer.customerId}">
                                        ${c.fullName}
                                    </a>
                                </td>
                                <td>
                                    <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                        ${v.licensePlate}
                                        <c:if test="${!vs.last}">, </c:if>
                                    </c:forEach>
                                </td>
                                <td>${c.email}</td>
                                <td>${c.phoneNumber}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
<script>
    document.querySelector('form').addEventListener('submit', function(e) {
        const fromDate = document.querySelector('input[name="fromDate"]').value;
        const toDate = document.querySelector('input[name="toDate"]').value;

        if (fromDate && toDate && new Date(fromDate) > new Date(toDate)) {
            e.preventDefault();
            alert("❌ Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }
    });
</script>
</html>
