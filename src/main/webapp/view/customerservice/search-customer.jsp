<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>

<html
        lang="en"
        class="light-style layout-menu-fixed"
        dir="ltr"
        data-theme="theme-default"
        data-assets-path="../assets/"
        data-template="vertical-menu-template-free"
>
<head>
    <meta charset="utf-8" />
    <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"
    />

    <title>Customer Service - Home</title>

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
            <div class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                             padding-top: 0;">
                    <div class="content-wrapper">
                        <div class="container-fluid flex-grow-1 container-p-y">

                            <div class="container py-4">

                                <!-- HEADER -->
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h3>Search Customer</h3>
                                    <a href="${pageContext.request.contextPath}/view/customerservice/create-customer.jsp" class="btn btn-primary">
                                        <i class="fa-solid fa-user-plus"></i> Create Customer
                                    </a>
                                </div>

                                <!-- SEARCH FORM -->
                                <form action="${pageContext.request.contextPath}/customerservice/search-customer" method="get" class="card p-4 mb-4">
                                    <div class="row g-3">
                                        <div class="col-md-4">
                                            <label for="searchName" class="form-label">Customer Name</label>
                                            <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Enter customer name" />
                                        </div>

                                        <div class="col-md-4">
                                            <label for="searchLicensePlate" class="form-label">License Plate</label>
                                            <input type="text" id="searchLicensePlate" name="searchLicensePlate" value="${param.searchLicensePlate}" class="form-control" placeholder="Enter license plate" />
                                        </div>

                                        <div class="col-md-4">
                                            <label for="searchEmail" class="form-label">Email / Phone Number</label>
                                            <input type="text" id="searchEmail" name="searchEmail" value="${param.searchEmail}" class="form-control" placeholder="Enter email or phone number" />
                                        </div>
                                    </div>

                                    <div class="mt-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
                                        <div class="d-flex align-items-center gap-2">
                                            <label>From Date:</label>
                                            <input type="date" name="fromDate" value="${param.fromDate}" class="form-control form-control-sm" />

                                            <label>To Date:</label>
                                            <input type="date" name="toDate" value="${param.toDate}" class="form-control form-control-sm" />
                                        </div>

                                        <div class="d-flex align-items-center gap-2">
                                            <select id="sortOrder" name="sortOrder" class="form-select w-auto">
                                                <option value="newest"
                                                        <c:if test="${param.sortOrder eq 'newest'}">selected</c:if>>Newest</option>
                                                <option value="oldest"
                                                        <c:if test="${param.sortOrder eq 'oldest'}">selected</c:if>>Oldest</option>
                                            </select>

                                            <button type="submit" class="btn btn-success">🔍 Search</button>
                                        </div>
                                    </div>
                                </form>

                                <!-- CUSTOMER TABLE -->
                                <div class="card">
                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <span>List Of Customer</span>
                                    </div>

                                    <div class="table-responsive">
                                        <table class="table table-bordered align-middle mb-0">
                                            <thead class="table-light">
                                            <tr>
                                                <th>No</th>
                                                <th>Customer Name</th>
                                                <th>License Plate</th>
                                                <th>Email</th>
                                                <th>Phone Number</th>
                                                <th>Actions</th>
                                            </tr>
                                            </thead>

                                            <tbody>
                                            <c:choose>
                                                <c:when test="${empty customerList.paginatedData}">
                                                    <tr class="text-center text-muted">
                                                        <td colspan="6">No customers found</td>
                                                    </tr>
                                                </c:when>

                                                <c:otherwise>
                                                    <c:forEach var="c" items="${customerList.paginatedData}" varStatus="loop">
                                                        <tr>
                                                            <td>${(customerList.currentPage - 1) * customerList.itemsPerPage + loop.index + 1}</td>
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/user/profile?id=${c.userId}">
                                                                        ${c.fullName}
                                                                </a>
                                                            </td>
                                                            <td>
                                                                <div>
                                                                    <c:choose>
                                                                        <c:when test="${fn:length(c.vehicles) == 0}">
                                                                            <span class="text-muted">Nothing</span>
                                                                        </c:when>
                                                                        <c:when test="${fn:length(c.vehicles) <= 2}">
                                                                            <!-- Nếu có 1-2 xe, hiển thị tất cả -->
                                                                            <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                                                                ${v.licensePlate}<c:if test="${!vs.last}">, </c:if>
                                                                            </c:forEach>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <!-- Nếu có > 2 xe, hiển thị 2 xe đầu + nút toggle -->
                                                                            <span class="visible-vehicles-${c.customerId}">
                                                                                <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                                                                    <c:if test="${vs.index < 2}">
                                                                                        ${v.licensePlate}<c:if test="${vs.index < 1}">, </c:if>
                                                                                    </c:if>
                                                                                </c:forEach>
                                                                            </span>

                                                                            <!-- Các xe còn lại (ẩn mặc định) -->
                                                                            <span class="hidden-vehicles-${c.customerId}" style="display:none;">
                                                                                <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                                                                    <c:if test="${vs.index >= 2}">
                                                                                        , ${v.licensePlate}
                                                                                    </c:if>
                                                                                </c:forEach>
                                                                            </span>

                                                                            <!-- Nút toggle -->
                                                                            <button type="button"
                                                                                    class="btn btn-link p-0 ms-1 toggle-vehicles-btn"
                                                                                    data-customer-id="${c.customerId}"
                                                                                    style="font-size: 0.85rem; text-decoration: none; vertical-align: baseline;">
                                                                                <i class="fas fa-chevron-down"></i> See more (${fn:length(c.vehicles) - 2})
                                                                            </button>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </td>

                                                            <td>${c.email}</td>
                                                            <td>${c.phoneNumber}</td>
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/customerservice/createRequest?customerId=${c.customerId}"
                                                                   class="btn btn-sm btn-success">
                                                                    Create Request
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>

                                <!-- PAGINATION -->
                                <jsp:include page="/view/customerservice/pagination.jsp">
                                    <jsp:param name="currentPage" value="${customerList.currentPage}" />
                                    <jsp:param name="totalPages" value="${customerList.totalPages}" />
                                    <jsp:param name="queryString"
                                               value="&searchName=${param.searchName}&searchLicensePlate=${param.searchLicensePlate}&searchEmail=${param.searchEmail}&fromDate=${param.fromDate}&toDate=${param.toDate}&sortOrder=${param.sortOrder}" />
                                </jsp:include>
                            </div>
                        </div>
                        <div class="content-backdrop fade"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        // Xử lý tất cả các nút toggle
        document.querySelectorAll('.toggle-vehicles-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const customerId = this.getAttribute('data-customer-id');
                const hiddenVehicles = document.querySelector('.hidden-vehicles-' + customerId);

                if (hiddenVehicles) {
                    const isHidden = hiddenVehicles.style.display === 'none';

                    // Toggle hiển thị
                    hiddenVehicles.style.display = isHidden ? 'inline' : 'none';

                    // Đổi text và icon của nút
                    if (isHidden) {
                        this.innerHTML = '<i class="fas fa-chevron-up"></i> Hide';
                    } else {
                        const totalVehicles = hiddenVehicles.textContent.split(',').length;
                        this.innerHTML = '<i class="fas fa-chevron-down"></i> See more (' + totalVehicles + ')';
                    }
                }
            });
        });
    });
</script>

</body>
</html>