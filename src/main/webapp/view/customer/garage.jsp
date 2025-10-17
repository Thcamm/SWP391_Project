<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Garage</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/vehicle/garage.css">

</head>
<body>
<%@ include file="/common/header.jsp" %>
<main class="garage-container">
    <div class="garage-header">
        <h2>My Vehicles</h2>
        <a href="${pageContext.request.contextPath}/customer/addVehicle" class="btn btn-primary">Add New Vehicle</a>
    </div>

    <c:choose>
        <c:when test="${empty vehicleList}">
            <div class="empty-garage">
                <p>You haven't added any vehicles yet.</p>
                <p>Click "Add New Vehicle" to get started!</p>
            </div>
        </c:when>
        <c:otherwise>
            <table class="vehicle-table">
                <thead>
                <tr>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Year</th>
                    <th>License Plate</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="vehicle" items="${vehicleList}">
                    <tr>
                        <td><c:out value="${vehicle.brand}"/></td>
                        <td><c:out value="${vehicle.model}"/></td>
                        <td><c:out value="${vehicle.yearManufacture}"/></td>
                        <td><c:out value="${vehicle.licensePlate}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/customer/editVehicle?id=${vehicle.vehicleID}" class="btn btn-secondary">Edit</a>
                            <form action="${pageContext.request.contextPath}/customer/deleteVehicle" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this vehicle?');">
                                <input type="hidden" name="vehicleId" value="${vehicle.vehicleID}">
                                <button type="submit" class="btn btn-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</main>
<%@ include file="/common/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>