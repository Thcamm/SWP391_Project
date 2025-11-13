<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Appointment List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <%-- Xóa file CSS cũ nếu không cần thiết --%>
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/appointment-list.css"> --%>
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

        /* Hiệu ứng focus */
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
        .form-label {
            font-weight: 600;
            font-size: 0.875rem; /* 14px */
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
            padding-top: 1rem;
            padding-bottom: 1rem;
        }
        /* Style cho hàng click được */
        .clickable-row {
            cursor: pointer;
        }
        .clickable-row:hover {
            background-color: #f8f9fa !important;
        }
        .table td {
            vertical-align: middle;
            word-wrap: break-word;
            white-space: normal;
        }

        /* CSS cho Status Badges */
        .badge {
            font-size: 0.8rem;
            padding: 0.4em 0.7em;
            font-weight: 600;
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

        /* CSS cho form-check */
        .form-check-input:checked {
            background-color: #4f46e5;
            border-color: #4f46e5;
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
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                             border: 1px solid #e5e7eb;
                             border-radius: 12px;
                             padding: 1.5rem 2.5rem 2.5rem 2.5rem;
                             min-height: calc(100vh - 64px - 1.25rem);
                             display: flex;
                             flex-direction: column;">

                    <div class="container py-4" style="padding: 0 !important;">
                        <jsp:include page="/view/customerservice/result.jsp"/>

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 style="margin: 0; font-weight: 700; color: #111827;">
                                    <i class="bi bi-calendar-check me-2"></i>Appointment List
                                </h2>
                                <p style="margin: 0.5rem 0 0 0; color: #6b7280; font-size: 14px;">Review and manage customer appointments</p>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="get" class="card p-4 mb-4"
                              style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="searchName" class="form-label">Customer Name</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-person"></i></span>
                                        <input type="text" id="searchName" name="searchName" value="${param.searchName}" class="form-control" placeholder="Enter customer name"/>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <label for="sortOrder" class="form-label"><i class="bi bi-sort-down me-1"></i> Sort</label>
                                    <select id="sortOrder" name="sortOrder" class="form-select">
                                        <option value="newest" <c:if test="${param.sortOrder eq 'newest'}">selected</c:if>>Newest</option>
                                        <option value="oldest" <c:if test="${param.sortOrder eq 'oldest'}">selected</c:if>>Oldest</option>
                                    </select>
                                </div>
                            </div>
                            <div class="row g-3 mt-2">
                                <div class="col-md-6">
                                    <label for="fromDate" class="form-label"><i class="bi bi-calendar-range me-1"></i> From Date</label>
                                    <input type="date" id="fromDate" name="fromDate" value="${param.fromDate}" class="form-control"/>
                                    <div id="fromDateError" class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="toDate" class="form-label"><i class="bi bi-calendar-range me-1"></i> To Date</label>
                                    <input type="date" id="toDate" name="toDate" value="${param.toDate}" class="form-control"/>
                                    <div id="toDateError" class="invalid-feedback"></div>
                                </div>
                            </div>

                            <div class="row g-3 mt-3">
                                <div class="col-md-8">
                                    <label class="form-label fw-bold">Status</label>
                                    <div class="d-flex flex-wrap gap-3">
                                        <c:set var="selectedStatuses" value="${fn:join(paramValues.status, ',')}" />
                                        <c:forEach var="s" items="${['pending','accepted','rejected','cancelled']}">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="status" value="${s}" id="status_${s}"
                                                       <c:if test="${fn:contains(selectedStatuses, s)}">checked</c:if>/>
                                                <label class="form-check-label text-capitalize" for="status_${s}">${s}</label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="col-md-4 d-flex align-items-end justify-content-end gap-2">
                                    <a href="${pageContext.request.contextPath}/customerservice/appointment-list" class="btn btn-secondary">
                                        <i class="bi bi-arrow-clockwise me-1"></i> Reset
                                    </a>
                                </div>
                            </div>
                        </form>

                        <div class="card" style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                            <div class="card-header d-flex justify-content-between align-items-center" style="background-color: transparent; border-bottom: 1px solid #e5e7eb;">
                                <h5 class="mb-0">List of Appointments</h5>
                                <span class="text-muted" style="font-size: 0.9rem;">
                                    Total: <strong>${appointmentList.totalItems}</strong> result(s)
                                </span>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0" style="table-layout: fixed;">
                                    <thead class="table-light">
                                    <tr>
                                        <%-- THAY ĐỔI CỘT: Cột Action cũ (20%) tách thành Cột SO (10%) --%>
                                        <th style="width: 10%;">No</th>
                                        <th style="width: 30%;">Customer</th>
                                        <th style="width: 20%;">Appointment Date</th>
                                        <th style="width: 20%;">Status</th>
                                        <th style="width: 10%;">Description</th>
                                        <th style="width: 10%;" class="text-center">SO</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:choose>
                                        <c:when test="${empty appointmentList.paginatedData}">
                                            <tr class="text-center text-muted">
                                                <td colspan="6" class="py-5">
                                                    <i class="bi bi-inbox-fill" style="font-size: 3rem; color: #e5e7eb;"></i>
                                                    <p class="mb-0 mt-2">No appointments found</p>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="row" items="${appointmentList.paginatedData}" varStatus="loop">
                                                <c:set var="apm" value="${row.appointment}" />

                                                <%-- HÀNG CLICKABLE --%>
                                                <tr class="clickable-row" data-id="${apm.appointmentID}">

                                                        <%-- CỘT 1: NO --%>
                                                    <td>${(appointmentList.currentPage - 1) * appointmentList.itemsPerPage + loop.index + 1}</td>

                                                        <%-- CỘT 2: CUSTOMER --%>
                                                    <td><strong class="text-dark">${row.customerName}</strong></td>

                                                        <%-- CỘT 3: DATE --%>
                                                    <td>${apm.appointmentDate}</td>

                                                        <%-- CỘT 4: STATUS (Giữ nguyên logic dropdown, nhưng thêm onclick stopPropagation) --%>
                                                    <td onclick="event.stopPropagation();">
                                                        <c:choose>
                                                            <c:when test="${apm.status eq 'PENDING'}">
                                                                <form action="${pageContext.request.contextPath}/customerservice/appointment-list" method="post" class="d-inline">
                                                                    <input type="hidden" name="appointmentID" value="${apm.appointmentID}">
                                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.contextPath}/customerservice/appointment-list?page=${appointmentList.currentPage}&searchName=${param.searchName}&fromDate=${param.fromDate}&toDate=${param.toDate}&sortOrder=${param.sortOrder}${statusQuery}" />
                                                                    <select name="status" class="form-select form-select-sm d-inline-block w-auto ms-1" onchange="this.form.submit()">
                                                                        <option value="PENDING" selected>PENDING</option>
                                                                        <option value="ACCEPTED">ACCEPTED</option>
                                                                        <option value="REJECTED">REJECTED</option>
                                                                    </select>
                                                                </form>
                                                            </c:when>
                                                            <c:when test="${apm.status eq 'ACCEPTED'}">
                                                                <span class="badge status-accepted">${apm.status}</span>
                                                            </c:when>
                                                            <c:when test="${apm.status eq 'REJECTED'}">
                                                                <span class="badge status-rejected">${apm.status}</span>
                                                            </c:when>
                                                            <c:when test="${apm.status eq 'CANCELLED'}">
                                                                <span class="badge status-cancelled">${apm.status}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">${apm.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                        <%-- CỘT 5: DESCRIPTION (Vẫn dùng popover, thêm onclick stopPropagation) --%>
                                                    <td class="text-center">
                                                        <button type="button" class="btn btn-sm btn-outline-secondary"
                                                                data-bs-toggle="popover" data-bs-html="false"
                                                                data-bs-content="${fn:escapeXml(apm.description)}"
                                                                title="Description" onclick="event.stopPropagation();">
                                                            <i class="bi bi-info-circle"></i>
                                                        </button>
                                                    </td>

                                                        <%-- CỘT 6: SO (Action riêng, thêm onclick stopPropagation) --%>
                                                            <td class="text-center" onclick="event.stopPropagation();">
                                                                <c:choose>
                                                                    <%-- Chỉ hiển thị nút active khi ACCEPTED VÀ chưa có ServiceRequest --%>
                                                                    <c:when test="${apm.status eq 'ACCEPTED' && !row.hasServiceRequest}">
                                                                        <a href="${pageContext.request.contextPath}/customerservice/createRequest?appointmentId=${apm.appointmentID}&customerId=${apm.customerID}"
                                                                           class="btn btn-sm btn-success" title="Create Service Order">
                                                                            <i class="bi bi-plus-circle"></i> SO
                                                                        </a>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <%-- Hiển thị nút disabled với tooltip phù hợp --%>
                                                                        <c:choose>
                                                                            <c:when test="${row.hasServiceRequest}">
                                                                                <button class="btn btn-sm btn-success" disabled
                                                                                        title="Service Order already created"
                                                                                        style="opacity: 0.5; cursor: not-allowed;">
                                                                                    <i class="bi bi-check-circle"></i> Created
                                                                                </button>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <button class="btn btn-sm btn-success" disabled
                                                                                        title="Only ACCEPTED appointments can create Service Order"
                                                                                        style="opacity: 0.5; cursor: not-allowed;">
                                                                                    <i class="bi bi-plus-circle"></i> SO
                                                                                </button>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <c:set var="statusQuery" value="" />
                        <c:if test="${not empty paramValues.status}">
                            <c:forEach var="s" items="${paramValues.status}">
                                <c:set var="statusQuery" value="${statusQuery}&status=${s}" />
                            </c:forEach>
                        </c:if>

                        <jsp:include page="/view/customerservice/pagination.jsp">
                            <jsp:param name="currentPage" value="${appointmentList.currentPage}" />
                            <jsp:param name="totalPages" value="${appointmentList.totalPages}" />
                            <jsp:param name="baseUrl" value="/customerservice/appointment-list" />
                            <jsp:param name="queryString"
                                       value="&searchName=${param.searchName}&fromDate=${param.fromDate}&toDate=${param.toDate}&sortOrder=${param.sortOrder}${statusQuery}" />
                        </jsp:include>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

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

    /* Hiệu ứng focus */
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
    .form-label {
        font-weight: 600;
        font-size: 0.875rem; /* 14px */
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
        padding-top: 1rem;
        padding-bottom: 1rem;
    }
    /* Style cho hàng click được */
    .clickable-row {
        cursor: pointer;
    }
    .clickable-row:hover {
        background-color: #f8f9fa !important;
    }
    .table td {
        vertical-align: middle;
        word-wrap: break-word;
        white-space: normal;
    }

    /* CSS cho Status Badges */
    .badge {
        font-size: 0.8rem;
        padding: 0.4em 0.7em;
        font-weight: 600;
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

    /* CSS cho form-check */
    .form-check-input:checked {
        background-color: #4f46e5;
        border-color: #4f46e5;
    }
</style>

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

    // 2. Hàm applyFilters (giữ nguyên)
    function applyFilters() {
        // Lấy *elements*
        const nameEl = document.getElementById('searchName');
        const fromEl = document.getElementById('fromDate');
        const toEl = document.getElementById('toDate');
        const sortEl = document.getElementById('sortOrder');
        const statusCheckboxes = document.querySelectorAll('input[name="status"]:checked');

        // Lấy *divs* báo lỗi
        const fromErrorEl = document.getElementById('fromDateError');
        const toErrorEl = document.getElementById('toDateError');

        // Lấy *values*
        const name = nameEl.value;
        const from = fromEl.value;
        const to = toEl.value;
        const sort = sortEl.value;

        // --- BẮT ĐẦU VALIDATION ---
        let isValid = true;

        // 1. Xóa lỗi cũ
        fromEl.classList.remove('is-invalid');
        toEl.classList.remove('is-invalid');
        if (fromErrorEl) fromErrorEl.textContent = '';
        if (toErrorEl) toErrorEl.textContent = '';

        // 2. Kiểm tra
        if (from && to && new Date(from) > new Date(to)) {
            fromEl.classList.add('is-invalid');
            toEl.classList.add('is-invalid');
            if (fromErrorEl) fromErrorEl.textContent = 'Start date cannot be after end date.';
            isValid = false;
        }

        // 3. Nếu không hợp lệ, dừng
        if (!isValid) {
            return;
        }
        // --- KẾT THÚC VALIDATION ---

        // Xử lý các checkbox status
        let statusQuery = '';
        statusCheckboxes.forEach(cb => {
            statusQuery += '&status=' + encodeURIComponent(cb.value);
        });

        // Xây dựng URL
        let url = '${pageContext.request.contextPath}/customerservice/appointment-list?page=1';
        if (name) url += '&searchName=' + encodeURIComponent(name);
        if (from) url += '&fromDate=' + encodeURIComponent(from);
        if (to) url += '&toDate=' + encodeURIComponent(to);
        if (sort) url += '&sortOrder=' + encodeURIComponent(sort);
        url += statusQuery;

        window.location.href = url;
    }

    // 3. Tạo hàm debounce (giữ nguyên)
    const debouncedApplyFilters = debounce(applyFilters, 500);

    // 4. Gán sự kiện (ĐÃ CẬP NHẬT)
    document.addEventListener("DOMContentLoaded", function() {

        const fromEl = document.getElementById('fromDate');
        const toEl = document.getElementById('toDate');

        // Code Popover (giữ nguyên)
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl, {
                trigger: 'focus',
                placement: 'right'
            });
        });

        // === LOGIC MỚI: CLICKABLE ROW ===
        document.querySelectorAll('.clickable-row').forEach(row => {
            row.addEventListener('click', function() {
                const appointmentId = this.getAttribute('data-id');
                if (appointmentId) {
                    // Chuyển hướng đến trang chi tiết
                    window.location.href = 'appointment-detail?id=' + appointmentId;
                }
            });
        });
        // === KẾT THÚC LOGIC CLICKABLE ROW ===

        // === LOGIC FILTER (Giữ nguyên) ===
        if (fromEl.value) {
            toEl.min = fromEl.value;
        }
        if (toEl.value) {
            fromEl.max = toEl.value;
        }

        // Gán sự kiện cho Lọc tự động
        const searchNameEl = document.getElementById('searchName');
        if (searchNameEl) {
            searchNameEl.addEventListener('input', debouncedApplyFilters);
        }

        // Gán sự kiện cho Dropdown, Date, Checkbox (dùng 'change')
        if (fromEl) {
            fromEl.addEventListener('change', applyFilters);
        }
        if (toEl) {
            toEl.addEventListener('change', applyFilters);
        }
        const sortOrderEl = document.getElementById('sortOrder');
        if (sortOrderEl) {
            sortOrderEl.addEventListener('change', applyFilters);
        }
        document.querySelectorAll('input[name="status"]').forEach(cb => {
            cb.addEventListener('change', applyFilters);
        });
    });
</script>
</body>
</html>