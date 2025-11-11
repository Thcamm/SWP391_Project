<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* Định nghĩa Bảng màu "Trang trọng & Nhã nhặn"
    */
    :root {
        --theme-navy: #2c3e50;      /* Một màu xanh navy đậm, chuyên nghiệp */
        --theme-soft-bg: #f8f9fa;  /* Màu nền xám/xanh rất nhạt (Bootstrap bg-light) */
        --theme-text-dark: #343a40; /* Màu văn bản chính */
        --theme-border-light: #e9ecef; /* Màu viền tinh tế */
    }

    /* 1. Tinh chỉnh Thẻ Wrapper chính
    */
    .card.shadow-sm {
        /* Dùng shadow nhẹ hơn thay vì mặc định */
        box-shadow: 0 .1rem .4rem rgba(0,0,0,0.05) !important;
        border: none; /* Bỏ viền thẻ mặc định */
    }

    /* 2. Header chính (chứa nút "Thêm xe mới")
    */
    .card-header {
        background-color: #ffffff; /* Nền trắng sạch sẽ */
        border-bottom: 1px solid var(--theme-border-light);
    }

    .card-header h5 {
        color: var(--theme-navy); /* Áp dụng màu navy cho tiêu đề */
    }

    /* 3. Thẻ xe riêng lẻ
    */
    .vehicle-card {
        border: 1px solid var(--theme-border-light);
        /* Thay thế viền xanh dương bằng viền navy tinh tế */
        border-left: 4px solid var(--theme-navy);
        transition: all 0.2s ease-in-out;
        box-shadow: none; /* Bắt đầu không có shadow */
    }
    .vehicle-card:hover {
        /* Hiệu ứng nhấc lên khi di chuột qua */
        transform: translateY(-2px);
        box-shadow: 0 .2rem .5rem rgba(0,0,0,0.06);
    }

    /* 4. Chế độ XEM (Read View)
    */
    .vehicle-display .card-title {
        color: var(--theme-navy); /* Màu navy cho Biển số xe */
        font-weight: 600; /* Làm nó đậm hơn một chút */
    }

    /* 5. Chế độ SỬA (Edit View)
    */
    .vehicle-form {
        background-color: var(--theme-soft-bg); /* Nền nhã nhặn cho form sửa */
        padding: 1.25rem; /* Thêm đệm */

        /* Kỹ thuật để làm form full-width bên trong card-body */
        margin: -1.25rem;
        margin-top: 1rem;

        /* Bo góc cho đẹp */
        border-bottom-left-radius: 0.375rem;
        border-bottom-right-radius: 0.375rem;
    }

    .vehicle-form h6 {
        color: var(--theme-navy); /* Tiêu đề "Chỉnh sửa" */
        border-bottom: 1px solid var(--theme-border-light);
        padding-bottom: 10px;
    }

    /* 6. Nút "Sửa" (Ghi đè màu primary)
    */
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
            <i class="bi bi-plus-circle"></i> Thêm xe mới
        </button>
    </div>

    <div class="card-body">

        <c:if test="${empty vehicles}">
            <p class="text-center text-muted mb-0">Khách hàng này chưa có xe nào.</p>
        </c:if>

        <c:forEach var="vehicle" items="${vehicles}">
            <%-- Tính toán brandId (Không đổi) --%>
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
                                    <i class="bi bi-pencil-fill"></i> Sửa
                                </button>
                            </div>
                        </div>
                        <p class="mb-0" style="font-size: 0.9rem;">
                            <strong>Năm sản xuất:</strong>
                            <span class="display-year">${vehicle.yearManufacture}</span>
                        </p>
                    </div>

                    <div class="vehicle-form" style="display:none;">
                        <h6 class="mb-3">Chỉnh sửa thông tin xe</h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Biển số xe</label>
                                <input type="text" class="form-control vehicle-field vehicle-license-plate"
                                       value="${vehicle.licensePlate}">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Năm sản xuất</label>
                                <input type="number" class="form-control vehicle-field vehicle-year"
                                       value="${vehicle.yearManufacture}">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Hãng xe</label>
                                <select class="form-select vehicle-field vehicle-brand">
                                    <option value="">-- Chọn hãng xe --</option>
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
                                    data-vehicle-id="${vehicle.vehicleID}">Hủy</button>
                            <button type="button" class="btn btn-success btn-sm btn-save"
                                    data-vehicle-id="${vehicle.vehicleID}">Lưu thay đổi</button>
                        </div>
                    </div>

                </div>
            </div>
        </c:forEach>

        <jsp:include page="/view/customerservice/pagination.jsp">
            <jsp:param name="totalPages" value="${totalPages}" />
            <jsp:param name="currentPage" value="${currentPage}" />
            <jsp:param name="baseUrl" value="/customerservice/customer-detail" />
            <jsp:param name="queryString" value="&id=${customer.customerId}" />
        </jsp:include>
    </div>
</div>

