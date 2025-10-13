<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add New Vehicle</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/vehicle/addVehicle.css">
</head>
<body>
<div class="form-container">
    <h2>Add New Vehicle</h2>

    <c:if test="${not empty error}">
        <div class="message error">${error}</div>
    </c:if>

    <form action="addVehicle" method="post">
        <input type="hidden" name="action" value="selectModel">
        <div class="form-group">
            <label for="brandId">Brand <span class="required">*</span></label>
            <select id="brandId" name="brandId" onchange="this.form.submit()" class="form-input">
                <option value="">-- Select a Brand --</option>
                <c:forEach var="brand" items="${brands}">
                    <option value="${brand.brandId}" ${brand.brandId == selectedBrandId ? 'selected' : ''}>
                        <c:out value="${brand.brandName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
    </form>

    <c:if test="${not empty selectedBrandId}">
        <form action="addVehicle" method="post">
            <input type="hidden" name="action" value="saveVehicle">
            <input type="hidden" name="brandName" value="${brands.stream().filter(b -> b.brandId == selectedBrandId).findFirst().get().brandName}">

            <div class="form-group">
                <label for="modelName">Model <span class="required">*</span></label>
                <select id="modelName" name="modelName" required class="form-input">
                    <option value="">-- Select a Model --</option>
                    <c:forEach var="model" items="${models}">
                        <option value="${model.modelName}"><c:out value="${model.modelName}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="yearManufacture">Year of Manufacture <span class="required">*</span></label>
                <input type="number" id="yearManufacture" name="yearManufacture" required class="form-input" min="1900" max="2025">
            </div>

            <div class="form-group">
                <label for="licensePlate">License Plate <span class="required">*</span></label>
                <input type="text" id="licensePlate" name="licensePlate" required class="form-input">
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">Save Vehicle</button>
                <a href="${pageContext.request.contextPath}/garage" class="btn btn-secondary">Back to Garage</a>
            </div>
        </form>
    </c:if>
</div>
</body>
</html>