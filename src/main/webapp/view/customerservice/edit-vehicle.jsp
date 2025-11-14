<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<style>
    /* --- Tinh chỉnh thẻ card chính --- */
    .card.shadow-sm {
        background-color: #f8f9fa; /* Màu nền xám rất nhạt */
        border: none;
    }

    /* --- Thẻ thông tin xe (Vehicle Card) --- */
    .vehicle-card {
        border: 1px solid #dee2e6;
        border-radius: 0.5rem; /* Bo góc nhiều hơn */
        transition: all 0.3s ease-in-out;
        background-color: #ffffff;
    }

    /* Hiệu ứng "nổi lên" khi di chuột */
    .vehicle-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }

    /* Tinh chỉnh tiêu đề (Biển số xe) */
    .vehicle-card .card-title {
        font-size: 1.15rem;
        font-weight: 600;
        color: #0d6efd; /* Màu xanh chủ đạo */
    }

    /* Tăng khoảng cách icon */
    .vehicle-card .card-title i {
        margin-right: 8px;
    }

    /* --- Khu vực hiển thị thông tin (Năm, Hãng, Model) --- */
    .vehicle-display p {
        font-size: 0.95rem; /* Chữ to rõ hơn */
        color: #495057;
    }
    .vehicle-display p strong {
        color: #212529; /* Chữ tiêu đề đậm hơn */
    }
    .vehicle-display p i {
        width: 1.25em; /* Căn chỉnh các icon cho thẳng hàng */
    }

    /* --- Khu vực chỉnh sửa (Edit Form) --- */
    .vehicle-form {
        background-color: #fdfdfd;
        border-top: 1px dashed #ced4da;
        padding: 1.25rem; /* Tăng padding cho thoáng */
        margin: 1.25rem -1.25rem -1.25rem -1.25rem; /* Kéo ra sát viền card-body */
        border-radius: 0 0 0.5rem 0.5rem; /* Bo góc ở dưới */
    }

    .vehicle-form h6 {
        font-weight: 600;
        margin-bottom: 1rem;
        color: #343a40;
    }

    /* --- Thêm icon cho nút Save/Cancel --- */
    .btn-save::before {
        font-family: "bootstrap-icons";
        content: "\F28A"; /* Icon check-lg */
        margin-right: 5px;
        font-weight: 600;
    }

    .btn-cancel::before {
        font-family: "bootstrap-icons";
        content: "\F62A"; /* Icon x-lg */
        margin-right: 5px;
        font-weight: 600;
    }

    /* --- Modal Thêm Mới --- */
    #addVehicleModal .modal-header {
        background-color: #0d6efd;
        color: white;
    }
    #addVehicleModal .modal-header .btn-close {
        filter: invert(1) grayscale(100) brightness(200%); /* Nút close màu trắng */
    }

    #addVehicleModal .modal-title {
        font-weight: 600;
    }

    /* Thêm icon vào các label trong modal */
    #addVehicleForm .form-label {
        font-weight: 500;
    }

    /* Định nghĩa icon cho từng label bằng 'for' attribute */
    #addVehicleForm .form-label::after {
        font-family: "bootstrap-icons";
        font-size: 0.9rem;
        color: #6c757d;
        margin-left: 8px;
        font-style: normal;
        font-weight: normal;
    }

    #addVehicleForm label[for="licensePlate"]::after { content: "\F1D7"; } /* Icon bi-person-vcard */
    #addVehicleForm label[for="yearManufacture"]::after { content: "\F1F3"; } /* Icon bi-calendar-event */
    #addVehicleForm label[for="brandName"]::after { content: "\F5D2"; } /* Icon bi-tag */
    #addVehicleForm label[for="modelName"]::after { content: "\F212"; } /* Icon bi-car-front-fill */
