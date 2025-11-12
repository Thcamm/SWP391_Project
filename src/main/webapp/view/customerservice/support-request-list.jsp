<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
        /* CSS Tùy chỉnh (Giữ nguyên) */
        .form-control:focus,
        .form-select:focus {
            border-color: #4f46e5;
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
        }
        .form-label {
            font-weight: 600;
            font-size: 0.875rem;
        }
        .form-check-input:checked {
            background-color: #4f46e5;
            border-color: #4f46e5;
        }
        .table thead {
            background-color: #f9fafb;
            color: #374151;
            text-transform: uppercase;
            font-size: 0.75rem;
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
        .badge {
            font-size: 0.8rem !important; /* Kích thước 16px */
            padding: 0.4em 0.7em;
            font-weight: 580;
        }
        .status-resolved {
            background-color: rgba(6, 95, 70, 0.82);
            color: #ffffff;


        }
        .form-select.status-pending {
            border-color: #fcd34d;
            background-color: #fef9c3;
            color: #92400e;
            font-weight: 600;
        }
        .form-select.status-inprogress {
            border-color: #93c5fd;
            background-color: #dbeafe;
            color: #1e40af;
            font-weight: 600;
        }
    </style>
</head>

<body class="bg-light">

<jsp:include page="/view/customerservice/result.jsp" />
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
                             display: flex; flex-direction: column;">

                    <div class="container-fluid" style="padding: 0;">

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 style="margin: 0; font-size: 24px; font-weight: 700; color: #111827;">
                                    <i class="bi bi-headset me-2"></i>Support Request List
                                </h2>
                                <p style="margin: 0.5rem 0 0 0; color: #6b7280; font-size: 14px;">Review and manage customer support tickets</p>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/customerservice/view-support-request" method="get" class="card p-4 mb-4"
                              style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="categoryId" class="form-label"><i class="bi bi-tag me-1"></i>Category</label>
                                    <select id="categoryId" name="categoryId" class="form-select">
                                        <option value="">All Categories</option>
                                        <c:forEach var="cat" items="${categories}">
                                            <option value="${cat.categoryId}" ${param.categoryId == cat.categoryId ? 'selected' : ''}>
                                                    ${cat.categoryName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="sortOrder" class="form-label"><i class="bi bi-sort-down me-1"></i>Sort</label>
                                    <select id="sortOrder" name="sortOrder" class="form-select">
                                        <option value="newest" ${param.sortOrder == 'newest' ? 'selected' : ''}>Newest</option>
                                        <option value="oldest" ${param.sortOrder == 'oldest' ? 'selected' : ''}>Oldest</option>
                                    </select>
                                </div>
                            </div>

                            <div class="row g-3 mt-2">
                                <div class="col-md-6">
                                    <label for="fromDate" class="form-label"><i class="bi bi-calendar-range me-1"></i>From Date</label>
                                    <input type="date" id="fromDate" name="fromDate" value="${param.fromDate}" class="form-control" />
                                    <div id="fromDateError" class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="toDate" class="form-label"><i class="bi bi-calendar-range me-1"></i>To Date</label>
                                    <input type="date" id="toDate" name="toDate" value="${param.toDate}" class="form-control" />
                                    <div id="toDateError" class="invalid-feedback"></div>
                                </div>
                            </div>


                            <div class="row g-3 mt-3">
                                <div class="col-md-8">
                                    <label class="form-label fw-bold">Status</label>
                                    <div class="d-flex flex-wrap gap-3">
                                        <c:forEach var="s" items="${statuses}">
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox" name="statuses"
                                                       value="${fn:toUpperCase(s)}" id="status_${s}"
                                                       <c:if test="${fn:contains(fn:join(paramValues.statuses, ','), fn:toUpperCase(s))}">checked</c:if> />
                                                <label class="form-check-label" for="status_${s}">${fn:toUpperCase(s)}</label>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="col-md-4 d-flex align-items-end justify-content-end gap-2">
                                    <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="btn btn-secondary">
                                        <i class="bi bi-arrow-clockwise me-1"></i>Reset
                                    </a>
                                </div>
                            </div>
                        </form>

                        <div class="card" style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                            <div class="card-header d-flex justify-content-between align-items-center" style="background-color: transparent; border-bottom: 1px solid #e5e7eb;">
                                <h5 class="mb-0">List of Support Requests</h5>
                                <span class="text-muted" style="font-size: 0.9rem;">
                                    Total: <strong>${supportrequestList.totalItems}</strong> result(s)
                                </span>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0" style="table-layout: fixed;">
                                    <thead class="table-light">
                                    <tr>
                                        <th style="width: 5%;">No</th>
                                        <th style="width: 25%;">Category</th>
                                        <th style="width: 20%;">Status</th>
                                        <th style="width: 15%;">Created At</th>
                                        <th style="width: 15%;">Updated At</th>
                                        <th style="width: 20%;" class="text-center">Reply</th> <%-- Đổi tên cột Action thành Reply --%>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    <c:choose>
                                        <c:when test="${empty supportrequestList.paginatedData}">
                                            <tr class="text-center text-muted">
                                                <td colspan="6" class="py-5">
                                                    <i class="bi bi-inbox-fill" style="font-size: 3rem; color: #e5e7eb;"></i>
                                                    <p class="mb-0 mt-2">No request found</p>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="sr" items="${supportrequestList.paginatedData}" varStatus="loop">
                                                <%-- HÀNG CLICKABLE --%>
                                                <tr class="clickable-row" data-id="${sr.requestId}">

                                                    <td>${(supportrequestList.currentPage - 1) * supportrequestList.itemsPerPage + loop.index + 1}</td>
                                                    <td>
                                                        <span class="badge bg-secondary bg-opacity-10 text-dark-emphasis" style="font-size: 0.9rem !important">${categoryMap[sr.categoryId]}</span>
                                                    </td>

                                                        <%-- CỘT STATUS (Có Dropdown) --%>
                                                    <td onclick="event.stopPropagation();">
                                                        <c:choose>
                                                            <c:when test="${sr.status == 'RESOLVED'}">
                                                                <span class="badge status-resolved">RESOLVED</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form action="${pageContext.request.contextPath}/customerservice/view-support-request"
                                                                      method="post" class="d-inline">
                                                                    <input type="hidden" name="requestId" value="${sr.requestId}">

                                                                    <c:set var="currentUri" value="${pageContext.request.contextPath}/customerservice/view-support-request" />
                                                                    <c:set var="currentQuery" value="${pageContext.request.queryString}" />
                                                                    <c:choose>
                                                                        <c:when test="${not empty currentQuery}">
                                                                            <c:set var="fullRedirectUrl" value="${currentUri}?${currentQuery}" />
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <c:set var="fullRedirectUrl" value="${currentUri}" />
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <input type="hidden" name="redirectUrl" value="${fullRedirectUrl}">

                                                                    <select name="status" class="form-select form-select-sm d-inline-block w-auto
                                                                        ${sr.status == 'PENDING' ? 'status-pending' : ''}
                                                                        ${sr.status == 'INPROGRESS' ? 'status-inprogress' : ''}"
                                                                            onchange="this.form.submit()">
                                                                        <option value="PENDING" <c:if test="${sr.status == 'PENDING'}">selected</c:if>>PENDING</option>
                                                                        <option value="INPROGRESS" <c:if test="${sr.status == 'INPROGRESS'}">selected</c:if>>INPROGRESS</option>
                                                                        <option value="RESOLVED">RESOLVED</option>
                                                                    </select>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <td>${sr.createdAt}</td>
                                                    <td>${sr.updatedAt}</td>

                                                        <%-- CỘT REPLY (Action riêng) --%>
                                                    <td class="text-center" onclick="event.stopPropagation();">
                                                        <c:choose>
                                                            <c:when test="${sr.status == 'INPROGRESS'}">
                                                                <a href="${pageContext.request.contextPath}/customerservice/reply-request?id=${sr.requestId}&email=${customerEmailMap[sr.customerId]}"
                                                                   class="btn btn-sm btn-outline-success" title="Reply">
                                                                    <i class="bi bi-reply"></i> Reply
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="btn btn-sm btn-outline-success disabled"
                                                                   style="opacity: 0.5; pointer-events: none;" title="Reply (Disabled)">
                                                                    <i class="bi bi-reply"></i> Reply
                                                                </a>
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
                        <%-- Sửa 1: Dùng paramValues.statuses --%>
                        <c:if test="${not empty paramValues.statuses}">
                            <c:forEach var="s" items="${paramValues.statuses}">
                                <%-- Sửa 2: Dùng &amp;statuses= --%>
                                <c:set var="statusQuery" value="${statusQuery}&amp;statuses=${s}" />
                            </c:forEach>
                        </c:if>

                        <jsp:include page="/view/customerservice/pagination.jsp">
                            <jsp:param name="currentPage" value="${supportrequestList.currentPage}" />
                            <jsp:param name="totalPages" value="${supportrequestList.totalPages}" />
                            <jsp:param name="baseUrl" value="/customerservice/view-support-request" />
                            <jsp:param name="queryString" value="&amp;categoryId=${param.categoryId}&amp;fromDate=${param.fromDate}&amp;toDate=${param.toDate}&amp;sortOrder=${param.sortOrder}${statusQuery}" />
                        </jsp:include>

                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // 1. Hàm Debounce (Không dùng ở đây nhưng để lại cho nhất quán)
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

    // 2. Hàm applyFilters (ĐÃ SỬA: Thêm validation)
    function applyFilters() {
        // Lấy elements
        const categoryEl = document.getElementById('categoryId');
        const fromEl = document.getElementById('fromDate');
        const toEl = document.getElementById('toDate');
        const sortEl = document.getElementById('sortOrder');
        const statusCheckboxes = document.querySelectorAll('input[name="statuses"]:checked');

        // Lấy div báo lỗi
        const fromErrorEl = document.getElementById('fromDateError');
        const toErrorEl = document.getElementById('toDateError');

        // Lấy values
        const category = categoryEl.value;
        const from = fromEl.value;
        const to = toEl.value;
        const sort = sortEl.value;

        // --- BẮT ĐẦU VALIDATION ---
        let isValid = true;
        fromEl.classList.remove('is-invalid');
        toEl.classList.remove('is-invalid');
        if(fromErrorEl) fromErrorEl.textContent = '';
        if(toErrorEl) toErrorEl.textContent = '';

        if (from && to && new Date(from) > new Date(to)) {
            fromEl.classList.add('is-invalid');
            toEl.classList.add('is-invalid');
            if (fromErrorEl) fromErrorEl.textContent = 'Start date cannot be after end date.';
            isValid = false;
        }

        if (!isValid) {
            return; // Dừng nếu không hợp lệ
        }
        // --- KẾT THÚC VALIDATION ---

        // Xử lý các checkbox status
        let statusQuery = '';
        statusCheckboxes.forEach(cb => {
            statusQuery += '&statuses=' + encodeURIComponent(cb.value);
        });

        // Xây dựng URL
        let url = '${pageContext.request.contextPath}/customerservice/view-support-request?page=1';
        if (category) url += '&categoryId=' + encodeURIComponent(category);
        if (from) url += '&fromDate=' + encodeURIComponent(from);
        if (to) url += '&toDate=' + encodeURIComponent(to);
        if (sort) url += '&sortOrder=' + encodeURIComponent(sort);
        url += statusQuery;

        window.location.href = url;
    }

    // 3. Gán sự kiện (ĐÃ SỬA: Thêm logic min/max và Clickable Row)
    document.addEventListener("DOMContentLoaded", function() {

        const categoryEl = document.getElementById('categoryId');
        const fromDateEl = document.getElementById('fromDate');
        const toDateEl = document.getElementById('toDate');
        const sortOrderEl = document.getElementById('sortOrder');

        // === LOGIC CLICKABLE ROW ===
        document.querySelectorAll('.clickable-row').forEach(row => {
            row.addEventListener('click', function() {
                const requestId = this.getAttribute('data-id');
                if (requestId) {
                    // Chuyển hướng đến trang chi tiết yêu cầu hỗ trợ
                    window.location.href = '${pageContext.request.contextPath}/customerservice/view-support-request?id=' + requestId;
                }
            });
        });
        // === KẾT THÚC LOGIC CLICKABLE ROW ===

        // === LOGIC FILTER VÀ VALIDATION (Giữ nguyên) ===
        if (fromDateEl.value) {
            toDateEl.min = fromDateEl.value;
        }
        if (toDateEl.value) {
            fromDateEl.max = toDateEl.value;
        }

        // Gán sự kiện cho Lọc tự động
        if (categoryEl) {
            categoryEl.addEventListener('change', applyFilters);
        }
        if (fromDateEl) {
            fromDateEl.addEventListener('change', applyFilters);
        }
        if (toDateEl) {
            toDateEl.addEventListener('change', applyFilters);
        }
        if (sortOrderEl) {
            sortOrderEl.addEventListener('change', applyFilters);
        }

        // Lắng nghe tất cả các checkbox status
        document.querySelectorAll('input[name="statuses"]').forEach(cb => {
            cb.addEventListener('change', applyFilters);
        });
    });
</script>

</body>
</html>