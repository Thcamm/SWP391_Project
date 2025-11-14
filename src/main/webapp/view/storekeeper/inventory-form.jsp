<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${isEdit ? 'Edit' : 'Add'} Part</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .form-container {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-top: 30px;
            margin-bottom: 30px;
        }
        .section-title {
            color: #1e293b;
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #3b82f6;
        }
        .form-control-plaintext {
            padding-top: .375rem;
            padding-bottom: .375rem;
            background-color: #f8f9fa;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            padding-left: 12px;
        }
        .btn-container {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #e2e8f0;
        }
    </style>
</head>
<body>
<%@ include file="/common/header.jsp" %>

<div class="container">
    <div class="form-container">
        <h2 class="mb-4">
            <i class="fas fa-${isEdit ? 'edit' : 'plus-circle'}"></i>
            ${isEdit ? 'Edit Part Information' : 'Add New Part'}
        </h2>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                <c:choose>
                    <c:when test="${param.error == 'missing_required_fields'}">Please fill in all required fields!</c:when>
                    <c:when test="${param.error == 'invalid_quantity'}">Invalid quantity value!</c:when>
                    <c:when test="${param.error == 'invalid_price'}">Invalid price value!</c:when>
                    <c:when test="${param.error == 'invalid_format'}">Invalid data format!</c:when>
                    <c:when test="${param.error == 'add_failed'}">Failed to add part!</c:when>
                    <c:otherwise>An error occurred!</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/inventory">
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'add'}">
            <c:if test="${isEdit}">
                <input type="hidden" name="partDetailId" value="${partDetail.partDetailId}">
            </c:if>

            <div class="row">
                <!-- Left Column: Part Basic Information -->
                <div class="col-md-6">
                    <h4 class="section-title">
                        <i class="fas fa-box"></i> Part Information
                    </h4>

                    <!-- Part Code -->
                    <div class="mb-3">
                        <label class="form-label">
                            Part Code <span class="text-danger">*</span>
                        </label>
                        <c:choose>
                            <c:when test="${isEdit}">
                                <input type="text" class="form-control-plaintext"
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
                        <label class="form-label">
                            Part Name <span class="text-danger">*</span>
                        </label>
                        <c:choose>
                            <c:when test="${isEdit}">
                                <input type="text" class="form-control-plaintext"
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
                        <label class="form-label">
                            Category <span class="text-danger">*</span>
                        </label>
                        <c:choose>
                            <c:when test="${isEdit}">
                                <input type="text" class="form-control-plaintext"
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
                        <label class="form-label">
                            SKU <span class="text-danger">*</span>
                        </label>
                        <input type="text" name="sku" class="form-control"
                               value="${partDetail.sku}"
                               placeholder="e.g., ENG-OIL-001" required>
                    </div>

                    <!-- Manufacturer -->
                    <div class="mb-3">
                        <label class="form-label">Manufacturer</label>
                        <input type="text" name="manufacturer" class="form-control"
                               value="${partDetail.manufacturer}"
                               placeholder="e.g., Honda, Toyota">
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea name="description" class="form-control" rows="3"
                                  placeholder="Enter part description...">${partDetail.description}</textarea>
                    </div>
                </div>

                <!-- Right Column: Stock & Pricing -->
                <div class="col-md-6">
                    <h4 class="section-title">
                        <i class="fas fa-warehouse"></i> Stock & Pricing
                    </h4>

                    <!-- Unit Name (Only for Add) -->
                    <c:if test="${!isEdit}">
                        <div class="mb-3">
                            <label class="form-label">
                                Unit <span class="text-danger">*</span>
                            </label>
                            <input type="text" name="unitName" class="form-control"
                                   placeholder="e.g., Piece, Liter, Set" required>
                            <small class="text-muted">Enter the unit of measurement</small>
                        </div>
                    </c:if>

                    <!-- Current Quantity (Only for Edit) -->
                    <c:if test="${isEdit}">
                        <div class="mb-3">
                            <label class="form-label">Current Quantity</label>
                            <input type="number" class="form-control-plaintext"
                                   value="${partDetail.quantity}" readonly>
                            <small class="text-muted">
                                <i class="fas fa-info-circle"></i>
                                Use "Stock In" button to add quantity
                            </small>
                        </div>
                    </c:if>

                    <!-- Min Stock -->
                    <div class="mb-3">
                        <label class="form-label">
                            Minimum Stock <span class="text-danger">*</span>
                        </label>
                        <input type="number" name="minStock" class="form-control"
                               value="${partDetail.minStock}"
                               min="0" placeholder="e.g., 10" required>
                        <small class="text-muted">Alert threshold for low stock</small>
                    </div>

                    <!-- Location -->
                    <div class="mb-3">
                        <label class="form-label">
                            Location <span class="text-danger">*</span>
                        </label>
                        <input type="text" name="location" class="form-control"
                               value="${partDetail.location}"
                               placeholder="e.g., Shelf A1, Bin C3" required>
                    </div>

                    <!-- Unit Price -->
                    <div class="mb-3">
                        <label class="form-label">
                            Unit Price (VND) <span class="text-danger">*</span>
                        </label>
                        <input type="number" name="unitPrice" class="form-control"
                               value="${partDetail.unitPrice}"
                               min="0" step="1000" placeholder="e.g., 50000" required>
                    </div>

                    <!-- Stock Status (Only for Edit) -->
                    <c:if test="${isEdit}">
                        <div class="mb-3">
                            <label class="form-label">Stock Status</label>
                            <div class="pt-2">
                                <c:choose>
                                    <c:when test="${partDetail.quantity == 0}">
                                        <span class="badge bg-danger fs-6">
                                            <i class="fas fa-times-circle"></i> Out of Stock
                                        </span>
                                    </c:when>
                                    <c:when test="${partDetail.quantity <= partDetail.minStock}">
                                        <span class="badge bg-warning fs-6">
                                            <i class="fas fa-exclamation-triangle"></i> Low Stock
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-success fs-6">
                                            <i class="fas fa-check-circle"></i> In Stock
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Action Buttons -->
            <div class="btn-container text-end">
                <a href="${pageContext.request.contextPath}/inventory?action=list"
                   class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-${isEdit ? 'save' : 'plus'}"></i>
                    ${isEdit ? 'Update Part' : 'Add New Part'}
                </button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>