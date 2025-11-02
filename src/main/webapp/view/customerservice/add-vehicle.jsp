<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Hãng xe -->
<div>Customerid = </div>
<div class="mb-3">
    <label for="brandId" class="form-label">Hãng xe <span class="text-danger">*</span></label>
    <select id="brandId" name="brandId" class="form-select" required>
        <option value="">-- Chọn hãng --</option>
        <c:forEach var="brand" items="${brands}">
            <option value="${brand.brandId}">${brand.brandName}</option>
        </c:forEach>
    </select>
</div>

<!-- Model -->
<div class="mb-3">
    <label for="modelName" class="form-label">Model <span class="text-danger">*</span></label>
    <select id="modelName" name="modelName" class="form-select" required disabled>
        <option value="">-- Chọn hãng trước --</option>
    </select>
    <small class="text-muted">Vui lòng chọn hãng xe trước</small>
</div>

<!-- Biển số xe -->
<div class="mb-3">
    <label for="licensePlate" class="form-label">Biển số xe <span class="text-danger">*</span></label>
    <input type="text"
           id="licensePlate"
           name="licensePlate"
           class="form-control"
           required
           placeholder="VD: 30A-12345">
    <small class="text-muted">Định dạng: 30A-12345</small>
</div>

<!-- Năm sản xuất -->
<div class="mb-3">
    <label for="yearManufacture" class="form-label">Năm sản xuất <span class="text-danger">*</span></label>
    <input type="number"
           id="yearManufacture"
           name="yearManufacture"
           class="form-control"
           min="2000"
           max="2025"
           required
           placeholder="VD: 2023">
    <small class="text-muted">Từ năm 2000 đến nay</small>
</div>

<!-- Hidden customerId -->
<input type="hidden" id="modalCustomerId" name="customerId" value="${customer.customerId}">


<script>
    (function() {
        const brandSelect = document.getElementById("brandId");
        const modelSelect = document.getElementById("modelName");
        const modalCustomerIdInput = document.getElementById("modalCustomerId");

        if (!brandSelect || !modelSelect) return;

        // Xóa event listeners cũ bằng cách clone element
        const newBrandSelect = brandSelect.cloneNode(true);
        brandSelect.parentNode.replaceChild(newBrandSelect, brandSelect);

        // Event listener cho việc thay đổi brand
        newBrandSelect.addEventListener("change", function() {
            const brandId = this.value;

            if (brandId) {
                modelSelect.disabled = false;
                modelSelect.innerHTML = "<option value=''>-- Đang tải... --</option>";

                fetch(contextPath + "/customerservice/addVehicle?action=getModels&brandId=" + brandId)
                    .then(function(res) {
                        if (!res.ok) throw new Error('Network error');
                        return res.json();
                    })
                    .then(function(data) {
                        modelSelect.innerHTML = "<option value=''>-- Chọn model --</option>";

                        if (data.length === 0) {
                            modelSelect.innerHTML = "<option value=''>-- Không có model --</option>";
                        } else {
                            data.forEach(function(m) {
                                const opt = document.createElement("option");
                                opt.value = m.name;
                                opt.textContent = m.name;
                                modelSelect.appendChild(opt);
                            });
                        }
                    })
                    .catch(function(err) {
                        console.error('Error loading models:', err);
                        modelSelect.innerHTML = "<option value=''>-- Lỗi tải model --</option>";
                    });
            } else {
                modelSelect.disabled = true;
                modelSelect.innerHTML = "<option value=''>-- Chọn hãng trước --</option>";
            }
        });

        // Reset form khi modal đóng
        const modal = document.getElementById('addVehicleModal');
        if (modal) {
            modal.addEventListener('hidden.bs.modal', function () {
                const form = document.getElementById('addVehicleForm');
                if (form) {
                    form.reset();
                    modelSelect.disabled = true;
                    modelSelect.innerHTML = "<option value=''>-- Chọn hãng trước --</option>";
                }
            });

            // Set customerId khi modal mở
            modal.addEventListener('show.bs.modal', function () {
                if (typeof customerId !== 'undefined' && modalCustomerIdInput) {
                    modalCustomerIdInput.value = customerId;
                }
            });
        }
    })();
</script>