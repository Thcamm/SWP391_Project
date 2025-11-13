<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* -------------------------------------------
       Elegant & Professional Color Palette
    ------------------------------------------- */
    :root {
        --theme-navy: #2c3e50;       /* Deep professional navy blue */
        --theme-soft-bg: #f8f9fa;    /* Very light grayish background (Bootstrap bg-light) */
        --theme-text-dark: #343a40;  /* Main text color */
        --theme-border-light: #e9ecef; /* Subtle border color */
    }

    /* 1. Card Wrapper */
    .card.shadow-sm {
        box-shadow: 0 .1rem .4rem rgba(0,0,0,0.05) !important;
        border: none;
    }

    /* 2. Card Header (contains "Add New Vehicle" button) */
    .card-header {
        background-color: #ffffff;
        border-bottom: 1px solid var(--theme-border-light);
    }

    .card-header h5 {
        color: var(--theme-navy);
    }

    /* 3. Individual Vehicle Card */
    .vehicle-card {
        border: 1px solid var(--theme-border-light);
        border-left: 4px solid var(--theme-navy);
        transition: all 0.2s ease-in-out;
        box-shadow: none;
    }
    .vehicle-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 .2rem .5rem rgba(0,0,0,0.06);
    }

    /* 4. READ Mode (Display) */
    .vehicle-display .card-title {
        color: var(--theme-navy);
        font-weight: 600;
    }

    /* 5. EDIT Mode */
    .vehicle-form {
        background-color: var(--theme-soft-bg);
        padding: 1.25rem;
        margin: -1.25rem;
        margin-top: 1rem;
        border-bottom-left-radius: 0.375rem;
        border-bottom-right-radius: 0.375rem;
    }

    .vehicle-form h6 {
        color: var(--theme-navy);
        border-bottom: 1px solid var(--theme-border-light);
        padding-bottom: 10px;
    }

    /* 6. “Edit” Button */
    .btn-outline-primary {
        color: var(--theme-navy);
        border-color: var(--theme-navy);
    }
    .btn-outline-primary:hover {
        background-color: var(--theme-navy);
        color: #ffffff;
    }
</style>

