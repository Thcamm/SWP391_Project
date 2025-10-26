<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Vehicle</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/vehicle/addVehicle.css">
</head>
<body>
<%--<%@ include file="/common/header.jsp" %>--%>
<div class="form-container">
    <h2>Edit Vehicle Information</h2>

    <c:if test="${not empty vehicle}">
        <form action="${pageContext.request.contextPath}/customer/editVehicle" method="post" id="brandForm">
            <input type="hidden" name="action" value="selectModel">
            <input type="hidden" name="vehicleId" value="${vehicle.vehicleID}">
            <div class="form-group">
                <label for="brandId">Brand <span class="required">*</span></label>
                <select id="brandId" name="brandId" onchange="document.getElementById('brandForm').submit()" class="form-input">
                    <option value="">-- Select a Brand --</option>
                    <c:forEach var="brand" items="${brands}">
                        <option value="${brand.brandId}" ${brand.brandId == selectedBrandId ? 'selected' : ''}>
                            <c:out value="${brand.brandName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
        </form>

        <form action="${pageContext.request.contextPath}/customer/editVehicle" method="post">
            <input type="hidden" name="action" value="saveVehicle">
            <input type="hidden" name="vehicleId" value="${vehicle.vehicleID}">
            <c:forEach var="brand" items="${brands}"><c:if test="${brand.brandId == selectedBrandId}"><input type="hidden" name="brandName" value="${brand.brandName}"></c:if></c:forEach>

            <div class="form-group">
                <label for="modelName">Model <span class="required">*</span></label>
                <select id="modelName" name="modelName" required class="form-input">
                    <option value="">-- Select a Model --</option>
                    <c:forEach var="model" items="${models}">
                        <option value="${model.modelName}" ${model.modelName == vehicle.model ? 'selected' : ''}>
                            <c:out value="${model.modelName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="yearManufacture">Year of Manufacture <span class="required">*</span></label>
                <input type="number" id="yearManufacture" name="yearManufacture" value="${vehicle.yearManufacture}" required class="form-input" min="1900" max="2025">
            </div>

            <div class="form-group">
                <label for="licensePlate">License Plate <span class="required">*</span></label>
                <input type="text" id="licensePlate" name="licensePlate" value="<c:out value='${vehicle.licensePlate}'/>" required class="form-input">
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">Save Changes</button>
                <a href="${pageContext.request.contextPath}/customer/garage" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </c:if>
</div>
<%--<%@ include file="/common/sidebar.jsp" %>--%>
</body>
</html>