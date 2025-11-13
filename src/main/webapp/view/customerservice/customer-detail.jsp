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

        /* 2. Card Section thay thế Accordion */
        .section-card {
            border: 1px solid var(--theme-border-light);
            border-radius: 0.375rem;
            margin-bottom: 1.5rem;
            background-color: #ffffff;
            box-shadow: 0 .1rem .4rem rgba(0,0,0,0.05);
        }
        .section-header {
            background-color: var(--theme-soft-bg);
            padding: 1rem 1.25rem;
            border-bottom: 1px solid var(--theme-border-light);
            border-top-left-radius: 0.375rem;
            border-top-right-radius: 0.375rem;
        }
        .section-header h5 {
            color: var(--theme-navy);
            font-weight: 600;
            margin: 0;
        }
        .section-body {
            padding: 1.25rem;
        }

        /* 3. Chế độ XEM cho Thông tin Khách hàng */
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
            color: #5a6a7b;
        }

        /* 4. Kiểu cho Thẻ Xe */
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
            background-color: #1e2b37;
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

                        <!-- 1. Customer Information Section -->
                        <div class="section-card">
                            <div class="section-header">
                                <h5>1. Customer Information</h5>
                            </div>
                            <div class="section-body">
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
                                        <div class="data-item">
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
                                            <button type="submit" id="saveBtn" class="btn btn-success">Save Changes</button>
                                            <button type="button" id="cancelBtn" class="btn btn-secondary" onclick="cancelEdit()">Cancel</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- 2. Vehicle Information Section -->
                        <div class="section-card">
                            <div class="section-header">
                                <h5>2. Vehicle Information</h5>
                            </div>
                            <div class="section-body">
                                <jsp:include page="edit-vehicle.jsp" />
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
        document.querySelectorAll('.editable').forEach(field => {
            originalValues[field.id] = field.value;
        });

        document.getElementById('customerDisplay').style.display = 'none';
        document.getElementById('customerFormWrapper').style.display = 'block';

        const newPasswordField = document.getElementById('newPassword');
        if (newPasswordField) newPasswordField.value = '';

        const fullAddressInput = document.getElementById("address");
        const addressDetailTextarea = document.getElementById("addressDetail");
        if (fullAddressInput && fullAddressInput.value) {
            const parts = fullAddressInput.value.split(',');
            if (parts.length >= 3) {
                const provinceName = parts[parts.length - 1].trim();
                const districtName = parts[parts.length - 2].trim();
                const detail = parts.slice(0, parts.length - 2).join(',').trim();
                addressDetailTextarea.value = detail;
                autoSelectAddress(provinceName, districtName);
            } else {
                addressDetailTextarea.value = fullAddressInput.value;
            }
        }
    }

    function cancelEdit() {
        document.querySelectorAll('.editable').forEach(field => {
            field.value = originalValues[field.id] || '';
        });

        document.getElementById('customerDisplay').style.display = 'block';
        document.getElementById('customerFormWrapper').style.display = 'none';
    }

    document.addEventListener("DOMContentLoaded", function () {
        const fullAddressInput = document.getElementById("address");
        const fullAddress = fullAddressInput ? fullAddressInput.value : null;
        const addressDetailTextarea = document.getElementById("addressDetail");
        const provinceSelect = document.getElementById("province");
        const districtSelect = document.getElementById("district");

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

        document.getElementById("customerDetailForm").addEventListener("submit", function () {
            const provinceText = provinceSelect.options[provinceSelect.selectedIndex].text;
            const districtText = districtSelect.options[districtSelect.selectedIndex].text;
            const detailText = addressDetailTextarea.value.trim();
            const finalProvince = (provinceText === "Select province / city" || provinceText === "") ? "" : provinceText;
            const finalDistrict = (districtText === "Select District" || districtText === "") ? "" : districtText;
            fullAddressInput.value = `${detailText}, ${finalDistrict}, ${finalProvince}`;
            const passwordField = document.getElementById("newPassword");
            const password = passwordField.value.trim();
            if (password && password.length < 8) {
                e.preventDefault();
                alert("Password must be at least 8 characters");
                passwordField.focus();
            }

            document.getElementById('displayFullName').textContent = document.getElementById('fullName').value;
            document.getElementById('displayEmail').textContent = document.getElementById('email').value;
            document.getElementById('displayPhone').textContent = document.getElementById('phone').value;
            document.getElementById('displayAddress').textContent = fullAddressInput.value;
        });
    });

    async function autoSelectAddress(provinceName, districtName) {
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
    const contextPath = "${pageContext.request.contextPath}";
    window.customerId = "${customer.customerId}";
    console.log("✅ ContextPath:", contextPath);
    console.log("✅ Customer ID:", window.customerId);
</script>
</body>
</html>