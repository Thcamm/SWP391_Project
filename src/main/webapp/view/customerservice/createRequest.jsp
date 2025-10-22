<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Create Service Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
</head>
<body class="bg-light">


<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0"><i class="bi bi-plus-circle"></i> Create New Service Request</h4>
                </div>
                <div class="card-body">

                    <h5 class="card-title">Customer Information</h5>
                    <div class="mb-3">
                        <strong>Name:</strong>
                        <c:out value="${customer.fullName}" />
                    </div>
                    <div class="mb-3">
                        <strong>Email:</strong>
                        <c:out value="${customer.email}" />
                    </div>
                    <div class="mb-3">
                        <strong>Phone:</strong>
                        <c:out value="${customer.phoneNumber}" />
                    </div>
                    <hr />

                    <form action="${pageContext.request.contextPath}/customerservice/createRequest" method="POST">

                        <input type="hidden" name="customerId" value="${customer.customerId}" />

                        <div class="mb-3">
                            <label for="vehicleId" class="form-label">Select Vehicle: <span class="text-danger">*</span></label>
                            <select id="vehicleId" name="vehicleId" class="form-select" required>
                                <option value="">-- Please select a vehicle --</option>
                                <c:forEach var="vehicle" items="${vehicles}">
                                    <option value="${vehicle.vehicleID}">
                                            ${vehicle.brand} ${vehicle.model} - ${vehicle.licensePlate}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="serviceId" class="form-label">Select Service: <span class="text-danger">*</span></label>
                            <select id="serviceId" name="serviceId" class="form-select" required>
                                <option value="">-- Please select a service --</option>
                                <c:forEach var="service" items="${services}">
                                    <option value="${service.serviceTypeID}">
                                            ${service.serviceName} (<fmt:formatNumber value="${service.price}" type="currency" currencySymbol=""/> VND)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/customerservice/search-customer" class="btn btn-secondary me-md-2">
                                <i class="bi bi-x-circle"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-send"></i> Submit Request
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>