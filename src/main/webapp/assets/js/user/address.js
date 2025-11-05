document.addEventListener("DOMContentLoaded", function () {
    // --- Lấy các phần tử cần thiết ---
    const form = document.getElementById("createCustomerForm");
    const cancelBtn = document.getElementById("cancelBtn");

    const customerFields = {
        fullName: document.getElementById("fullName"),
        email: document.getElementById("email"),
        phone: document.getElementById("phone"),
        gender: document.getElementById("gender"),
        dateOfBirth: document.getElementById("dateOfBirth"),
        province: document.getElementById("province"),
        district: document.getElementById("district"),
        addressDetail: document.getElementById("addressDetail"),
        address: document.getElementById("address") // hidden input chứa địa chỉ đầy đủ
    };

    // --- Lấy dữ liệu khách hàng ---
    function getCustomerData() {
        return {
            fullName: customerFields.fullName.value.trim(),
            email: customerFields.email.value.trim(),
            phone: customerFields.phone.value.trim(),
            gender: customerFields.gender.value,
            dateOfBirth: customerFields.dateOfBirth.value,
            address: customerFields.address.value.trim(), // địa chỉ đầy đủ
            province: customerFields.province.value,
            district: customerFields.district.value,
            addressDetail: customerFields.addressDetail.value.trim()
        };
    }

    // --- Validate form ---
    function validateForm() {
        const c = getCustomerData();

        if (!c.fullName) {
            alert("Vui lòng nhập họ tên");
            return false;
        }

        if (!c.email) {
            alert("Vui lòng nhập email");
            return false;
        }

        if (!c.phone) {
            alert("Vui lòng nhập số điện thoại");
            return false;
        }

        if (!c.province) {
            alert("Vui lòng chọn tỉnh / thành phố");
            return false;
        }

        if (!c.district) {
            alert("Vui lòng chọn quận / huyện");
            return false;
        }

        if (!c.addressDetail) {
            alert("Vui lòng nhập địa chỉ chi tiết");
            return false;
        }

        return true;
    }

    // --- Xử lý khi nhấn Submit ---
    function handleSubmit(e) {
        e.preventDefault();
        if (!validateForm()) return;

        const payload = getCustomerData();
        console.log("Submitting:", payload);

        // Gửi form thật sự đến server
        form.submit();

        // Nếu muốn chỉ test mà không reload trang:
        // alert("Tạo khách hàng thành công!");
    }

    // --- Xử lý khi nhấn Hủy ---
    function handleCancel() {
        if (confirm("Bạn có chắc chắn muốn hủy?")) {
            form.reset();
        }
    }

    // --- Gán sự kiện ---
    if (form) form.addEventListener("submit", handleSubmit);
    if (cancelBtn) cancelBtn.addEventListener("click", handleCancel);
});
