<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                            <h2 class="h4 mb-1">
                                <i class="fas fa-${isEdit ? 'edit' : 'plus-circle'} me-2"></i>
                                ${isEdit ? 'Edit Part Information' : 'Add New Part'}
                            </h2>
                            <p class="text-muted mb-0">
                                ${isEdit ? 'Update inventory item details' : 'Create new inventory item'}
                            </p>
                        </div>
                        <a href="${pageContext.request.contextPath}/inventory?action=list"
                           class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left me-1"></i> Back to List
                        </a>
                    </div>
                </div>

                <!-- Alert Messages -->
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        <c:choose>
                            <c:when test="${param.error == 'missing_required_fields'}">Please fill in all required fields!</c:when>
                            <c:when test="${param.error == 'invalid_quantity'}">Invalid quantity value!</c:when>
                            <c:when test="${param.error == 'invalid_price'}">Invalid price value!</c:when>
                            <c:when test="${param.error == 'invalid_format'}">Invalid data format!</c:when>
                            <c:when test="${param.error == 'add_failed'}">Failed to add part!</c:when>
                            <c:otherwise>An error occurred!</c:otherwise>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Form Card -->
                <form method="post" action="${pageContext.request.contextPath}/inventory">
                    <input type="hidden" name="action" value="${isEdit ? 'update' : 'add'}">
                    <c:if test="${isEdit}">
                        <input type="hidden" name="partDetailId" value="${partDetail.partDetailId}">
                    </c:if>

                    <div class="row g-3">
                        <!-- Left Column: Part Basic Information -->
                        <div class="col-md-6">
                            <div class="card border-0 shadow-sm h-100">
                                <div class="card-header bg-white border-0">
                                    <h5 class="mb-0">
                                        <i class="fas fa-box text-primary me-2"></i>Part Information
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <!-- Part Code -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            Part Code <span class="text-danger">*</span>
                                        </label>
                                        <c:choose>
                                            <c:when test="${isEdit}">
                                                <input type="text" class="form-control bg-light"
                                                       value="${partDetail.partCode}" readonly>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" name="partCode" class="form-control"
                                                       placeholder="e.g., PT-001" required>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <!-- Part Name -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            Part Name <span class="text-danger">*</span>
                                        </label>
                                        <c:choose>
                                            <c:when test="${isEdit}">
                                                <input type="text" class="form-control bg-light"
                                                       value="${partDetail.partName}" readonly>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" name="partName" class="form-control"
                                                       placeholder="e.g., Engine Oil Filter" required>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <!-- Category -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            Category <span class="text-danger">*</span>
                                        </label>
                                        <c:choose>
                                            <c:when test="${isEdit}">
                                                <input type="text" class="form-control bg-light"
                                                       value="${partDetail.category}" readonly>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" name="category" class="form-control"
                                                       list="categoryList" placeholder="Select or enter category" required>
                                                <datalist id="categoryList">
                                                    <c:forEach var="cat" items="${allCategoriesList}">
                                                        <option value="${cat}">
                                                    </c:forEach>
                                                </datalist>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <!-- SKU -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            SKU <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" name="sku" class="form-control"
                                               value="${partDetail.sku}"
                                               placeholder="e.g., ENG-OIL-001" required>
                                    </div>

                                    <!-- Manufacturer -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Manufacturer</label>
                                        <input type="text" name="manufacturer" class="form-control"
                                               value="${partDetail.manufacturer}"
                                               placeholder="e.g., Honda, Toyota">
                                    </div>

                                    <!-- Description -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Description</label>
                                        <textarea name="description" class="form-control" rows="3"
                                                  placeholder="Enter part description...">${partDetail.description}</textarea>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Right Column: Stock & Pricing -->
                        <div class="col-md-6">
                            <div class="card border-0 shadow-sm h-100">
                                <div class="card-header bg-white border-0">
                                    <h5 class="mb-0">
                                        <i class="fas fa-warehouse text-success me-2"></i>Stock & Pricing
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <!-- Unit Name (Only for Add) -->
                                    <c:if test="${!isEdit}">
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                Unit <span class="text-danger">*</span>
                                            </label>
                                            <input type="text" name="unitName" class="form-control"
                                                   placeholder="e.g., Piece, Liter, Set" required>
                                            <small class="text-muted">
                                                <i class="fas fa-info-circle"></i> Enter the unit of measurement
                                            </small>
                                        </div>
                                    </c:if>

                                    <!-- Current Quantity (Only for Edit) -->
                                    <c:if test="${isEdit}">
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">Current Quantity</label>
                                            <div class="input-group">
                                                <input type="number" class="form-control bg-light"
                                                       value="${partDetail.quantity}" readonly>
                                                <span class="input-group-text bg-light">${partDetail.unitName}</span>
                                            </div>
                                            <small class="text-muted">
                                                <i class="fas fa-info-circle"></i>
                                                Use "Stock In" button to add quantity
                                            </small>
                                        </div>
                                    </c:if>

                                    <!-- Min Stock -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            Minimum Stock <span class="text-danger">*</span>
                                        </label>
                                        <input type="number" name="minStock" class="form-control"
                                               value="${partDetail.minStock}"
                                               min="0" placeholder="e.g., 10" required>
                                        <small class="text-muted">
                                            <i class="fas fa-bell"></i> Alert threshold for low stock
                                        </small>
                                    </div>

                                    <!-- Location -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            Location <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" name="location" class="form-control"
                                               value="${partDetail.location}"
                                               placeholder="e.g., Shelf A1, Bin C3" required>
                                    </div>

                                    <!-- Unit Price -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            Unit Price <span class="text-danger">*</span>
                                        </label>
                                        <div class="input-group">
                                            <input type="number" name="unitPrice" class="form-control"
                                                   value="${partDetail.unitPrice}"
                                                   min="0" step="1000" placeholder="e.g., 50000" required>
                                            <span class="input-group-text">VND</span>
                                        </div>
                                    </div>

                                    <!-- Stock Status (Only for Edit) -->
                                    <c:if test="${isEdit}">
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">Stock Status</label>
                                            <div class="pt-2">
                                                <c:choose>
                                                    <c:when test="${partDetail.quantity == 0}">
                                                        <span class="badge bg-danger fs-6 px-3 py-2">
                                                            <i class="fas fa-times-circle"></i> Out of Stock
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${partDetail.quantity <= partDetail.minStock}">
                                                        <span class="badge bg-warning fs-6 px-3 py-2">
                                                            <i class="fas fa-exclamation-triangle"></i> Low Stock
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-success fs-6 px-3 py-2">
                                                            <i class="fas fa-check-circle"></i> In Stock
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="card border-0 shadow-sm mt-3">
                        <div class="card-body">
                            <div class="d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/inventory?action=list"
                                   class="btn btn-secondary">
                                    <i class="fas fa-times me-1"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-${isEdit ? 'save' : 'plus'} me-1"></i>
                                    ${isEdit ? 'Update Part' : 'Add New Part'}
                                </button>
                            </div>
                        </div>
                    </div>
                </form>

            </main>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
