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

    <title>Customer Service - Search</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

</head>

<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <div class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                             border: 1px solid #e5e7eb;
                             border-radius: 12px;
                             padding: 1.5rem 2.5rem 2.5rem 2.5rem; <%-- Sửa padding --%>
                             min-height: calc(100vh - 64px - 1.25rem);
                             display: flex; flex-direction: column;">

                    <div class="content-wrapper">
                        <div class="container-fluid flex-grow-1 container-p-y" style="padding: 0;"> <%-- Sửa padding --%>

                            <div class="container py-4" style="padding: 0 !important;"> <%-- Sửa padding --%>

                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <div>
                                        <h2 style="margin: 0; font-size: 24px; font-weight: 700; color: #111827;">Search Customer</h2>
                                        <p style="margin: 0.5rem 0 0 0; color: #6b7280; font-size: 14px;">Find, view, and manage customer details</p>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/view/customerservice/create-customer.jsp" class="btn btn-primary" style="border-radius: 10px;">
                                        <i class="bi bi-person-plus-fill me-2"></i> Create Customer
                                    </a>
                                </div>

                                <form action="${pageContext.request.contextPath}/customerservice/search-customer" method="get" class="card p-4 mb-4" style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                                    <div class="row g-3">

                                        <div class="col-md-4">
                                            <label for="searchName" class="form-label">Customer Name</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="bi bi-person"></i></span>
                                                <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Enter customer name" />
                                            </div>
                                        </div>

                                        <div class="col-md-4">
                                            <label for="searchLicensePlate" class="form-label">License Plate</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="bi bi-car-front"></i></span>
                                                <input type="text" id="searchLicensePlate" name="searchLicensePlate" value="${param.searchLicensePlate}" class="form-control" placeholder="Enter license plate" />
                                            </div>
                                        </div>

                                        <div class="col-md-4">
                                            <label for="searchEmail" class="form-label">Email / Phone</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="bi bi-envelope-at"></i></span>
                                                <input type="text" id="searchEmail" name="searchEmail" value="${param.searchEmail}" class="form-control" placeholder="Enter email or phone" />
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mt-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
                                        <div class="d-flex align-items-center gap-2">
                                            <label class="form-label mb-0">From:</label>
                                            <input type="date" id="fromDate" name="fromDate" value="${param.fromDate}" class="form-control form-control-sm" style="width: auto;"/>

                                            <label class="form-label mb-0 ms-2">To:</label>
                                            <input type="date" id="toDate" name="toDate" value="${param.toDate}" class="form-control form-control-sm" style="width: auto;"/>
                                        </div>

                                        <div class="d-flex align-items-center gap-2">
                                            <label class="form-label mb-0">Sort by:</label>
                                            <select id="sortOrder" name="sortOrder" class="form-select form-select-sm w-auto">
                                                <option value="newest"
                                                        <c:if test="${param.sortOrder eq 'newest'}">selected</c:if>>Newest</option>
                                                <option value="oldest"
                                                        <c:if test="${param.sortOrder eq 'oldest'}">selected</c:if>>Oldest</option>
                                            </select>
                                        </div>
                                    </div>
                                </form>

                                <div class="card" style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                                    <div class="card-header d-flex justify-content-between align-items-center" style="background-color: transparent; border-bottom: 1px solid #e5e7eb;">
                                        <h5 class="mb-0">List Of Customers</h5>
                                        <span class="text-muted" style="font-size: 0.9rem;">
                                            Showing <strong>${fn:length(customerList.paginatedData)}</strong> of <strong>${customerList.totalItems}</strong> results
                                        </span>
                                    </div>

                                    <div class="table-responsive">
                                        <%-- Sửa: Bỏ table-bordered, thêm table-hover --%>
                                            <table class="table table-hover align-middle mb-0" style="table-layout: fixed;">
                                                <thead class="table-light">
                                                <tr>
                                                    <%-- Sửa: Cho cột 'No' nhỏ lại --%>
                                                    <th style="width: 5%;">No</th>

                                                    <th style="width: 20%;">Customer Name</th>

                                                    <%-- Sửa: Cho cột 'License Plate' rộng ra --%>
                                                    <th style="width: 30%;">License Plate(s)</th>

                                                    <th style="width: 20%;">Email</th>
                                                    <th style="width: 15%;">Phone Number</th>

                                                    <%-- Cho cột 'Actions' nhỏ lại --%>
                                                    <th class="text-center" style="width: 10%;">Actions</th>
                                                </tr>
                                                </thead>

                                            <tbody>
                                            <c:choose>
                                                <c:when test="${empty customerList.paginatedData}">
                                                    <tr class="text-center text-muted">
                                                        <td colspan="6" class="py-5">
                                                            <i class="bi bi-inbox-fill" style="font-size: 3rem; color: #e5e7eb;"></i>
                                                            <p class="mb-0 mt-2">No customers found</p>
                                                        </td>
                                                    </tr>
                                                </c:when>

                                                <c:otherwise>
                                                    <c:forEach var="c" items="${customerList.paginatedData}" varStatus="loop">
                                                        <tr>
                                                            <td>${(customerList.currentPage - 1) * customerList.itemsPerPage + loop.index + 1}</td>
                                                            <td style="cursor: pointer;" onclick="window.location='${pageContext.request.contextPath}/customerservice/customer-detail?id=${c.customerId}'">
                                                                <strong class="text-dark">${c.fullName}</strong>
                                                            </td>

                                                            <td>
                                                                <div>
                                                                    <c:choose>
                                                                        <c:when test="${fn:length(c.vehicles) == 0}">
                                                                            <span class="text-muted" style="font-size: 0.9rem;">(No vehicles)</span>
                                                                        </c:when>
                                                                        <c:when test="${fn:length(c.vehicles) <= 2}">
                                                                            <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                                                                <span class="badge bg-secondary bg-opacity-10 text-dark-emphasis">${v.licensePlate}</span>
                                                                            </c:forEach>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <%-- Hiển thị 2 xe đầu --%>
                                                                            <span class="visible-vehicles-${c.customerId}">
                                                                                <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                                                                    <c:if test="${vs.index < 2}">
                                                                                        <span class="badge bg-secondary bg-opacity-10 text-dark-emphasis me-1">${v.licensePlate}</span>
                                                                                    </c:if>
                                                                                </c:forEach>
                                                                            </span>

                                                                            <%-- Các xe còn lại (ẩn) --%>
                                                                            <span class="hidden-vehicles-${c.customerId}" style="display:none;">
                                                                                <c:forEach var="v" items="${c.vehicles}" varStatus="vs">
                                                                                    <c:if test="${vs.index >= 2}">
                                                                                        <span class="badge bg-secondary bg-opacity-10 text-dark-emphasis me-1">${v.licensePlate}</span>
                                                                                    </c:if>
                                                                                </c:forEach>
                                                                            </span>

                                                                            <%-- Sửa: Nút toggle kiểu badge --%>
                                                                            <button type="button"
                                                                                    class="badge bg-primary bg-opacity-10 text-primary-emphasis toggle-vehicles-btn"
                                                                                    data-customer-id="${c.customerId}">
                                                                                +${fn:length(c.vehicles) - 2} more
                                                                            </button>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </td>

                                                            <td>${c.email}</td>
                                                            <td>${c.phoneNumber}</td>
                                                            <td class="text-center">
                                                                <a href="${pageContext.request.contextPath}/customerservice/createRequest?customerId=${c.customerId}"
                                                                   class="btn btn-sm btn-success">
                                                                    <i class="bi bi-plus-circle me-1"></i> Create Request
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

                                <jsp:include page="/view/customerservice/pagination.jsp">
                                    <jsp:param name="currentPage" value="${customerList.currentPage}" />
                                    <jsp:param name="totalPages" value="${customerList.totalPages}" />
                                    <jsp:param name="baseUrl" value="/customerservice/search-customer" />
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


