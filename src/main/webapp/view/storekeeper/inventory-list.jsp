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

                <!-- Statistics -->
                <div class="row g-3 mb-3">
                    <div class="col-12 col-sm-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">Total Items</div>
                                <div class="display-6 fw-semibold">${totalItems != null ? totalItems : 0}</div>
                                <div class="text-muted small">In inventory</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">Low Stock Alert</div>
                                <div class="display-6 fw-semibold text-warning">${lowStockCount != null ? lowStockCount : 0}</div>
                                <div class="text-muted small">Need attention</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">Total Value</div>
                                <div class="display-6 fw-semibold text-success">
                                    <fmt:formatNumber value="${totalValue != null ? totalValue : 0}" type="number" maxFractionDigits="0"/>
                                </div>
                                <div class="text-muted small">VND</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Search & Filter -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-0">
                        <h3 class="h5 mb-0">🔍 Search & Filter</h3>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/inventory" method="get">
                            <input type="hidden" name="action" value="search">

                            <div class="row g-3">
                                <div class="col-12 col-md-6 col-lg-4">
                                    <label class="form-label">Keyword (Name/SKU)</label>
                                    <input type="text" name="keyword" class="form-control"
                                           value="${param.keyword}" placeholder="Search...">
                                </div>

                                <div class="col-12 col-md-6 col-lg-4">
                                    <label class="form-label">Location</label>
                                    <input type="text" name="location" class="form-control"
                                           value="${param.location}" placeholder="Storage location">
                                </div>

                                <div class="col-12 col-md-6 col-lg-4">
                                    <label class="form-label">Status</label>
                                    <select name="stockStatus" class="form-select">
                                        <option value="">-- All --</option>
                                        <option value="normal" ${param.stockStatus == 'normal' ? 'selected' : ''}>In Stock</option>
                                        <option value="low" ${param.stockStatus == 'low' ? 'selected' : ''}>Low Stock</option>
                                        <option value="out" ${param.stockStatus == 'out' ? 'selected' : ''}>Out of Stock</option>
                                    </select>
                                </div>

                                <div class="col-12 col-md-6 col-lg-4">
                                    <label class="form-label">Category</label>
                                    <input type="text" name="category" class="form-control"
                                           value="${param.category}" placeholder="Part category">
                                </div>

                                <div class="col-12 col-md-6 col-lg-4">
                                    <label class="form-label">Price Range (VND)</label>
                                    <div class="input-group">
                                        <input type="number" name="priceFrom" class="form-control"
                                               value="${param.priceFrom}" placeholder="From">
                                        <span class="input-group-text">-</span>
                                        <input type="number" name="priceTo" class="form-control"
                                               value="${param.priceTo}" placeholder="To">
                                    </div>
                                </div>

                                <div class="col-12 col-md-6 col-lg-4">
                                    <label class="form-label">Manufacturer</label>
                                    <input type="text" name="manufacturer" class="form-control"
                                           value="${param.manufacturer}" placeholder="Brand name">
                                </div>
                            </div>

                            <div class="d-flex gap-2 mt-3">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-search"></i> Search
                                </button>
                                <a href="${pageContext.request.contextPath}/inventory?action=list"
                                   class="btn btn-outline-secondary">
                                    <i class="bi bi-arrow-clockwise"></i> Reset
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Categories Filter -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body">
                        <div class="d-flex flex-wrap gap-2 align-items-center">
                            <strong class="me-2">Categories:</strong>
                            <a href="${pageContext.request.contextPath}/inventory?action=list"
                               class="badge text-bg-primary text-decoration-none">All</a>
                            <c:forEach var="cat" items="${allCategoriesList}">
                                <a href="${pageContext.request.contextPath}/inventory?action=list&category=${cat}"
                                   class="badge text-bg-light text-dark text-decoration-none">${cat}</a>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <!-- Inventory Table -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-0">
                        <h3 class="h5 mb-0">📋 Inventory List</h3>
                    </div>

                    <c:choose>
                        <c:when test="${empty inventoryList}">
                            <div class="card-body">
                                <div class="alert alert-light mb-0 text-center py-5">
                                    <div class="display-1 mb-3">📭</div>
                                    <p class="mb-0">No inventory data found.</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card-body pt-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>SKU</th>
                                            <th>Part Name</th>
                                            <th>Category</th>
                                            <th>Manufacturer</th>
                                            <th>Quantity</th>
                                            <th>Location</th>
                                            <th>Unit Price</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="item" items="${inventoryList}" varStatus="st">
                                            <tr>
                                                <td>${(currentPage - 1) * 10 + st.count}</td>
                                                <td><span class="font-monospace badge text-bg-secondary">${item.sku}</span></td>
                                                <td>
                                                    <strong>${item.partName}</strong><br/>
                                                    <small class="text-muted">${item.partCode}</small>
                                                </td>
                                                <td><span class="badge text-bg-light text-dark">${item.category}</span></td>
                                                <td>${empty item.manufacturer ? 'N/A' : item.manufacturer}</td>
                                                <td>
                                                    <strong class="fs-5">${item.quantity}</strong> <small class="text-muted">${item.unitName}</small><br/>
                                                    <small class="text-muted">Min: ${item.minStock}</small>
                                                </td>
                                                <td>${item.location}</td>
                                                <td class="font-monospace">
                                                    <fmt:formatNumber value="${item.unitPrice}" type="number" maxFractionDigits="0"/> VND
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${item.quantity == 0}">
                                                            <span class="badge text-bg-danger">OUT OF STOCK</span>
                                                        </c:when>
                                                        <c:when test="${item.quantity <= item.minStock}">
                                                            <span class="badge text-bg-warning">LOW STOCK</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge text-bg-success">OK</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="d-flex gap-1">
                                                        <button type="button"
                                                                class="btn btn-success btn-sm"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#stockInModal"
                                                                onclick="openStockInModal('${item.partDetailId}', '${item.partId}', '${item.partName}', '${item.sku}')">
                                                            <i class="bi bi-arrow-down-circle"></i>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/inventory?action=edit&id=${item.partDetailId}"
                                                           class="btn btn-primary btn-sm" title="Edit">
                                                            <i class="bi bi-pencil"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Pagination -->
                                <c:if test="${totalPages > 1}">
                                    <nav class="mt-3">
                                        <ul class="pagination pagination-sm mb-0 justify-content-center">
                                            <c:if test="${currentPage > 1}">
                                                <li class="page-item">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage - 1}&keyword=${param.keyword}&category=${param.category}">
                                                        Previous
                                                    </a>
                                                </li>
                                            </c:if>

                                            <c:forEach begin="1" end="${totalPages}" var="p">
                                                <li class="page-item ${p == currentPage ? 'active' : ''}">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${p}&keyword=${param.keyword}&category=${param.category}">
                                                            ${p}
                                                    </a>
                                                </li>
                                            </c:forEach>

                                            <c:if test="${currentPage < totalPages}">
                                                <li class="page-item">
                                                    <a class="page-link"
                                                       href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage + 1}&keyword=${param.keyword}&category=${param.category}">
                                                        Next
                                                    </a>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </nav>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </main>
        </div>
    </div>
