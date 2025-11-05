<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${isEdit ? 'Edit' : 'Add'} Part</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* Thêm style để trường plaintext căn chỉnh tốt hơn */
        .form-control-plaintext {
            padding-top: .375rem;
            padding-bottom: .375rem;
        }
    </style>
</head>
<body>
<div class="container mt-5">
    <h2>${isEdit ? 'Edit' : 'Add New'} Part</h2>
    <hr>

    <form method="post" action="${pageContext.request.contextPath}/inventory">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'add'}">
        <c:if test="${isEdit}">
            <input type="hidden" name="partDetailId" value="${partDetail.partDetailId}">
        </c:if>

        <div class="row">
            <div class="col-md-6">
                <h4>Part Details</h4>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Part Name</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control-plaintext" value="${partDetail.partName}" readonly>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Category</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control-plaintext" value="${partDetail.category}" readonly>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">SKU</label>
                    <div class="col-sm-8">
                        <input type="text" name="sku" class="form-control" value="${partDetail.sku}" required>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Manufacturer</label>
                    <div class="col-sm-8">
                        <input type="text" name="manufacturer" class="form-control" value="${partDetail.manufacturer}">
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Description</label>
                    <div class="col-sm-8">
                        <textarea name="description" class="form-control" rows="3">${partDetail.description}</textarea>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <h4>Stock & Pricing</h4>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Current Quantity</label>
                    <div class="col-sm-8">
                        <input type="number" class="form-control-plaintext" value="${partDetail.quantity}" readonly>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Min Stock</label>
                    <div class="col-sm-8">
                        <input type="number" name="minStock" class="form-control" value="${partDetail.minStock}" min="0" required>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Location</label>
                    <div class="col-sm-8">
                        <input type="text" name="location" class="form-control" value="${partDetail.location}" required>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Unit Price (VND)</label>
                    <div class="col-sm-8">
                        <input type="number" name="unitPrice" class="form-control" value="${partDetail.unitPrice}" min="0" step="0.01" required>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Total Value (VND)</label>
                    <div class="col-sm-8">
                        <input type="text" class="form-control-plaintext" value="${partDetail.totalValue}" readonly>
                    </div>
                </div>

                <div class="row mb-3">
                    <label class="col-sm-4 col-form-label">Stock Status</label>
                    <div class="col-sm-8 pt-2"> <%-- Thêm pt-2 để căn giữa theo chiều dọc --%>
                        <c:choose>
                            <c:when test="${partDetail.quantity == 0}">
                                <span class="badge bg-danger">Out of Stock</span>
                            </c:when>
                            <c:when test="${partDetail.lowStock}">
                                <span class="badge bg-warning">Low Stock</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-success">In Stock</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col-12">
                <button type="submit" class="btn btn-primary">
                    ${isEdit ? 'Update' : 'Add New'}
                </button>
                <a href="${pageContext.request.contextPath}/inventory?action=list" class="btn btn-secondary">Cancel</a>
            </div>
        </div>
    </form>
</div>
</body>
</html>