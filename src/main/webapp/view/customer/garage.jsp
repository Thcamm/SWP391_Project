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
<jsp:include page="/common/header.jsp" />

<main class="garage-container container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>My Vehicles</h2>
        <a href="${pageContext.request.contextPath}/customer/addVehicle" class="btn btn-primary">
            <i class="fa fa-plus"></i> Add New Vehicle
        </a>
    </div>

    <!-- Search + Filter -->
    <form class="row g-2 mb-4" method="get" action="${pageContext.request.contextPath}/customer/garage">
        <div class="col-md-4">
            <input type="text" name="keyword" value="${param.keyword}+${licensePlate != null ? licensePlate : ''}"  class="form-control" placeholder="Search by brand, model or license plate">
        </div>
        <div class="col-md-3">
            <select name="brandFilter" class="form-select">
                <option value="">All Brands</option>
                <c:forEach var="brand" items="${brandList}">
                    <option value="${brand}" ${param.brandFilter == brand ? 'selected' : ''}>${brand}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-success w-100">
                <i class="fa fa-search"></i> Search
            </button>
        </div>
    </form>

    <!-- Alerts -->
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- Vehicle Table -->
    <c:choose>
        <c:when test="${empty vehicleList}">
            <div class="alert alert-info">No vehicles found. Try adding or changing filters.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>Brand</th>
                        <th>Model</th>
                        <th>Year</th>
                        <th>License Plate</th>
                        <th>Change</th>
                        <th>Action</th>
                        <th>Track Service</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="vehicle" items="${vehicleList}">
                        <tr>
                            <td>${vehicle.brand}</td>
                            <td>${vehicle.model}</td>
                            <td>${vehicle.yearManufacture}</td>
                            <td>${vehicle.licensePlate}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/customer/editVehicle?id=${vehicle.vehicleID}" class="btn btn-sm btn-secondary me-1">Edit</a>
                                <form action="${pageContext.request.contextPath}/customer/deleteVehicle" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this vehicle?');">
                                    <input type="hidden" name="vehicleId" value="${vehicle.vehicleID}">
                                    <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                </form>
                            </td>
<%--                            <td>--%>
<%--                                <a href="${pageContext.request.contextPath}/customerservice/createRequest?customerId=${c.customerId}"--%>
<%--                                   class="btn btn-sm btn-success">--%>
<%--                                    Create Request--%>
<%--                                </a>--%>
<%--                            </td>--%>
                            <td>
                                <a href="#"
                                   class="btn btn-sm btn-info">
                                    Tracking
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <nav>
                <ul class="pagination justify-content-center">
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/customer/garage?page=${i}&keyword=${param.keyword}&brandFilter=${param.brandFilter}">
                                    ${i}
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </nav>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/common/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
