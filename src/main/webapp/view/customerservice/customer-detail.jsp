<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Customer Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
            /* 1. Bảng màu "Trang trọng & Nhã nhặn" */
        :root {
            --theme-navy: #2c3e50;      /* Xanh navy đậm */
            --theme-soft-bg: #f8f9fa;  /* Nền xám/xanh rất nhạt */
            --theme-text-dark: #343a40;
            --theme-border-light: #e9ecef;
        }

        /* 2. Tinh chỉnh Accordion (MỚI) */
        .accordion-item {
            border: 1px solid var(--theme-border-light);
            border-radius: 0.375rem !important; /* Đảm bảo bo góc */
        }
        .accordion-header {
            border-bottom: 1px solid var(--theme-border-light);
        }
        .accordion-button {
            color: var(--theme-navy);
            font-weight: 600;
            background-color: #ffffff;
        }
        .accordion-button:not(.collapsed) {
            background-color: var(--theme-soft-bg);
            color: var(--theme-navy);
            box-shadow: none;
        }
        .accordion-button:focus {
            box-shadow: none;
            border-color: transparent;
        }
        .accordion-button::after {
            /* Thay thế icon mặc định bằng icon Bootstrap (tùy chọn) */
            background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%232c3e50'%3e%3cpath fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/%3e%3c/svg%3e");
        }

        /* 3. Chế độ XEM cho Thông tin Khách hàng (MỚI) */
        .customer-display-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1rem 1.5rem;
        }
        .customer-display-grid .data-item {
            margin-bottom: 0.5rem;
        }
        .customer-display-grid .data-label {
            font-weight: 500;
            color: var(--theme-text-dark);
            display: block;
            font-size: 0.9rem;
        }
        .customer-display-grid .data-value {
            font-size: 1rem;
            color: #5a6a7b; /* Một màu xám-xanh nhẹ */
        }

        /* 4. Kiểu cho Thẻ Xe (Từ lần trước) */
        .card.shadow-sm {
            box-shadow: 0 .1rem .4rem rgba(0,0,0,0.05) !important;
            border: none;
        }
        .card-header h5 {
            color: var(--theme-navy);
        }
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
        .vehicle-display .card-title {
            color: var(--theme-navy);
            font-weight: 600;
        }
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
        .btn-outline-primary {
            color: var(--theme-navy);
            border-color: var(--theme-navy);
        }
        .btn-outline-primary:hover {
            background-color: var(--theme-navy);
            color: #ffffff;
        }

        /* Ghi đè màu primary cho nút */
        .btn-primary {
            background-color: var(--theme-navy);
            border-color: var(--theme-navy);
        }
        .btn-primary:hover {
            background-color: #1e2b37; /* Màu navy tối hơn một chút */
            border-color: #1e2b37;
        }
    </style>
