<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Bootstrap 5 & FontAwesome -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="header.jsp"/>

<div class="container-fluid px-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-auto" style="flex:0 0 280px; width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content -->
        <div class="col" style="min-width:0;">
            <main class="p-3 pb-0">
                <!-- Page Header -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex align-items-center justify-content-between">
                        <div>
                            <h2 class="h4 mb-1">Inventory Management</h2>
                            <p class="text-muted mb-0">
                                Manage your warehouse inventory
                            </p>
                        </div>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/inventory?action=add"
                               class="btn btn-primary">
                                <i class="fas fa-plus"></i> Add New Part
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Alert Messages -->
                <c:if test="${not empty param.message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${param.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${param.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Statistics Cards -->
                <div class="row g-3 mb-3">
                    <div class="col-12 col-sm-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center justify-content-between">
                                    <div>
                                        <div class="text-muted small">Total Items</div>
                                        <div class="h3 fw-semibold mb-0">${totalItems != null ? totalItems : 0}</div>
                                    </div>
                                    <div class="fs-1 text-primary opacity-25">
                                        <i class="fas fa-boxes"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center justify-content-between">
                                    <div>
                                        <div class="text-muted small">Low Stock Items</div>
                                        <div class="h3 fw-semibold mb-0 text-warning">${lowStockCount != null ? lowStockCount : 0}</div>
                                    </div>
                                    <div class="fs-1 text-warning opacity-25">
                                        <i class="fas fa-exclamation-triangle"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="d-flex align-items-center justify-content-between">
                                    <div>
                                        <div class="text-muted small">Total Value</div>
                                        <div class="h3 fw-semibold mb-0">
                                            <fmt:formatNumber value="${totalValue != null ? totalValue : 0}"
                                                            type="number" maxFractionDigits="0"/>
                                        </div>
                                        <small class="text-muted">VND</small>
                                    </div>
                                    <div class="fs-1 text-success opacity-25">
                                        <i class="fas fa-dollar-sign"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Search & Filter Card -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-0">
                        <h5 class="mb-0">
                            <i class="fas fa-search"></i> Search & Filter
                        </h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/inventory" method="get">
                            <input type="hidden" name="action" value="search">

                            <div class="row g-3">
                                <div class="col-md-3">
                                    <label class="form-label">Keyword</label>
                                    <input type="text" name="keyword" class="form-control"
                                           placeholder="Name or SKU..." value="${param.keyword}">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Category</label>
                                    <input type="text" name="category" class="form-control"
                                           placeholder="Category..." value="${param.category}">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Location</label>
                                    <input type="text" name="location" class="form-control"
                                           placeholder="Location..." value="${param.location}">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Status</label>
                                    <select name="stockStatus" class="form-select">
                                        <option value="">All</option>
                                        <option value="normal" ${param.stockStatus == 'normal' ? 'selected' : ''}>In Stock</option>
                                        <option value="low" ${param.stockStatus == 'low' ? 'selected' : ''}>Low Stock</option>
                                        <option value="out" ${param.stockStatus == 'out' ? 'selected' : ''}>Out of Stock</option>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Manufacturer</label>
                                    <input type="text" name="manufacturer" class="form-control"
                                           placeholder="Manufacturer..." value="${param.manufacturer}">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Price From</label>
                                    <input type="number" name="priceFrom" class="form-control"
                                           placeholder="Min price" value="${param.priceFrom}">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Price To</label>
                                    <input type="number" name="priceTo" class="form-control"
                                           placeholder="Max price" value="${param.priceTo}">
                                </div>
                                <div class="col-md-3 d-flex align-items-end">
                                    <button type="submit" class="btn btn-primary w-100">
                                        <i class="fas fa-search"></i> Search
                                    </button>
                                </div>
                                <div class="col-12">
                                    <a href="${pageContext.request.contextPath}/inventory?action=list"
                                       class="btn btn-outline-secondary">
                                        <i class="fas fa-redo"></i> Reset
                                    </a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Category Filter -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body">
                        <div class="d-flex flex-wrap gap-2 align-items-center">
                            <strong class="me-2">Categories:</strong>
                            <a href="${pageContext.request.contextPath}/inventory?action=list"
                               class="btn btn-sm ${empty param.category ? 'btn-primary' : 'btn-outline-primary'}">
                                All
                            </a>
                            <c:forEach var="cat" items="${allCategoriesList}">
                                <a href="${pageContext.request.contextPath}/inventory?action=list&category=${cat}"
                                   class="btn btn-sm ${param.category == cat ? 'btn-primary' : 'btn-outline-primary'}">
                                    ${cat}
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <!-- Data Table -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>SKU</th>
                                        <th>Part Name</th>
                                        <th>Category</th>
                                        <th>Manufacturer</th>
                                        <th>Quantity</th>
                                        <th>Location</th>
                                        <th>Unit Price</th>
                                        <th>Status</th>
                                        <th class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty inventoryList}">
                                            <tr>
                                                <td colspan="9" class="text-center py-5 text-muted">
                                                    <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                                    No data found.
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="item" items="${inventoryList}">
                                                <tr>
                                                    <td><code>${item.sku}</code></td>
                                                    <td>
                                                        <strong>${item.partName}</strong><br/>
                                                        <small class="text-muted">${item.partCode}</small>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-light text-dark">${item.category}</span>
                                                    </td>
                                                    <td>${empty item.manufacturer ? 'N/A' : item.manufacturer}</td>
                                                    <td>
                                                        <strong class="${item.quantity <= item.minStock ? 'text-danger' : ''}">${item.quantity}</strong>
                                                        ${item.unitName}
                                                        <br/><small class="text-muted">Min: ${item.minStock}</small>
                                                    </td>
                                                    <td>
                                                        <i class="fas fa-map-marker-alt text-muted"></i> ${item.location}
                                                    </td>
                                                    <td>
                                                        <fmt:formatNumber value="${item.unitPrice}" type="number" maxFractionDigits="0"/>
                                                        <small class="text-muted">VND</small>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${item.quantity == 0}">
                                                                <span class="badge bg-danger">OUT OF STOCK</span>
                                                            </c:when>
                                                            <c:when test="${item.quantity <= item.minStock}">
                                                                <span class="badge bg-warning">LOW STOCK</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-success">IN STOCK</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="d-flex gap-1 justify-content-center">
                                                            <button type="button"
                                                                    class="btn btn-sm btn-success"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#stockInModal"
                                                                    onclick="openStockInModal('${item.partDetailId}', '${item.partId}', '${item.partName}', '${item.sku}')"
                                                                    title="Stock In">
                                                                <i class="fas fa-arrow-down"></i>
                                                            </button>
                                                            <a href="${pageContext.request.contextPath}/inventory?action=edit&id=${item.partDetailId}"
                                                               class="btn btn-sm btn-primary" title="Edit">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