</style>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card shadow-sm mb-4">
    <div class="card-header d-flex justify-content-between align-items-center flex-wrap">
        <button type="button" class="btn btn-primary" id="btnAddNewVehicle">
            <i class="bi bi-plus-circle"></i> Add New Vehicle
        </button>
    </div>

    <div class="card-body">

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="mb-0 text-secondary">
                <i class="bi bi-car-front-fill me-2"></i>
                Vehicle List
            </h5>
            <span class="badge bg-primary rounded-pill fs-6">
                Total: ${totalVehicles}
            </span>
        </div>

        <c:if test="${empty vehicles}">
            <p class="text-center text-muted mb-0">This customer has no vehicles.</p>
        </c:if>

        <c:forEach var="vehicle" items="${vehicles}">
            <div class="card mb-3 vehicle-card" id="vehicle-card-${vehicle.vehicleID}">
                <div class="card-body">
                    <input type="hidden" class="vehicle-id" value="${vehicle.vehicleID}">
                    <input type="hidden" class="customer-id" value="${customer.customerId}">

                    <div class="vehicle-display">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="card-title mb-1">
                                    <i class="bi bi-bounding-box-circles"></i>
                                    <span class="display-license-plate">${vehicle.licensePlate}</span>
                                </h6>
                            </div>
                            <div class="vehicle-controls-edit">
                                <button type="button" class="btn btn-outline-primary btn-sm btn-edit"
                                        data-vehicle-id="${vehicle.vehicleID}">
                                    <i class="bi bi-pencil-fill"></i> Edit
                                </button>
                            </div>
                        </div>

                        <div class="row mt-2">
                            <div class="col-md-4 col-6">
                                <p class="mb-1">
                                    <i class="bi bi-calendar-event me-2 text-muted"></i>
                                    <strong>Year:</strong> <span class="display-year">${vehicle.yearManufacture}</span>
                                </p>
                            </div>
                            <div class="col-md-4 col-6">
                                <p class="mb-1">
                                    <i class="bi bi-tag me-2 text-muted"></i>
                                    <strong>Brand:</strong> <span class="display-brand">${vehicle.brand}</span>
                                </p>
                            </div>
                            <div class="col-md-4 col-12 mt-1 mt-md-0">
                                <p class="mb-0">
                                    <i class="bi bi-car-front me-2 text-muted"></i>
                                    <strong>Model:</strong> <span class="display-model">${vehicle.model}</span>
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="vehicle-form" style="display:none;">
                        <h6><i class="bi bi-pencil-square me-2"></i>Edit Vehicle Information</h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">License Plate</label>
                                <input type="text" class="form-control vehicle-field vehicle-license-plate"
                                       value="${vehicle.licensePlate}">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Year of Manufacture</label>
                                <input type="number" class="form-control vehicle-field vehicle-year"
                                       value="${vehicle.yearManufacture}">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Brand</label>
                                <select class="form-select vehicle-field vehicle-brand"></select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Model</label>
                                <select class="form-select vehicle-field vehicle-model"></select>
                            </div>
                        </div>
                        <div class="text-end">
                            <button type="button" class="btn btn-secondary btn-sm btn-cancel"
                                    data-vehicle-id="${vehicle.vehicleID}">Cancel</button>
                            <button type="button" class="btn btn-success btn-sm btn-save"
                                    data-vehicle-id="${vehicle.vehicleID}">Save Changes</button>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>

        <jsp:include page="/view/customerservice/pagination.jsp">
            <jsp:param name="totalPages" value="${vehicleTotalPages}" />
            <jsp:param name="currentPage" value="${vehicleCurrentPage}" />
            <jsp:param name="baseUrl" value="/customerservice/customer-detail" />
            <jsp:param name="paramName" value="vehiclePage" />
            <jsp:param name="queryString" value="&id=${customer.customerId}" />
        </jsp:include>

    </div>
</div>