</head>
<body>
<jsp:include page="/view/customerservice/result.jsp" />
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding:0rem 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           ">
                    <div class="container mt-3 mb-3">
                        <h2 class="mb-4">Customer Detail Management</h2>



                        <div class="accordion" id="customerDetailAccordion">

                            <!-- Customer Information -->
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="headingOne">
                                    <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                            data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                        <strong>1. Customer Information</strong>
                                    </button>
                                </h2>
                                <div id="collapseOne" class="accordion-collapse collapse show" aria-labelledby="headingOne"
                                     data-bs-parent="#customerDetailAccordion">
                                    <div class="accordion-body">

                                        <div id="customerDisplay">
                                            <div class="customer-display-grid">
                                                <div class="data-item">
                                                    <span class="data-label">Full Name</span>
                                                    <span class="data-value" id="displayFullName">${customer.fullName}</span>
                                                </div>
                                                <div class="data-item">
                                                    <span class="data-label">Email</span>
                                                    <span class="data-value" id="displayEmail">${customer.email}</span>
                                                </div>
                                                <div class="data-item">
                                                    <span class="data-label">Phone</span>
                                                    <span class="data-value" id="displayPhone">${customer.phoneNumber}</span>
                                                </div>
                                                <div class="data-item">
                                                    <span class="data-label">Gender</span>
                                                    <span class="data-value" id="displayGender">
                            <c:choose>
                                <c:when test="${customer.gender == 'male'}">Male</c:when>
                                <c:when test="${customer.gender == 'female'}">Female</c:when>
                                <c:when test="${customer.gender == 'other'}">Other</c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </span>
                                                </div>
                                                <div class="data-item"> <%-- Trải dài toàn bộ --%>
                                                    <span class="data-label">Address</span>
                                                    <span class="data-value" id="displayAddress">${customer.address}</span>
                                                </div>
                                            </div>
                                            <div class="text-end mt-4">
                                                <button type="button" id="editBtn" class="btn btn-primary" onclick="enableEdit()">
                                                    <i class="bi bi-pencil-fill me-1"></i> Edit Customer
                                                </button>
                                            </div>
                                        </div>

                                        <div id="customerFormWrapper" style="display:none;">
                                            <form id="customerDetailForm" method="post" action="${pageContext.request.contextPath}/customerservice/customer-detail">
                                                <input type="hidden" name="customerId" value="${customer.customerId}"/>

                                                <%-- Các trường form của bạn, nhưng gỡ bỏ readonly/disabled --%>
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label for="fullName" class="form-label">Full Name</label>
                                                        <input type="text" id="fullName" name="FullName" value="${customer.fullName}"
                                                               class="form-control editable"/>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label for="email" class="form-label">Email</label>
                                                        <input type="email" id="email" name="Email" value="${customer.email}"
                                                               class="form-control editable"/>
                                                    </div>
                                                </div>

                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label for="phone" class="form-label">Phone</label>
                                                        <input type="text" id="phone" name="PhoneNumber" value="${customer.phoneNumber}"
                                                               class="form-control editable"/>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label for="gender" class="form-label">Gender</label>
                                                        <select id="gender" name="Gender" class="form-select editable">
                                                            <option value="">--- Select Gender ---</option>
                                                            <option value="male" ${customer.gender == 'male' ? 'selected' : ''}>Male</option>
                                                            <option value="female" ${customer.gender == 'female' ? 'selected' : ''}>Female</option>
                                                            <option value="other" ${customer.gender == 'other' ? 'selected' : ''}>Other</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <hr class="my-3">
                                                <h5 class="mb-3">Address Information</h5>
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label for="province" class="form-label">Province / City <span class="text-danger">*</span></label>
                                                        <select id="province" name="province" class="form-select editable" required>
                                                            <option selected disabled value="">Select province / city</option>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label for="district" class="form-label">District <span class="text-danger">*</span></label>
                                                        <select id="district" name="district" class="form-select editable" required>
                                                            <option selected disabled value="">Select District</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div class="mb-3">
                                                    <label for="addressDetail" class="form-label">Detail Address <span class="text-danger">*</span></label>
                                                    <textarea id="addressDetail" name="addressDetail" class="form-control editable" rows="2"
                                                              placeholder="House number, street name..." required></textarea>
                                                </div>

                                                <input type="hidden" id="address" name="address" value="${customer.address}"/>
                                                <script src="${pageContext.request.contextPath}/assets/js/user/address.js"></script>

                                                <hr class="my-3">
                                                <div class="mb-3">
                                                    <label for="newPassword" class="form-label">New Password (Optional)</label>
                                                    <input type="password" id="newPassword" name="NewPassword" class="form-control editable"
                                                           placeholder="Enter new password if you want to change"/>
                                                </div>

                                                <div class="mb-3 text-end">
                                                    <%-- Gỡ bỏ style="display:none" --%>
                                                    <button type="submit" id="saveBtn" class="btn btn-success">Save Changes</button>
                                                    <button type="button" id="cancelBtn" class="btn btn-secondary" onclick="cancelEdit()">Cancel</button>
                                                </div>
                                            </form>
                                        </div>

                                    </div>
                                </div>
                            </div>

                            <!-- Vehicle Information -->
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="headingTwo">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                            data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                        <strong>2. Vehicle Information</strong>
                                    </button>
                                </h2>
                                <div id="collapseTwo" class="accordion-collapse collapse" aria-labelledby="headingTwo"
                                     data-bs-parent="#customerDetailAccordion">
                                    <div class="accordion-body">

                                        <jsp:include page="edit-vehicle.jsp" />

                                    </div>
                                </div>
                            </div>

                            <!-- Service Order Information -->
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="headingThree">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                            data-bs-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
                                        <strong>3. Service Order Information</strong>
                                    </button>
                                </h2>
                                <div id="collapseThree" class="accordion-collapse collapse" aria-labelledby="headingThree"
                                     data-bs-parent="#customerDetailAccordion">
                                    <div class="accordion-body">
                                        <jsp:include page="repair-history.jsp" />
                                    </div>
                                </div>
                            </div>

                        </div>

                    </div>

                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
        crossorigin="anonymous"></script>