<%--                                                            <a href="${pageContext.request.contextPath}/inventory?action=history&id=${item.partDetailId}"--%>
<%--                                                               class="btn btn-sm btn-info" title="History">--%>
<%--                                                                <i class="fas fa-history"></i>--%>
<%--                                                            </a>--%>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Pagination -->
                <c:if test="${not empty inventoryList and totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage - 1}&keyword=${param.keyword}&category=${param.category}">
                                    <i class="fas fa-chevron-left"></i> Previous
                                </a>
                            </li>

                            <li class="page-item active">
                                <span class="page-link">Page ${currentPage} of ${totalPages}</span>
                            </li>

                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage + 1}&keyword=${param.keyword}&category=${param.category}">
                                    Next <i class="fas fa-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>

            </main>
        </div>
    </div>
</div>

<!-- Stock In Modal -->
<div class="modal fade" id="stockInModal" tabindex="-1" aria-labelledby="stockInLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="stockInLabel">
                    <i class="fas fa-arrow-down me-2"></i>Stock In - Import to Warehouse
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form action="${pageContext.request.contextPath}/inventory" method="post">
                <div class="modal-body">
                    <input type="hidden" name="action" value="stockIn">
                    <input type="hidden" name="partDetailId" id="modalPartDetailId">
                    <input type="hidden" name="partId" id="modalPartId">

                    <!-- Part Info Display -->
                    <div class="alert alert-light border mb-3">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <small class="text-muted d-block mb-1">
                                    <i class="fas fa-box me-1"></i>Part Name
                                </small>
                                <strong id="modalPartName" class="text-primary">...</strong>
                            </div>
                            <div class="col-md-6">
                                <small class="text-muted d-block mb-1">
                                    <i class="fas fa-barcode me-1"></i>SKU
                                </small>
                                <strong id="modalSku">...</strong>
                            </div>
                        </div>
                    </div>

                    <!-- Quantity Input -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-cubes text-primary me-1"></i>
                            Quantity <span class="text-danger">*</span>
                        </label>
                        <input type="number" name="quantity" class="form-control" min="1" required
                               placeholder="Enter quantity to import">
                    </div>

                    <!-- Unit Price Input -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-dollar-sign text-success me-1"></i>
                            Unit Price <span class="text-danger">*</span>
                        </label>
                        <div class="input-group">
                            <input type="number" name="unitPrice" class="form-control" min="0" step="1000" required
                                   placeholder="Enter unit price">
                            <span class="input-group-text bg-light">VND</span>
                        </div>
                        <small class="text-muted">Price per unit for this batch</small>
                    </div>

                    <!-- Supplier Select -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-truck text-info me-1"></i>
                            Supplier
                        </label>
                        <select name="supplierId" class="form-select">
                            <option value="0">-- Select Supplier (Optional) --</option>
                            <c:forEach var="sup" items="${suppliers}">
                                <option value="${sup.supplierId}">${sup.supplierName}</option>
                            </c:forEach>
                        </select>
                        <c:if test="${empty suppliers}">
                            <small class="text-warning">
                                <i class="fas fa-exclamation-triangle"></i> No suppliers available
                            </small>
                        </c:if>
                    </div>

                    <!-- Note Textarea -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            <i class="fas fa-sticky-note text-secondary me-1"></i>
                            Note
                        </label>
                        <textarea name="note" class="form-control" rows="3"
                                  placeholder="Enter any notes about this stock in (optional)"></textarea>
                    </div>
                </div>

                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i>Cancel
                    </button>
                    <button type="submit" class="btn btn-success">
                        <i class="fas fa-check me-1"></i>Confirm Stock In
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openStockInModal(partDetailId, partId, partName, sku) {
        document.getElementById('modalPartDetailId').value = partDetailId;
        document.getElementById('modalPartId').value = partId || partDetailId;
        document.getElementById('modalPartName').innerText = partName;
        document.getElementById('modalSku').innerText = sku;
    }
</script>

</body>
</html>