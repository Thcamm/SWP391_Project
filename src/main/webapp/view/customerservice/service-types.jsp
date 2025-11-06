<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- Sidebar Column -->
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card" style="background: white; border: 1px solid #e5e7eb; border-radius: 12px; padding: 1.5rem; min-height: calc(100vh - 64px - 1.25rem);">

                    <!-- Header with Add Button -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 style="margin: 0; font-size: 24px; font-weight: 700; color: #111827;">Service Types Management</h2>
                            <p style="margin: 0.5rem 0 0 0; color: #6b7280; font-size: 14px;">Manage vehicle service types and pricing</p>
                        </div>
                        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal" style="border-radius: 10px;">
                            <i class="bi bi-plus-circle me-2"></i>Add Service Type
                        </button>
                    </div>

                    <!-- Success/Error Messages -->
                    <c:if test="${not empty sessionScope.message}">
                        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert" style="border-radius: 10px;">
                            <i class="bi ${sessionScope.messageType == 'success' ? 'bi-check-circle' : 'bi-exclamation-triangle'} me-2"></i>
                                ${sessionScope.message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="message" scope="session"/>
                        <c:remove var="messageType" scope="session"/>
                    </c:if>

                    <!-- Search and Filter Bar -->
                    <div class="row g-3 mb-4">
                        <div class="col-md-6">
                            <div class="input-group" style="border-radius: 10px;">
                                <span class="input-group-text" style="background-color: #f9fafb; border: 1px solid #e5e7eb; border-right: none; border-radius: 10px 0 0 10px;">
                                    <i class="bi bi-search" style="color: #6b7280;"></i>
                                </span>
                                <input type="text"
                                       id="searchInput"
                                       class="form-control"
                                       placeholder="Search by service name or category..."
                                       value="${param.search}"
                                       style="border: 1px solid #e5e7eb; border-left: none; border-radius: 0 10px 10px 0; padding: 0.625rem 0.75rem; font-size: 14px;">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <select id="categoryFilter" class="form-select" style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;">
                                <option value="">All Categories</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select id="sortBy" class="form-select" style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;">
                                <option value="name_asc" ${param.sort == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                <option value="name_desc" ${param.sort == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                <option value="price_asc" ${param.sort == 'price_asc' ? 'selected' : ''}>Price (Low to High)</option>
                                <option value="price_desc" ${param.sort == 'price_desc' ? 'selected' : ''}>Price (High to Low)</option>
                            </select>
                        </div>
                    </div>

                    <!-- Results Info -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div style="color: #6b7280; font-size: 14px;">
                            <c:choose>
                                <c:when test="${not empty param.search or not empty param.category}">
                                    Showing ${not empty services ? services.size() : 0} result(s)
                                    <c:if test="${not empty param.search}">
                                        for "<strong>${param.search}</strong>"
                                    </c:if>
                                    <c:if test="${not empty param.category}">
                                        in <strong>${param.category}</strong>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/customerservice/service-types" class="ms-2" style="font-size: 13px;">Clear filters</a>
                                </c:when>
                                <c:otherwise>
                                    Showing <strong>${((currentPage - 1) * pageSize) + 1}</strong> to <strong>${(currentPage * pageSize) > totalServices ? totalServices : (currentPage * pageSize)}</strong> of <strong>${totalServices}</strong> service types
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div style="color: #6b7280; font-size: 14px;">
                            <label for="pageSizeSelect" class="me-2">Items per page:</label>
                            <select id="pageSizeSelect" class="form-select form-select-sm d-inline-block" style="width: auto; border-radius: 8px;">
                                <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                <option value="100" ${pageSize == 100 ? 'selected' : ''}>100</option>
                            </select>
                        </div>
                    </div>

                    <!-- Service Types Table -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead style="background-color: #f9fafb;">
                            <tr>
                                <th style="padding: 12px; color: #6b7280; font-weight: 600; font-size: 14px;">ID</th>
                                <th style="padding: 12px; color: #6b7280; font-weight: 600; font-size: 14px;">Service Name</th>
                                <th style="padding: 12px; color: #6b7280; font-weight: 600; font-size: 14px;">Category</th>
                                <th style="padding: 12px; color: #6b7280; font-weight: 600; font-size: 14px;">Price</th>
                                <th style="padding: 12px; color: #6b7280; font-weight: 600; text-align: center; font-size: 14px;">Actions</th>
                            </tr>
                            </thead>
                            <tbody id="servicesTableBody">
                            <c:choose>
                                <c:when test="${empty services}">
                                    <tr>
                                        <td colspan="5" class="text-center py-5" style="color: #6b7280;">
                                            <i class="bi bi-inbox" style="font-size: 48px; opacity: 0.3;"></i>
                                            <p class="mt-3 mb-0" style="font-size: 14px;">
                                                <c:choose>
                                                    <c:when test="${not empty param.search or not empty param.category}">
                                                        No service types match your search criteria
                                                    </c:when>
                                                    <c:otherwise>
                                                        No service types found
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                            <c:if test="${empty param.search and empty param.category}">
                                                <p class="mb-0" style="font-size: 12px; color: #9ca3af;">Click "Add Service Type" to create your first service</p>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="service" items="${services}">
                                        <tr style="border-bottom: 1px solid #e5e7eb;">
                                            <td style="padding: 12px; font-size: 14px; color: #6b7280;">#${service.serviceTypeID}</td>
                                            <td style="padding: 12px;">
                                                <strong style="color: #111827; font-size: 14px;">${service.serviceName}</strong>
                                            </td>
                                            <td style="padding: 12px;">
                                                <span class="badge" style="background-color: #eef2ff; color: #4f46e5; border-radius: 999px; padding: 4px 12px; font-size: 12px; font-weight: 500;">
                                                        ${service.category}
                                                </span>
                                            </td>
                                            <td style="padding: 12px;">
                                                <strong style="color: #059669; font-size: 14px;">
                                                    <fmt:formatNumber value="${service.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </strong>
                                            </td>
                                            <td style="padding: 12px; text-align: center;">
                                                <a href="${pageContext.request.contextPath}/customerservice/service-types?action=edit&id=${service.serviceTypeID}"
                                                   class="btn btn-sm btn-outline-primary me-1"
                                                   style="border-radius: 8px; padding: 0.375rem 0.75rem;"
                                                   title="Edit">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button class="btn btn-sm btn-outline-danger"
                                                        onclick="confirmDelete(${service.serviceTypeID}, '${service.serviceName}')"
                                                        style="border-radius: 8px; padding: 0.375rem 0.75rem;"
                                                        title="Delete">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Service types pagination" class="mt-4">
                            <ul class="pagination justify-content-center" style="margin: 0;">
                                <!-- First Page -->
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=1${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}&pageSize=${pageSize}"
                                       style="border-radius: 8px 0 0 8px; border: 1px solid #e5e7eb; color: #6b7280; padding: 0.5rem 0.75rem;">
                                        <i class="bi bi-chevron-double-left"></i>
                                    </a>
                                </li>

                                <!-- Previous Page -->
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage - 1}${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}&pageSize=${pageSize}"
                                       style="border: 1px solid #e5e7eb; color: #6b7280; padding: 0.5rem 0.75rem;">
                                        <i class="bi bi-chevron-left"></i>
                                    </a>
                                </li>

                                <!-- Page Numbers -->
                                <c:forEach var="i" begin="${currentPage - 2 > 0 ? currentPage - 2 : 1}"
                                           end="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}&pageSize=${pageSize}"
                                           style="border: 1px solid ${i == currentPage ? '#4f46e5' : '#e5e7eb'};
                                                   background-color: ${i == currentPage ? '#4f46e5' : 'white'};
                                                   color: ${i == currentPage ? 'white' : '#6b7280'};
                                                   padding: 0.5rem 0.75rem;">
                                                ${i}
                                        </a>
                                    </li>
                                </c:forEach>

                                <!-- Next Page -->
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage + 1}${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}&pageSize=${pageSize}"
                                       style="border: 1px solid #e5e7eb; color: #6b7280; padding: 0.5rem 0.75rem;">
                                        <i class="bi bi-chevron-right"></i>
                                    </a>
                                </li>

                                <!-- Last Page -->
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${totalPages}${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}&pageSize=${pageSize}"
                                       style="border-radius: 0 8px 8px 0; border: 1px solid #e5e7eb; color: #6b7280; padding: 0.5rem 0.75rem;">
                                        <i class="bi bi-chevron-double-right"></i>
                                    </a>
                                </li>
                            </ul>
                        </nav>

                        <!-- Page Info -->
                        <div class="text-center mt-3" style="color: #6b7280; font-size: 13px;">
                            Page ${currentPage} of ${totalPages}
                        </div>
                    </c:if>

                </div>
            </main>
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>

<!-- Add Modal -->
<div class="modal fade" id="addModal" tabindex="-1" aria-labelledby="addModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius: 12px; border: 1px solid #e5e7eb;">
            <form action="${pageContext.request.contextPath}/customerservice/service-types" method="post">
                <input type="hidden" name="action" value="add">

                <div class="modal-header" style="border-bottom: 1px solid #e5e7eb; padding: 1.25rem 1.5rem;">
                    <h5 class="modal-title" id="addModalLabel" style="font-weight: 600; color: #111827; font-size: 18px;">
                        <i class="bi bi-plus-circle me-2"></i>Add New Service Type
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body" style="padding: 1.5rem;">
                    <div class="mb-3">
                        <label for="serviceName" class="form-label" style="font-weight: 600; color: #111827; font-size: 14px;">
                            Service Name <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" id="serviceName" name="serviceName" required
                               style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;"
                               placeholder="e.g., Oil Change">
                    </div>

                    <div class="mb-3">
                        <label for="category" class="form-label" style="font-weight: 600; color: #111827; font-size: 14px;">
                            Category <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" id="category" name="category" required
                               style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;"
                               placeholder="e.g., Maintenance">
                    </div>

                    <div class="mb-3">
                        <label for="price" class="form-label" style="font-weight: 600; color: #111827; font-size: 14px;">
                            Price (VND) <span class="text-danger">*</span>
                        </label>
                        <input type="number" class="form-control" id="price" name="price" required step="1000" min="0"
                               style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;"
                               placeholder="500000">
                    </div>
                </div>

                <div class="modal-footer" style="border-top: 1px solid #e5e7eb; padding: 1rem 1.5rem;">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
                            style="border-radius: 10px; padding: 0.5rem 1rem; font-size: 14px;">
                        Cancel
                    </button>
                    <button type="submit" class="btn btn-primary"
                            style="border-radius: 10px; padding: 0.5rem 1rem; font-size: 14px;">
                        <i class="bi bi-check-circle me-2"></i>Add Service Type
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Modal -->
<c:if test="${not empty editService}">
    <div class="modal fade show" id="editModal" tabindex="-1" style="display: block; background-color: rgba(0,0,0,0.5);" aria-modal="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="border-radius: 12px; border: 1px solid #e5e7eb;">
                <form action="${pageContext.request.contextPath}/customerservice/service-types" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="serviceTypeId" value="${editService.serviceTypeID}">

                    <div class="modal-header" style="border-bottom: 1px solid #e5e7eb; padding: 1.25rem 1.5rem;">
                        <h5 class="modal-title" style="font-weight: 600; color: #111827; font-size: 18px;">
                            <i class="bi bi-pencil me-2"></i>Edit Service Type
                        </h5>
                        <a href="${pageContext.request.contextPath}/customerservice/service-types" class="btn-close" aria-label="Close"></a>
                    </div>

                    <div class="modal-body" style="padding: 1.5rem;">
                        <div class="mb-3">
                            <label for="editServiceName" class="form-label" style="font-weight: 600; color: #111827; font-size: 14px;">
                                Service Name <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="editServiceName" name="serviceName"
                                   value="${editService.serviceName}" required
                                   style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;">
                        </div>

                        <div class="mb-3">
                            <label for="editCategory" class="form-label" style="font-weight: 600; color: #111827; font-size: 14px;">
                                Category <span class="text-danger">*</span>
                            </label>
                            <input type="text" class="form-control" id="editCategory" name="category"
                                   value="${editService.category}" required
                                   style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;">
                        </div>

                        <div class="mb-3">
                            <label for="editPrice" class="form-label" style="font-weight: 600; color: #111827; font-size: 14px;">
                                Price (VND) <span class="text-danger">*</span>
                            </label>
                            <input type="number" class="form-control" id="editPrice" name="price"
                                   value="${editService.price}" required step="1000" min="0"
                                   style="border-radius: 10px; border: 1px solid #e5e7eb; padding: 0.625rem 0.75rem; font-size: 14px;">
                        </div>
                    </div>

                    <div class="modal-footer" style="border-top: 1px solid #e5e7eb; padding: 1rem 1.5rem;">
                        <a href="${pageContext.request.contextPath}/customerservice/service-types"
                           class="btn btn-secondary"
                           style="border-radius: 10px; padding: 0.5rem 1rem; font-size: 14px;">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-primary"
                                style="border-radius: 10px; padding: 0.5rem 1rem; font-size: 14px;">
                            <i class="bi bi-check-circle me-2"></i>Update Service Type
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<style>
    /* Table hover effect */
    .table tbody tr:hover {
        background-color: #f9fafb;
        transition: background-color 0.2s;
    }

    /* Button hover effects */
    .btn-outline-primary {
        border-color: #4f46e5;
        color: #4f46e5;
    }
    .btn-outline-primary:hover {
        background-color: #4f46e5;
        border-color: #4f46e5;
        color: white;
    }

    .btn-outline-danger {
        border-color: #ef4444;
        color: #ef4444;
    }
    .btn-outline-danger:hover {
        background-color: #ef4444;
        border-color: #ef4444;
        color: white;
    }

    /* Form input focus */
    .form-control:focus,
    .form-select:focus {
        border-color: #4f46e5;
        box-shadow: 0 0 0 0.2rem rgba(79, 70, 229, 0.1);
    }

    /* Modal backdrop */
    .modal.show {
        display: block;
    }

    /* Alert styles */
    .alert {
        font-size: 14px;
    }

    .alert-success {
        background-color: #d1fae5;
        border-color: #a7f3d0;
        color: #065f46;
    }

    .alert-danger {
        background-color: #fee2e2;
        border-color: #fecaca;
        color: #991b1b;
    }

    /* Pagination styles */
    .pagination .page-link {
        transition: all 0.2s;
    }

    .pagination .page-link:hover:not(.disabled) {
        background-color: #f9fafb;
        border-color: #4f46e5;
        color: #4f46e5;
    }

    .pagination .page-item.active .page-link {
        background-color: #4f46e5;
        border-color: #4f46e5;
    }

    .pagination .page-item.disabled .page-link {
        opacity: 0.5;
        cursor: not-allowed;
    }

    /* Search input group */
    .input-group:focus-within .input-group-text {
        border-color: #4f46e5;
    }

    .input-group:focus-within .form-control {
        border-color: #4f46e5;
    }
</style>

<script>
    // Debounce function for search
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

    // Search functionality
    const searchInput = document.getElementById('searchInput');
    const categoryFilter = document.getElementById('categoryFilter');
    const sortBy = document.getElementById('sortBy');
    const pageSizeSelect = document.getElementById('pageSizeSelect');

    // Handle search with debounce
    const handleSearch = debounce(function() {
        applyFilters();
    }, 500);

    if (searchInput) {
        searchInput.addEventListener('input', handleSearch);
    }

    // Handle category filter
    if (categoryFilter) {
        categoryFilter.addEventListener('change', function() {
            applyFilters();
        });
    }

    // Handle sort
    if (sortBy) {
        sortBy.addEventListener('change', function() {
            applyFilters();
        });
    }

    // Handle page size change
    if (pageSizeSelect) {
        pageSizeSelect.addEventListener('change', function() {
            applyFilters();
        });
    }

    // Apply all filters
    function applyFilters() {
        const search = searchInput ? searchInput.value.trim() : '';
        const category = categoryFilter ? categoryFilter.value : '';
        const sort = sortBy ? sortBy.value : '';
        const pageSize = pageSizeSelect ? pageSizeSelect.value : '10';

        let url = '${pageContext.request.contextPath}/customerservice/service-types?page=1';

        if (search) {
            url += '&search=' + encodeURIComponent(search);
        }
        if (category) {
            url += '&category=' + encodeURIComponent(category);
        }
        if (sort) {
            url += '&sort=' + encodeURIComponent(sort);
        }
        if (pageSize) {
            url += '&pageSize=' + encodeURIComponent(pageSize);
        }

        window.location.href = url;
    }

    // Auto show edit modal if editService exists
    document.addEventListener('DOMContentLoaded', function() {
        <c:if test="${not empty editService}">
        document.body.classList.add('modal-open');
        </c:if>
    });

    // Confirm delete with better styling
    function confirmDelete(id, name) {
        const message = 'Are you sure you want to delete "' + name + '"?\n\nThis action cannot be undone.';
        if (confirm(message)) {
            const urlParams = new URLSearchParams(window.location.search);
            let url = '${pageContext.request.contextPath}/customerservice/service-types?action=delete&id=' + id;

            // Preserve current filters
            const search = urlParams.get('search');
            const category = urlParams.get('category');
            const sort = urlParams.get('sort');
            const page = urlParams.get('page');
            const pageSize = urlParams.get('pageSize');

            if (search) url += '&search=' + encodeURIComponent(search);
            if (category) url += '&category=' + encodeURIComponent(category);
            if (sort) url += '&sort=' + encodeURIComponent(sort);
            if (page) url += '&page=' + page;
            if (pageSize) url += '&pageSize=' + pageSize;

            window.location.href = url;
        }
    }

    // Auto dismiss alerts after 5 seconds
    document.addEventListener('DOMContentLoaded', function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            setTimeout(function() {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        });
    });
</script>