<script>
    let originalValues = {};

    function enableEdit() {
        // 1. Lưu trữ giá trị gốc (quan trọng cho việc Hủy)
        document.querySelectorAll('.editable').forEach(field => {
            originalValues[field.id] = field.value;
        });

        // 2. Ẩn "Chế độ XEM", Hiện "Chế độ SỬA"
        document.getElementById('customerDisplay').style.display = 'none';
        document.getElementById('customerFormWrapper').style.display = 'block';

        // 3. Xóa password mới (đã có trong code của bạn, giữ lại)
        const newPasswordField = document.getElementById('newPassword');
        if (newPasswordField) newPasswordField.value = '';

        // 4. (QUAN TRỌNG) Kích hoạt lại việc điền địa chỉ
        // Đảm bảo các dropdown địa chỉ được điền đúng khi form hiện ra
        const fullAddressInput = document.getElementById("address");
        const addressDetailTextarea = document.getElementById("addressDetail");
        if (fullAddressInput && fullAddressInput.value) {
            const parts = fullAddressInput.value.split(',');
            if (parts.length >= 3) {
                const provinceName = parts[parts.length - 1].trim();
                const districtName = parts[parts.length - 2].trim();
                const detail = parts.slice(0, parts.length - 2).join(',').trim();
                addressDetailTextarea.value = detail;
                autoSelectAddress(provinceName, districtName); // Gọi lại hàm auto-select
            } else {
                addressDetailTextarea.value = fullAddressInput.value;
            }
        }
    }

    function cancelEdit() {
        // 1. Khôi phục các giá trị gốc vào form
        document.querySelectorAll('.editable').forEach(field => {
            field.value = originalValues[field.id] || '';
        });

        // 2. Ẩn "Chế độ SỬA", Hiện "Chế độ XEM"
        document.getElementById('customerDisplay').style.display = 'block';
        document.getElementById('customerFormWrapper').style.display = 'none';
    }

    // === Address Handling ===
    // (Toàn bộ phần code còn lại của bạn giữ nguyên)
    document.addEventListener("DOMContentLoaded", function () {
        const fullAddressInput = document.getElementById("address");
        const fullAddress = fullAddressInput ? fullAddressInput.value : null;
        const addressDetailTextarea = document.getElementById("addressDetail");
        const provinceSelect = document.getElementById("province");
        const districtSelect = document.getElementById("district");

        // Ghi chú: Logic này giờ sẽ chạy trên các trường form BỊ ẨN, điều này là OK.
        if (fullAddress && fullAddress.trim() !== "") {
            const parts = fullAddress.split(',');
            if (parts.length >= 3) {
                const provinceName = parts[parts.length - 1].trim();
                const districtName = parts[parts.length - 2].trim();
                const detail = parts.slice(0, parts.length - 2).join(',').trim();
                addressDetailTextarea.value = detail;
                autoSelectAddress(provinceName, districtName);
            } else {
                addressDetailTextarea.value = fullAddress;
                console.warn("Định dạng địa chỉ không đúng.");
            }
        }

        // Logic submit của bạn vẫn hoàn toàn chính xác
        document.getElementById("customerDetailForm").addEventListener("submit", function () {
            // (Không cần kiểm tra saveBtn vì form này chỉ submit khi ở chế độ Sửa)
            const provinceText = provinceSelect.options[provinceSelect.selectedIndex].text;
            const districtText = districtSelect.options[districtSelect.selectedIndex].text;
            const detailText = addressDetailTextarea.value.trim();
            const finalProvince = (provinceText === "Select province / city" || provinceText === "") ? "" : provinceText;
            const finalDistrict = (districtText === "Select District" || districtText === "") ? "" : districtText;
            fullAddressInput.value = `${detailText}, ${finalDistrict}, ${finalProvince}`;

            // (MỚI) Cập nhật lại "Chế độ XEM" trước khi submit (để có UX tốt hơn)
            document.getElementById('displayFullName').textContent = document.getElementById('fullName').value;
            document.getElementById('displayEmail').textContent = document.getElementById('email').value;
            document.getElementById('displayPhone').textContent = document.getElementById('phone').value;
            document.getElementById('displayAddress').textContent = fullAddressInput.value;
            // (bạn có thể thêm cho Gender)
        });
    });

    async function autoSelectAddress(provinceName, districtName) {
        // (Hàm này giữ nguyên, không thay đổi)
        const provinceSelect = document.getElementById("province");
        const districtSelect = document.getElementById("district");

        const poll = (selector, condition) => new Promise(resolve => {
            const interval = setInterval(() => {
                if (condition(selector)) { clearInterval(interval); resolve(); }
            }, 100);
        });

        try {
            await poll(provinceSelect, el => el.options.length > 1);
            let provinceFound = false;
            for (let i = 0; i < provinceSelect.options.length; i++) {
                if (provinceSelect.options[i].text.toLowerCase() === provinceName.toLowerCase()) {
                    provinceSelect.value = provinceSelect.options[i].value;
                    provinceFound = true;
                    break;
                }
            }
            if (provinceFound) {
                provinceSelect.dispatchEvent(new Event('change'));
                await poll(districtSelect, el => el.options.length > 1);
                for (let i = 0; i < districtSelect.options.length; i++) {
                    if (districtSelect.options[i].text.toLowerCase() === districtName.toLowerCase()) {
                        districtSelect.value = districtSelect.options[i].value;
                        break;
                    }
                }
            }
        } catch (error) {
            console.error("Lỗi khi tự động chọn địa chỉ:", error);
        }
    }
</script>
<script>
    // Định nghĩa contextPath và customerId TRƯỚC KHI load add-vehicle.js
    const contextPath = "${pageContext.request.contextPath}";
    window.customerId = "${customer.customerId}";
    console.log("✅ ContextPath:", contextPath);
    console.log("✅ Customer ID:", window.customerId);
</script>
</body>
</html>
