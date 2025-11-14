<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Bootstrap 5 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>



<div class="container-fluid px-0">
    <div class="row g-0">
        <!-- Main -->
        <div class="col" style="min-width:0;">
            <main class="p-3 pb-0">
                <!-- Topbar -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex align-items-center justify-content-between">
                        <div>
                            <h2 class="h4 mb-1">
                                ${isEdit ? '✏️ Edit Part' : '➕ Add New Part'}
                            </h2>
                            <p class="text-muted mb-0">
                                ${isEdit ? 'Update part information and stock details' : 'Create a new part entry in the inventory'}
                            </p>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/inventory?action=list"
                               class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left"></i> Back to Inventory
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Error Messages -->
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="bi bi-exclamation-triangle"></i>
                        <strong>Error: </strong>
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
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-body p-4">
                        <form method="post" action="${pageContext.request.contextPath}/inventory">
                            <input type="hidden" name="action" value="${isEdit ? 'update' : 'add'}">
                            <c:if test="${isEdit}">
                                <input type="hidden" name="partDetailId" value="${partDetail.partDetailId}">
                            </c:if>

                            <div class="row g-4">
                                <!-- Left Column: Part Basic Information -->
                                <div class="col-12 col-lg-6">
                                    <div class="mb-4">
                                        <h5 class="border-bottom pb-2 mb-3">
                                            <i class="bi bi-box-seam"></i> Part Information
                                        </h5>

                                        <!-- Part Code -->
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                Part Code <span class="text-danger">*</span>
                                            </label>
                                            <c:choose>
                                                <c:when test="${isEdit}">
                                                    <input type="text" class="form-control bg-light"
                                                           value="${partDetail.partCode}" readonly>
                                                    <small class="text-muted">
                                                        <i class="bi bi-lock"></i> Cannot be changed
                                                    </small>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="text" name="partCode" class="form-control"
                                                           placeholder="e.g., PT-001" required>
                                                    <small class="text-muted">Unique identifier for the part</small>
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
                                                    <small class="text-muted">
                                                        <i class="bi bi-lock"></i> Cannot be changed
                                                    </small>
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
                                                    <small class="text-muted">
                                                        <i class="bi bi-lock"></i> Cannot be changed
                                                    </small>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="text" name="category" class="form-control"
                                                           list="categoryList" placeholder="Select or enter category" required>
                                                    <datalist id="categoryList">
                                                        <c:forEach var="cat" items="${allCategoriesList}">
                                                        <option value="${cat}">
                                                            </c:forEach>
                                                    </datalist>
                                                    <small class="text-muted">Choose from list or type new category</small>
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
                                            <small class="text-muted">Stock Keeping Unit</small>
                                        </div>

                                        <!-- Manufacturer -->
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">Manufacturer</label>
                                            <input type="text" name="manufacturer" class="form-control"
                                                   value="${partDetail.manufacturer}"
                                                   placeholder="e.g., Honda, Toyota">
                                            <small class="text-muted">Brand or manufacturer name</small>
                                        </div>

                                        <!-- Description -->
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">Description</label>
                                            <textarea name="description" class="form-control" rows="4"
                                                      placeholder="Enter detailed part description...">${partDetail.description}</textarea>
                                            <small class="text-muted">Additional notes or specifications</small>
                                        </div>
                                    </div>
                                </div>

                                <!-- Right Column: Stock & Pricing -->
                                <div class="col-12 col-lg-6">
                                    <div class="mb-4">
                                        <h5 class="border-bottom pb-2 mb-3">
                                            <i class="bi bi-stack"></i> Stock & Pricing
                                        </h5>

                                        <!-- Unit Name (Only for Add) -->
                                        <c:if test="${!isEdit}">
                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">
                                                    Unit <span class="text-danger">*</span>
                                                </label>
                                                <input type="text" name="unitName" class="form-control"
                                                       placeholder="e.g., Piece, Liter, Set" required>
                                                <small class="text-muted">Unit of measurement</small>
                                            </div>
                                        </c:if>

                                        <!-- Current Quantity (Only for Edit) -->
                                        <c:if test="${isEdit}">
                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Current Quantity</label>
                                                <div class="input-group">
                                                    <input type="number" class="form-control bg-light"
                                                           value="${partDetail.quantity}" readonly>
                                                    <span class="input-group-text">${partDetail.unitName}</span>
                                                </div>
                                                <small class="text-muted">
                                                    <i class="bi bi-info-circle"></i>
                                                    Use "Stock In" button to add quantity
                                                </small>
                                            </div>
                                        </c:if>

                                        <!-- Min Stock -->
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                Minimum Stock Level <span class="text-danger">*</span>
                                            </label>
                                            <input type="number" name="minStock" class="form-control"
                                                   value="${partDetail.minStock}"
                                                   min="0" placeholder="e.g., 10" required>
                                            <small class="text-muted">Alert threshold for low stock warning</small>
                                        </div>

                                        <!-- Location -->
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">
                                                Storage Location <span class="text-danger">*</span>
                                            </label>
                                            <input type="text" name="location" class="form-control"
                                                   value="${partDetail.location}"
                                                   placeholder="e.g., Shelf A1, Bin C3, Zone 2" required>
                                            <small class="text-muted">Physical location in warehouse</small>
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
                                            <small class="text-muted">Price per unit</small>
                                        </div>

                                        <!-- Stock Status (Only for Edit) -->
                                        <c:if test="${isEdit}">
                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Current Stock Status</label>
                                                <div class="pt-2">
                                                    <c:choose>
                                                        <c:when test="${partDetail.quantity == 0}">
                                                            <span class="badge text-bg-danger fs-6 px-3 py-2">
                                                                <i class="bi bi-x-circle"></i> Out of Stock
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${partDetail.quantity <= partDetail.minStock}">
                                                            <span class="badge text-bg-warning fs-6 px-3 py-2">
                                                                <i class="bi bi-exclamation-triangle"></i> Low Stock
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge text-bg-success fs-6 px-3 py-2">
                                                                <i class="bi bi-check-circle"></i> In Stock
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <!-- Last Update Info -->
                                            <div class="alert alert-light border mb-0">
                                                <small class="text-muted">
                                                    <i class="bi bi-clock-history"></i>
                                                    <strong>Part ID:</strong> ${partDetail.partDetailId}
                                                </small>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="border-top pt-3 mt-3">
                                <div class="d-flex justify-content-end gap-2">
                                    <a href="${pageContext.request.contextPath}/inventory?action=list"
                                       class="btn btn-outline-secondary">
                                        <i class="bi bi-x-circle"></i> Cancel
                                    </a>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-${isEdit ? 'save' : 'plus-circle'}"></i>
                                        ${isEdit ? 'Update Part' : 'Add New Part'}
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Help Section -->
                <div class="row g-3 mb-4">
                    <div class="col-12">
                        <div class="card border-0 shadow-sm">
                            <div class="card-header bg-white border-0">
                                <h3 class="h6 mb-0">💡 Tips</h3>
                            </div>
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-12 col-md-4">
                                        <div class="d-flex align-items-start">
                                            <i class="bi bi-lightbulb text-primary me-2 mt-1"></i>
                                            <div>
                                                <strong>Part Code & Name</strong>
                                                <p class="mb-0 small text-muted">These fields cannot be changed after creation to maintain data integrity.</p>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <div class="d-flex align-items-start">
                                            <i class="bi bi-lightbulb text-success me-2 mt-1"></i>
                                            <div>
                                                <strong>Minimum Stock</strong>
                                                <p class="mb-0 small text-muted">Set a threshold to receive alerts when stock runs low.</p>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-12 col-md-4">
                                        <div class="d-flex align-items-start">
                                            <i class="bi bi-lightbulb text-warning me-2 mt-1"></i>
                                            <div>
                                                <strong>Stock In</strong>
                                                <p class="mb-0 small text-muted">Use the inventory list's "Stock In" button to add quantities.</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    </div>
</div>

