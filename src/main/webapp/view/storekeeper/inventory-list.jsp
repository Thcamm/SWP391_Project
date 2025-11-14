<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Inventory Management (Raw)</title>
</head>
<body>

<%-- --- HEADER & MENU --- --%>
<div>
    <h1>Inventory Management</h1>
    <p>
        <a href="${pageContext.request.contextPath}/inventory?action=add">[+] Add New Part</a> |
        <a href="${pageContext.request.contextPath}/stock-in">[->] General Stock In</a> |
        <a href="${pageContext.request.contextPath}/stock-out">[<-] Stock Out</a>
    </p>
</div>

<hr/>

<%-- --- MESSAGE ALERT --- --%>
<c:if test="${not empty param.message}">
    <p style="color: green;"><strong>Message: ${param.message}</strong></p>
</c:if>

<c:if test="${not empty param.error}">
    <p style="color: red;"><strong>Error: ${param.error}</strong></p>
</c:if>

<%-- --- STATISTICS --- --%>
<div style="border: 1px solid #000; padding: 10px; margin-bottom: 20px;">
    <h3>Statistics</h3>
    <ul>
        <li>Total Items: <strong>${totalItems != null ? totalItems : 0}</strong></li>
        <li>Low Stock: <strong>${lowStockCount != null ? lowStockCount : 0}</strong></li>
        <li>Total Value: <strong><fmt:formatNumber value="${totalValue != null ? totalValue : 0}" type="number"
                                                   maxFractionDigits="0"/> VND</strong></li>
    </ul>
</div>

<%-- --- SEARCH FORM --- --%>
<fieldset>
    <legend>Search & Filter</legend>
    <form action="${pageContext.request.contextPath}/inventory" method="get">
        <input type="hidden" name="action" value="search">

        <label>Keyword (Name/SKU):</label>
        <input type="text" name="keyword" value="${param.keyword}"><br><br>

        <label>Location:</label>
        <input type="text" name="location" value="${param.location}"><br><br>

        <label>Status:</label>
        <select name="stockStatus">
            <option value="">-- All --</option>
            <option value="normal" ${param.stockStatus == 'normal' ? 'selected' : ''}>In Stock</option>
            <option value="low" ${param.stockStatus == 'low' ? 'selected' : ''}>Low Stock</option>
            <option value="out" ${param.stockStatus == 'out' ? 'selected' : ''}>Out of Stock</option>
        </select><br><br>

        <label>Category:</label>
        <input type="text" name="category" value="${param.category}"><br><br>

        <label>Price (From - To):</label>
        <input type="number" name="priceFrom" value="${param.priceFrom}"> -
        <input type="number" name="priceTo" value="${param.priceTo}"><br><br>

        <label>Manufacturer:</label>
        <input type="text" name="manufacturer" value="${param.manufacturer}"><br><br>

        <button type="submit">Search</button>
        <a href="${pageContext.request.contextPath}/inventory?action=list">Reset</a>
    </form>
</fieldset>

<br/>

<%-- --- CATEGORY LINKS --- --%>
<div>
    <strong>Categories: </strong>
    <a href="${pageContext.request.contextPath}/inventory?action=list">All</a>
    <c:forEach var="cat" items="${allCategoriesList}">
        | <a href="${pageContext.request.contextPath}/inventory?action=list&category=${cat}">${cat}</a>
    </c:forEach>
</div>

<br/>

<%-- --- DATA TABLE --- --%>
<table border="1" cellpadding="5" cellspacing="0" width="100%">
    <thead>
    <tr style="background-color: #ccc;">
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
    <c:choose>
        <c:when test="${empty inventoryList}">
            <tr>
                <td colspan="9" align="center">No data found.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="item" items="${inventoryList}">
                <tr>
                    <td>${item.sku}</td>
                    <td>
                            ${item.partName}<br/>
                        <small>(${item.partCode})</small>
                    </td>
                    <td>${item.category}</td>
                    <td>${empty item.manufacturer ? 'N/A' : item.manufacturer}</td>
                    <td>
                        <strong>${item.quantity}</strong> ${item.unitName}
                        <br/><small>Min: ${item.minStock}</small>
                    </td>
                    <td>${item.location}</td>
                    <td>
                        <fmt:formatNumber value="${item.unitPrice}" type="number" maxFractionDigits="0"/>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${item.quantity == 0}">OUT OF STOCK</c:when>
                            <c:when test="${item.quantity <= item.minStock}">LOW STOCK</c:when>
                            <c:otherwise>OK</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                            <%-- Links to Actions --%>
                        <button type="button"
                                class="btn btn-sm btn-success"
                                data-bs-toggle="modal"
                                data-bs-target="#stockInModal"
                                onclick="openStockInModal('${item.partDetailId}', '${item.partId}', '${item.partName}', '${item.sku}')">
                            <i class="fas fa-arrow-down"></i> Stock In
                        </button>
                        <a href="${pageContext.request.contextPath}/inventory?action=history&id=${item.partDetailId}">[History]</a>
                        <br/>
                        <a href="${pageContext.request.contextPath}/inventory?action=edit&id=${item.partDetailId}">[Edit]</a>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<%-- --- PAGINATION --- --%>
<c:if test="${not empty inventoryList and totalPages > 1}">
    <div style="margin-top: 20px; text-align: center;">
            <%-- Previous Link --%>
        <c:if test="${currentPage > 1}">
            <a href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage - 1}&keyword=${param.keyword}&category=${param.category}">&laquo;
                Previous</a>
        </c:if>

        <span> | Page ${currentPage} of ${totalPages} | </span>

            <%-- Next Link --%>
        <c:if test="${currentPage < totalPages}">
            <a href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage + 1}&keyword=${param.keyword}&category=${param.category}">Next
                &raquo;</a>
        </c:if>
    </div>
</c:if>
<div class="modal fade" id="stockInModal" tabindex="-1" aria-labelledby="stockInLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="stockInLabel">
                    <i class="fas fa-box-open"></i> Stock In (Nhập kho)
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                        aria-label="Close"></button>
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
                                <strong id="modalPartName" class="text-primary">...</strong>
                            </div>
                            <div class="col-6">
                                <small class="text-muted">SKU:</small><br>
                                <strong id="modalSku">...</strong>
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
                    <button type="submit" class="btn btn-primary">Confirm Stock In</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    function openStockInModal(partDetailId, partId, partName, sku) {
        // 1. Tìm các thẻ trong Modal theo ID
        var inputDetailId = document.getElementById('modalPartDetailId');
        var inputPartId = document.getElementById('modalPartId');
        var txtName = document.getElementById('modalPartName');
        var txtSku = document.getElementById('modalSku');

        // 2. Gán dữ liệu lấy từ nút bấm vào các thẻ đó
        inputDetailId.value = partDetailId;

        // Kiểm tra nếu partId bị null (trường hợp model không có field này) thì gán tạm bằng partDetailId hoặc xử lý khác
        if (partId) {
            inputPartId.value = partId;
        }

        txtName.innerText = partName;
        txtSku.innerText = sku;
    }
</script>
</body>
</html>