<div class="modal fade" id="addVehicleModal" tabindex="-1" aria-labelledby="addVehicleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <form id="addVehicleForm">
                <div class="modal-header">
                    <h5 class="modal-title" id="addVehicleModalLabel">Thêm xe mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="customerId" value="${customer.customerId}">

                    <div class="mb-3">
                        <label class="form-label">Biển số xe</label>
                        <input type="text" name="licensePlate" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Năm sản xuất</label>
                        <input type="number" name="yearManufacture" class="form-control" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Hãng xe</label>
                        <select name="brandId" id="addBrandSelect" class="form-select" required>
                            <option value="">-- Chọn hãng xe --</option>
                            <c:forEach var="brand" items="${brands}">
                                <option value="${brand.brandId}">${brand.brandName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Model</label>
                        <select name="modelName" id="addModelSelect" class="form-select" required>
                            <option value="">-- Chọn model --</option>
                        </select>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    <button type="submit" class="btn btn-primary">Thêm xe</button>
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

        // ===================================
        // XỬ LÝ MODAL (Giữ nguyên)
        // ===================================
        document.getElementById('btnAddNewVehicle').addEventListener('click', () => {
            // Đảm bảo form được reset mỗi khi mở
            const form = document.getElementById('addVehicleForm');
            form.reset();
            const modelSelect = document.getElementById('addModelSelect');
            modelSelect.innerHTML = "<option value=''>-- Chọn model --</option>";

            // Hiển thị modal
            new bootstrap.Modal(document.getElementById('addVehicleModal')).show();
        });

        // Load models khi chọn brand trong modal (Giữ nguyên)
        document.getElementById('addBrandSelect').addEventListener('change', function() {
            const brandId = this.value;
            const modelSelect = document.getElementById('addModelSelect');
            if (!brandId) {
                modelSelect.innerHTML = "<option value=''>-- Chọn hãng xe trước --</option>";
                return;
            }
            modelSelect.innerHTML = "<option>Đang tải...</option>";
            fetch(contextPath + '/customerservice/addVehicle?action=getModels&brandId=' + encodeURIComponent(brandId))
                .then(res => res.json())
                .then(models => {
                    modelSelect.innerHTML = "<option value=''>-- Chọn model --</option>";
                    models.forEach(m => {
                        const op = document.createElement("option");
                        op.value = m.name;
                        op.textContent = m.name;
                        modelSelect.appendChild(op);
                    });
                })
                .catch(err => {
                    modelSelect.innerHTML = "<option>Lỗi khi tải</option>";
                    console.error(err);
                });
        });

        // Submit form thêm xe (Giữ nguyên)
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
                        alert('Thêm xe thành công!');
                        window.location.reload();
                    } else {
                        alert('Lỗi: ' + (data.message || 'Không xác định'));
                    }
                })
                .catch(err => alert('Lỗi hệ thống: ' + err.message));
        });

        // =================================================
        // (CẬP NHẬT) XỬ LÝ EDIT / SAVE / CANCEL
        // =================================================

        /**
         * (CẬP NHẬT) Hàm load models cho form SỬA
         */
        function loadModelsForEdit(brandSelect, modelSelect, selectedModelName) {
            const brandId = brandSelect.value;
            if (!brandId) {
                modelSelect.innerHTML = "<option value=''>-- Chọn hãng xe trước --</option>";
                modelSelect.disabled = true;
                return;
            }
            modelSelect.disabled = false;
            modelSelect.innerHTML = "<option>Đang tải...</option>";
            fetch(contextPath + '/customerservice/addVehicle?action=getModels&brandId=' + encodeURIComponent(brandId))
                .then(res => res.json())
                .then(models => {
                    modelSelect.innerHTML = "<option value=''>-- Chọn model --</option>";
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

        /**
         * (CẬP NHẬT) Bật chế độ SỬA
         */
        function enableVehicleEdit(vehicleID) {
            const card = document.getElementById('vehicle-card-' + vehicleID);
            const displayView = card.querySelector('.vehicle-display');
            const formView = card.querySelector('.vehicle-form');

            // 1. Lưu trữ giá trị gốc *trước khi* người dùng thay đổi
            originalVehicleValues[vehicleID] = {};
            formView.querySelectorAll('.vehicle-field').forEach(f => {
                const fieldKey = Array.from(f.classList).find(c => c.startsWith('vehicle-') && c !== 'vehicle-field');
                originalVehicleValues[vehicleID][fieldKey] = f.value;
            });

            // 2. Tải Model cho form (dựa trên Brand đã chọn)
            const brandSelect = formView.querySelector('.vehicle-brand');
            const modelSelect = formView.querySelector('.vehicle-model');
            const currentModelName = originalVehicleValues[vehicleID]['vehicle-model'];

            loadModelsForEdit(brandSelect, modelSelect, currentModelName);

            // 3. Thêm sự kiện 'change' cho BrandSelect *chỉ* trong form này
            const newBrandSelect = brandSelect.cloneNode(true);
            brandSelect.parentNode.replaceChild(newBrandSelect, brandSelect);
            newBrandSelect.value = originalVehicleValues[vehicleID]['vehicle-brand'];
            newBrandSelect.addEventListener('change', () => {
                loadModelsForEdit(newBrandSelect, modelSelect, null);
            });

            // 4. Ẩn view "Xem", Hiện view "Sửa"
            displayView.style.display = 'none';
            formView.style.display = 'block';
        }

        /**
         * (CẬP NHẬT) Hủy bỏ SỬA
         */
        function cancelVehicleEdit(vehicleID) {
            const card = document.getElementById('vehicle-card-' + vehicleID);
            const displayView = card.querySelector('.vehicle-display');
            const formView = card.querySelector('.vehicle-form');
            const values = originalVehicleValues[vehicleID];

            // 1. Khôi phục giá trị gốc cho các trường trong form
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

            // 2. Ẩn view "Sửa", Hiện view "Xem"
            formView.style.display = 'none';
            displayView.style.display = 'block';
        }

        /**
         * (CẬP NHẬT) Lưu thay đổi
         */
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
                        alert('Cập nhật thành công!');
                        window.location.reload();
                    } else {
                        alert('Lỗi: ' + (result.message || "Không xác định"));
                    }
                })
                .catch(err => alert('Lỗi hệ thống: ' + err.message));
        }

        /**
         * (CẬP NHẬT) Gán sự kiện
         */
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