<div class="modal fade" id="addVehicleModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <form id="addVehicleForm">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="bi bi-plus-circle-fill me-2"></i>Add New Vehicle
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="modalCustomerId" name="customerId" value="${customer.customerId}">

                    <div class="mb-3">
                        <label for="licensePlate" class="form-label">License Plate</label>
                        <input type="text" id="licensePlate" name="licensePlate" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label for="yearManufacture" class="form-label">Year of Manufacture</label>
                        <input type="number" id="yearManufacture" name="yearManufacture" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label for="brandName" class="form-label">Brand</label>
                        <select id="brandName" name="brandName" class="form-select" required>
                            <option value="">-- Select Brand --</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="modelName" class="form-label">Model</label>
                        <select id="modelName" name="modelName" class="form-select" required>
                            <option value="">-- Select Brand First --</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="bi bi-x-lg me-1"></i>Close
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-lg me-1"></i>Add Vehicle
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    (function() {
        'use strict';

        let vehicleData = [];
        const contextPath = "${pageContext.request.contextPath}";

        // Load car models JSON
        function loadCarModels() {
            if (vehicleData.length > 0) return Promise.resolve(vehicleData);
            return fetch(contextPath + '/assets/car-models.json')
                .then(res => res.json())
                .then(data => { vehicleData = data; return data; })
                .catch(err => console.error("Failed to load car models JSON", err));
        }

        // Populate brand select
        function populateBrandSelect(select, selectedBrand) {
            select.innerHTML = "<option value=''>-- Select Brand --</option>";
            vehicleData.forEach(b => {
                const opt = document.createElement("option");
                opt.value = b.brand;
                opt.textContent = b.brand;
                if (b.brand === selectedBrand) opt.selected = true;
                select.appendChild(opt);
            });
        }

        // Populate model select
        function populateModelSelect(select, brandName, selectedModel) {
            select.innerHTML = "<option value=''>-- Select Model --</option>";
            const brand = vehicleData.find(b => b.brand === brandName);
            if (!brand || !brand.models.length) {
                select.innerHTML = "<option value=''>-- No models --</option>";
                return;
            }
            brand.models.forEach(m => {
                const opt = document.createElement("option");
                opt.value = m;
                opt.textContent = m;
                if (m === selectedModel) opt.selected = true;
                select.appendChild(opt);
            });
        }

        // ----------------------------
        // Add Vehicle Modal
        // ----------------------------
        const addForm = document.getElementById("addVehicleForm");
        const brandSelect = document.getElementById("brandName");
        const modelSelect = document.getElementById("modelName");
        const modal = document.getElementById("addVehicleModal");
        const customerIdInput = document.getElementById("modalCustomerId");

        document.getElementById("btnAddNewVehicle").addEventListener("click", () => {
            addForm.reset();
            modelSelect.disabled = true;
            modelSelect.innerHTML = "<option value=''>-- Select Brand First --</option>";
            loadCarModels().then(() => populateBrandSelect(brandSelect));
            new bootstrap.Modal(modal).show();
        });

        brandSelect.addEventListener("change", () => {
            const brandName = brandSelect.value;
            if (!brandName) {
                modelSelect.disabled = true;
                modelSelect.innerHTML = "<option value=''>-- Select Brand First --</option>";
                return;
            }
            modelSelect.disabled = false;
            populateModelSelect(modelSelect, brandName);
        });

        addForm.addEventListener("submit", function(e) {
            e.preventDefault();
            const payload = new URLSearchParams({
                customerId: customerIdInput.value,
                brandName: brandSelect.value,
                modelName: modelSelect.value,
                licensePlate: document.getElementById("licensePlate").value.trim(),
                yearManufacture: document.getElementById("yearManufacture").value
            });

            const btn = addForm.querySelector("button[type='submit']");
            const oldText = btn.textContent;
            btn.disabled = true; btn.textContent = "Saving...";

            fetch(contextPath + '/customerservice/addVehicle?action=saveVehicle', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: payload.toString()
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert("Vehicle added successfully!");
                        location.reload();
                    } else alert(data.message || "Server error");
                })
                .catch(err => alert("Error: " + err.message))
                .finally(() => { btn.disabled = false; btn.textContent = oldText; });
        });

        // ----------------------------
        // Edit Vehicle Cards
        // ----------------------------
        const originalValues = {};

        document.querySelectorAll(".btn-edit").forEach(btn => {
            btn.addEventListener("click", () => {
                const vehicleID = btn.dataset.vehicleId;
                const card = document.getElementById("vehicle-card-" + vehicleID);
                const display = card.querySelector(".vehicle-display");
                const form = card.querySelector(".vehicle-form");

                originalValues[vehicleID] = {};
                form.querySelectorAll(".vehicle-field").forEach(f => originalValues[vehicleID][f.classList[1]] = f.value);

                const brandSelect = form.querySelector(".vehicle-brand");
                const modelSelect = form.querySelector(".vehicle-model");
                loadCarModels().then(() => {
                    populateBrandSelect(brandSelect, originalValues[vehicleID]['vehicle-brand']);
                    populateModelSelect(modelSelect, originalValues[vehicleID]['vehicle-brand'], originalValues[vehicleID]['vehicle-model']);
                });

                brandSelect.addEventListener("change", () => populateModelSelect(modelSelect, brandSelect.value));

                display.style.display = "none";
                form.style.display = "block";
            });
        });

        document.querySelectorAll(".btn-cancel").forEach(btn => {
            btn.addEventListener("click", () => {
                const vehicleID = btn.dataset.vehicleId;
                const card = document.getElementById("vehicle-card-" + vehicleID);
                const display = card.querySelector(".vehicle-display");
                const form = card.querySelector(".vehicle-form");

                const values = originalValues[vehicleID];
                if (values) form.querySelectorAll(".vehicle-field").forEach(f => f.value = values[f.classList[1]]);

                form.style.display = "none";
                display.style.display = "block";
            });
        });

        document.querySelectorAll(".btn-save").forEach(btn => {
            btn.addEventListener("click", () => {
                const vehicleID = btn.dataset.vehicleId;
                const card = document.getElementById("vehicle-card-" + vehicleID);
                const form = card.querySelector(".vehicle-form");

                const payload = new URLSearchParams({
                    action: 'updateVehicle',
                    vehicleId: vehicleID,
                    customerId: card.querySelector('.customer-id').value,
                    licensePlate: form.querySelector('.vehicle-license-plate').value.trim(),
                    yearManufacture: form.querySelector('.vehicle-year').value,
                    brandName: form.querySelector('.vehicle-brand').value,
                    modelName: form.querySelector('.vehicle-model').value
                });

                btn.disabled = true;
                const oldText = btn.textContent;
                btn.textContent = "Saving...";

                fetch(contextPath + '/customerservice/addVehicle', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: payload.toString()
                })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) location.reload();
                        else alert(data.message || "Server error");
                    })
                    .catch(err => alert("Error: " + err.message))
                    .finally(() => { btn.disabled = false; btn.textContent = oldText; });
            });
        });

    })();
</script>