<%-- THÊM CSS TÙY CHỈNH --%>
<style>
    /* CSS cho input group */
    .input-group-text {
        background-color: #f9fafb;
        border-right: none;
        color: #6b7280;
    }
    .input-group .form-control {
        border-left: none;
    }

    /* Hiệu ứng focus đẹp hơn */
    .form-control:focus,
    .form-select:focus {
        border-color: #4f46e5;
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }
    .input-group:focus-within .input-group-text {
        border-color: #4f46e5;
        background-color: white;
        color: #4f46e5;
    }

    /* CSS cho bảng */
    .table thead {
        background-color: #f9fafb;
        color: #374151;
        text-transform: uppercase;
        font-size: 0.75rem; /* 12px */
        letter-spacing: 0.05em;
    }
    .table th {
        font-weight: 600;
    }
    .table tbody tr:hover {
        background-color: #f8f9fa;
    }
    .table td {
        vertical-align: middle;
    }

    /* CSS cho nút toggle biển số xe */
    .toggle-vehicles-btn {
        border: none;
        font-size: 0.75rem;
        font-weight: 600;
        cursor: pointer;
        padding: 4px 8px;
    }
    .toggle-vehicles-btn:hover {
        background-color: #eef2ff; /* Màu nền primary-subtle của bootstrap */
    }
