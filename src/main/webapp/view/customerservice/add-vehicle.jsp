<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Include CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/add-vehicle.css">

<!-- Add Vehicle Modal -->
<div class="modal fade" id="addVehicleModal" tabindex="-1" aria-labelledby="addVehicleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">
                <h5 class="modal-title" id="addVehicleModalLabel">Add New Vehicle</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
            </div>

            <!-- Modal Form -->
            <form id="addVehicleForm" method="post">
                <div class="modal-body">

                    <!-- Vehicle Information Card -->
                    <div class="vehicle-card">
                        <div class="vehicle-header">
                            <h3 class="vehicle-title">Vehicle Information</h3>
                        </div>

                        <div class="grid-2" style="padding: 1.5rem;">
                            <!-- Hãng xe -->
                            <div class="form-group">
                                <label for="brandId" class="form-label">
                                    Brand <span class="text-danger">*</span>
                                </label>
                                <select id="brandId" name="brandId" class="form-select" required>
                                    <option value="">-- Select brand --</option>
                                    <c:forEach var="brand" items="${brands}">
                                        <option value="${brand.brandId}">${brand.brandName}</option>
                                    </c:forEach>
                                </select>
                                <small class="text-muted">Please select brand first</small>
                            </div>

                            <!-- Model -->
                            <div class="form-group">
                                <label for="modelName" class="form-label">
                                    Model <span class="text-danger">*</span>
                                </label>
                                <select id="modelName" name="modelName" class="form-select" required disabled>
                                    <option value="">-- Select model --</option>
                                </select>

                            </div>

                            <!-- Biển số xe -->
                            <div class="form-group">
                                <label for="licensePlate" class="form-label">
                                    License Plate <span class="text-danger">*</span>
                                </label>
                                <input type="text"
                                       id="licensePlate"
                                       name="licensePlate"
                                       class="form-control"
                                       required
                                       placeholder="VD: 30A-12345">
                                <small class="text-muted">Format: 30A-12345</small>
                            </div>

                            <!-- Năm sản xuất -->
                            <div class="form-group">
                                <label for="yearManufacture" class="form-label">
                                    Year Manufacture <span class="text-danger">*</span>
                                </label>
                                <input type="number"
                                       id="yearManufacture"
                                       name="yearManufacture"
                                       class="form-control"
                                       min="2000"
                                       max="2025"
                                       required
                                       placeholder="VD: 2023">
                                <small class="text-muted">From 2000 to now</small>
                            </div>
                        </div>

                        <!-- Hidden Customer ID -->
                        <input type="hidden" id="modalCustomerId" name="customerId" value="${customer.customerId}">
                    </div>

                </div>

                <!-- Modal Footer -->
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu xe</button>
                </div>
            </form>

        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/customerservice/add-vehicle.js"></script>