</div>

<!-- Stock In Modal -->
<div class="modal fade" id="stockInModal" tabindex="-1" aria-labelledby="stockInLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="stockInLabel">
                    <i class="bi bi-box-arrow-in-down"></i> Stock In (Nhập kho)
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form action="${pageContext.request.contextPath}/inventory" method="post">
                <div class="modal-body">
                    <input type="hidden" name="action" value="stockIn">
                    <input type="hidden" name="partDetailId" id="modalPartDetailId">
                    <input type="hidden" name="partId" id="modalPartId">

                    <div class="alert alert-light border mb-3">
                        <div class="row">
                            <div class="col-6">
                                <small class="text-muted">Part Name:</small><br>
                                <strong id="modalPartName" class="text-success">...</strong>
                            </div>
                            <div class="col-6">
                                <small class="text-muted">SKU:</small><br>
                                <strong id="modalSku" class="font-monospace">...</strong>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Quantity <span class="text-danger">*</span></label>
                        <input type="number" name="quantity" class="form-control" min="1" required
                               placeholder="Enter quantity">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Unit Price (VND) <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="number" name="unitPrice" class="form-control" min="0" step="1000" required
                                   placeholder="Enter price">
                            <span class="input-group-text">VND</span>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Supplier</label>
                        <select name="supplierId" class="form-select">
                            <option value="0">-- Select Supplier --</option>
                            <c:forEach var="sup" items="${suppliers}">
                                <option value="${sup.supplierId}">${sup.supplierName}</option>
                            </c:forEach>
                        </select>
                        <c:if test="${empty suppliers}">
                            <small class="text-danger">Supplier list is empty. Please check Controller.</small>
                        </c:if>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Note</label>
                        <textarea name="note" class="form-control" rows="2" placeholder="Optional note..."></textarea>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-circle"></i> Confirm Stock In
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openStockInModal(partDetailId, partId, partName, sku) {
        document.getElementById('modalPartDetailId').value = partDetailId;
        if (partId) {
            document.getElementById('modalPartId').value = partId;
        }
        document.getElementById('modalPartName').innerText = partName;
        document.getElementById('modalSku').innerText = sku;
    }
</script>