</style>

<%-- GỘP 2 SCRIPT LẠI THÀNH 1 --%>
<script>
    // 1. Hàm Debounce (giữ nguyên)
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // 2. Hàm xây dựng URL và tải lại trang (giữ nguyên, đã có validation)
    function applyFilters() {
        // ... (Toàn bộ code hàm applyFilters của bạn giữ nguyên)
        const nameEl = document.getElementById('searchName');
        const plateEl = document.getElementById('searchLicensePlate');
        const emailEl = document.getElementById('searchEmail');
        const fromEl = document.getElementById('fromDate');
        const toEl = document.getElementById('toDate');
        const sortEl = document.getElementById('sortOrder');
        const fromErrorEl = document.getElementById('fromDateError');
        const toErrorEl = document.getElementById('toDateError');
        const name = nameEl.value;
        const plate = plateEl.value;
        const email = emailEl.value;
        const from = fromEl.value;
        const to = toEl.value;
        const sort = sortEl.value;
        let isValid = true;
        fromEl.classList.remove('is-invalid');
        toEl.classList.remove('is-invalid');
        if (fromErrorEl) fromErrorEl.textContent = '';
        if (toErrorEl) toErrorEl.textContent = '';
        if (from && to && new Date(from) > new Date(to)) {
            fromEl.classList.add('is-invalid');
            toEl.classList.add('is-invalid');
            if (fromErrorEl) fromErrorEl.textContent = 'Start date cannot be after end date.';
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        let url = '${pageContext.request.contextPath}/customerservice/search-customer?page=1';
        if (name) url += '&searchName=' + encodeURIComponent(name);
        if (plate) url += '&searchLicensePlate=' + encodeURIComponent(plate);
        if (email) url += '&searchEmail=' + encodeURIComponent(email);
        if (from) url += '&fromDate=' + encodeURIComponent(from);
        if (to) url += '&toDate=' + encodeURIComponent(to);
        if (sort) url += '&sortOrder=' + encodeURIComponent(sort);
        window.location.href = url;
    }

    // 3. Tạo hàm debounce (giữ nguyên)
    const debouncedApplyFilters = debounce(applyFilters, 500);

    // 4. Gán sự kiện khi DOM đã tải (ĐÃ SỬA)
    document.addEventListener("DOMContentLoaded", function() {

        const fromEl = document.getElementById('fromDate');
        const toEl = document.getElementById('toDate');

        // === BẮT ĐẦU LOGIC MỚI: Đặt min/max KHI TẢI TRANG ===
        // Logic này chạy mỗi khi trang tải xong,
        // nó đọc giá trị (value) đã được JSP điền vào
        // và áp dụng min/max cho ô còn lại.
        if (fromEl.value) {
            toEl.min = fromEl.value;
        }
        if (toEl.value) {
            fromEl.max = toEl.value;
        }
        // === KẾT THÚC LOGIC MỚI ===


        // Gán sự kiện cho Lọc tự động (Text inputs)
        document.getElementById('searchName').addEventListener('input', debouncedApplyFilters);
        document.getElementById('searchLicensePlate').addEventListener('input', debouncedApplyFilters);
        document.getElementById('searchEmail').addEventListener('input', debouncedApplyFilters);

        // Gán sự kiện cho Dropdown và Date (sự kiện 'change')
        document.getElementById('sortOrder').addEventListener('change', applyFilters);
        fromEl.addEventListener('change', applyFilters); // 'fromDate' vẫn gọi applyFilters
        toEl.addEventListener('change', applyFilters);   // 'toDate' vẫn gọi applyFilters

        // Gán sự kiện cho nút "Toggle Vehicles" (giữ nguyên)
        document.querySelectorAll('.toggle-vehicles-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const customerId = this.getAttribute('data-customer-id');
                const hiddenVehicles = document.querySelector('.hidden-vehicles-' + customerId);
                if (hiddenVehicles) {
                    const isHidden = hiddenVehicles.style.display === 'none';
                    hiddenVehicles.style.display = isHidden ? 'block' : 'none'; // Sửa thành 'block'
                    if (isHidden) {
                        this.innerHTML = '<i class="bi bi-chevron-up"></i> Hide';
                    } else {
                        const totalHidden = hiddenVehicles.querySelectorAll('.badge').length;
                        this.innerHTML = '+' + totalHidden + ' more';
                    }
                }
            });
        });

    });
</script>

</body>
</html>