<div class="card shadow-sm mb-4">
    <div class="card-header d-flex justify-content-between align-items-center flex-wrap">
        <button type="button" class="btn btn-primary" id="btnAddNewVehicle">
            <i class="bi bi-plus-circle"></i> Add New Vehicle
        </button>
    </div>

    <div class="card-body">

        <c:if test="${empty vehicles}">
            <p class="text-center text-muted mb-0">This customer has no vehicles.</p>
        </c:if>
        <a>Total Vehicles: ${totalVehicles}</a>
        <c:forEach var="vehicle" items="${vehicles}">
            <%-- Determine brandId (unchanged) --%>
            <c:set var="currentBrandId" value="0"/>
            <c:forEach var="brand" items="${brands}">
                <c:if test="${brand.brandName == vehicle.brand}">
                    <c:set var="currentBrandId" value="${brand.brandId}"/>
                </c:if>
            </c:forEach>

            <div class="card mb-3 vehicle-card" id="vehicle-card-${vehicle.vehicleID}">
                <div class="card-body">
                    <input type="hidden" class="vehicle-id" value="${vehicle.vehicleID}">
                    <input type="hidden" class="customer-id" value="${customer.customerId}">

                    <div class="vehicle-display">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="card-title mb-1" style="font-size: 1.1rem;">
                                    <i class="bi bi-bounding-box-circles me-1"></i>
                                    <span class="display-license-plate">${vehicle.licensePlate}</span>
                                </h6>
                                <p class="card-subtitle mb-2 text-muted">
                                    <span class="display-brand">${vehicle.brand}</span> -
                                    <span class="display-model">${vehicle.model}</span>
                                </p>
                            </div>
                            <div class="vehicle-controls-edit">
                                <button type="button" class="btn btn-outline-primary btn-sm btn-edit"
                                        data-vehicle-id="${vehicle.vehicleID}">
                                    <i class="bi bi-pencil-fill"></i> Edit
                                </button>
                            </div>
                        </div>
                        <p class="mb-0" style="font-size: 0.9rem;">
                            <strong>Year of Manufacture:</strong>
                            <span class="display-year">${vehicle.yearManufacture}</span>
                        </p>
                    </div>

                    <div class="vehicle-form" style="display:none;">
                        <h6 class="mb-3">Edit Vehicle Information</h6>
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
                                <select class="form-select vehicle-field vehicle-brand">
                                    <option value="">-- Select Brand --</option>
                                    <c:forEach var="brand" items="${brands}">
                                        <option value="${brand.brandId}" ${brand.brandId == currentBrandId ? 'selected' : ''}>
                                                ${brand.brandName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Model</label>
                                <select class="form-select vehicle-field vehicle-model">
                                    <option value="${vehicle.model}" selected>${vehicle.model}</option>
                                </select>
                            </div>
                        </div>

                        <div class="text-end vehicle-controls-save">
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

<!-- Modal: Add Vehicle -->
<div class="modal fade" id="addVehicleModal" tabindex="-1" aria-labelledby="addVehicleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <form id="addVehicleForm">
                <div class="modal-header">
                    <h5 class="modal-title" id="addVehicleModalLabel">Add New Vehicle</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="customerId" value="${customer.customerId}">

                    <div class="mb-3">
                        <label class="form-label">License Plate</label>
                        <input type="text" name="licensePlate" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Year of Manufacture</label>
                        <input type="number" name="yearManufacture" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Brand</label>
                        <select name="brandId" id="addBrandSelect" class="form-select" required>
                            <option value="">-- Select Brand --</option>
                            <c:forEach var="brand" items="${brands}">
                                <option value="${brand.brandId}">${brand.brandName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Model</label>
                        <select name="modelName" id="addModelSelect" class="form-select" required>
                            <option value="">-- Select Model --</option>
                        </select>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Add Vehicle</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    (function () {
        'use strict';
        const contextPath = "${pageContext.request.contextPath}";
        const originalVehicleValues = {};

        // ==============================
        // MODAL HANDLING
        // ==============================
        document.getElementById('btnAddNewVehicle').addEventListener('click', () => {
            const form = document.getElementById('addVehicleForm');
            form.reset();
            const modelSelect = document.getElementById('addModelSelect');
            modelSelect.innerHTML = "<option value=''>-- Select Model --</option>";
            new bootstrap.Modal(document.getElementById('addVehicleModal')).show();
        });

        // Load models when selecting brand
        document.getElementById('addBrandSelect').addEventListener('change', function() {
            const brandId = this.value;
            const modelSelect = document.getElementById('addModelSelect');
            if (!brandId) {
                modelSelect.innerHTML = "<option value=''>-- Select Brand First --</option>";
                return;
            }
            modelSelect.innerHTML = "<option>Loading...</option>";
            fetch(contextPath + '/customerservice/addVehicle?action=getModels&brandId=' + encodeURIComponent(brandId))
                .then(res => res.json())
                .then(models => {
                    modelSelect.innerHTML = "<option value=''>-- Select Model --</option>";
                    models.forEach(m => {
                        const op = document.createElement("option");
                        op.value = m.name;
                        op.textContent = m.name;
                        modelSelect.appendChild(op);
                    });
                })
                .catch(err => {
                    modelSelect.innerHTML = "<option>Error loading</option>";
                    console.error(err);
                });
        });

        // Submit add vehicle form
        document.getElementById('addVehicleForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new URLSearchParams(new FormData(this));
            formData.append('action', 'saveVehicle');
            fetch(contextPath + '/customerservice/addVehicle', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: formData.toString()
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert('Vehicle added successfully!');
                        window.location.reload();
                    } else {
                        alert('Error: ' + (data.message || 'Unknown error'));
                    }
                })
                .catch(err => alert('System error: ' + err.message));
        });

        // =================================================
        // EDIT / SAVE / CANCEL HANDLING
        // =================================================

        function loadModelsForEdit(brandSelect, modelSelect, selectedModelName) {
            const brandId = brandSelect.value;
            if (!brandId) {
                modelSelect.innerHTML = "<option value=''>-- Select Brand First --</option>";
                modelSelect.disabled = true;
                return;
            }
            modelSelect.disabled = false;
            modelSelect.innerHTML = "<option>Loading...</option>";
            fetch(contextPath + '/customerservice/addVehicle?action=getModels&brandId=' + encodeURIComponent(brandId))
                .then(res => res.json())
                .then(models => {
                    modelSelect.innerHTML = "<option value=''>-- Select Model --</option>";
                    models.forEach(m => {
                        const op = document.createElement('option');
                        op.value = m.name;
                        op.textContent = m.name;
                        if (selectedModelName && selectedModelName === m.name) {
                            op.selected = true;
                        }
                        modelSelect.appendChild(op);
                    });
                });
        }

        function enableVehicleEdit(vehicleID) {
            const card = document.getElementById('vehicle-card-' + vehicleID);
            const displayView = card.querySelector('.vehicle-display');
            const formView = card.querySelector('.vehicle-form');

            originalVehicleValues[vehicleID] = {};
            formView.querySelectorAll('.vehicle-field').forEach(f => {
                const fieldKey = Array.from(f.classList).find(c => c.startsWith('vehicle-') && c !== 'vehicle-field');
                originalVehicleValues[vehicleID][fieldKey] = f.value;
            });

            const brandSelect = formView.querySelector('.vehicle-brand');
            const modelSelect = formView.querySelector('.vehicle-model');
            const currentModelName = originalVehicleValues[vehicleID]['vehicle-model'];

            loadModelsForEdit(brandSelect, modelSelect, currentModelName);

            const newBrandSelect = brandSelect.cloneNode(true);
            brandSelect.parentNode.replaceChild(newBrandSelect, brandSelect);
            newBrandSelect.value = originalVehicleValues[vehicleID]['vehicle-brand'];
            newBrandSelect.addEventListener('change', () => {
                loadModelsForEdit(newBrandSelect, modelSelect, null);
            });

            displayView.style.display = 'none';
            formView.style.display = 'block';
        }

        function cancelVehicleEdit(vehicleID) {
            const card = document.getElementById('vehicle-card-' + vehicleID);
            const displayView = card.querySelector('.vehicle-display');
            const formView = card.querySelector('.vehicle-form');
            const values = originalVehicleValues[vehicleID];

            if (values) {
                formView.querySelectorAll('.vehicle-field').forEach(f => {
                    const fieldKey = Array.from(f.classList).find(c => c.startsWith('vehicle-') && c !== 'vehicle-field');
                    if (values[fieldKey]) {
                        f.value = values[fieldKey];
                    }
                });

                const modelSelect = formView.querySelector('.vehicle-model');
                modelSelect.innerHTML = `<option value="${values['vehicle-model']}" selected>${values['vehicle-model']}</option>`;
            }

            formView.style.display = 'none';
            displayView.style.display = 'block';
        }

        function saveVehicleEdit(vehicleID) {
            const card = document.getElementById('vehicle-card-' + vehicleID);
            const formView = card.querySelector('.vehicle-form');

            const data = new URLSearchParams({
                action: 'updateVehicle',
                vehicleId: vehicleID,
                customerId: card.querySelector('.customer-id').value,
                licensePlate: formView.querySelector('.vehicle-license-plate').value.trim(),
                yearManufacture: formView.querySelector('.vehicle-year').value,
                brandId: formView.querySelector('.vehicle-brand').value,
                modelName: formView.querySelector('.vehicle-model').value
            });

            fetch(contextPath + '/customerservice/addVehicle', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: data.toString()
            })
                .then(res => res.json())
                .then(result => {
                    if (result.success) {
                        alert('Vehicle updated successfully!');
                        window.location.reload();
                    } else {
                        alert('Error: ' + (result.message || "Unknown error"));
                    }
                })
                .catch(err => alert('System error: ' + err.message));
        }

        document.querySelectorAll('.btn-edit').forEach(btn =>
            btn.addEventListener('click', () => enableVehicleEdit(btn.dataset.vehicleId))
        );
        document.querySelectorAll('.btn-save').forEach(btn =>
            btn.addEventListener('click', () => saveVehicleEdit(btn.dataset.vehicleId))
        );
        document.querySelectorAll('.btn-cancel').forEach(btn =>
            btn.addEventListener('click', () => cancelVehicleEdit(btn.dataset.vehicleId))
        );

    })();
</script>
