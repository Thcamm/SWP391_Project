const host = "https://provinces.open-api.vn/api/";
const provinceDropdown = document.getElementById("province");
const districtDropdown = document.getElementById("district");

// Hàm gọi API và render dropdown Tỉnh/Thành phố
function renderProvinces() {
    fetch(host + "?depth=2")
        .then(response => response.json())
        .then(data => {
            window.provincesData = data;

            for (const province of data) {
                const option = document.createElement("option");
                option.value = province.name; // ✅ Lưu NAME thay vì code
                option.textContent = province.name;
                option.dataset.code = province.code; // Giữ code nếu cần
                provinceDropdown.appendChild(option);
            }
        })
        .catch(error => console.error("Lỗi khi tải danh sách tỉnh thành:", error));
}

// Sự kiện khi người dùng thay đổi Tỉnh/Thành phố
provinceDropdown.addEventListener("change", function() {
    districtDropdown.innerHTML = '<option selected disabled value="">District</option>';

    const selectedProvinceName = this.value;
    // Tìm province theo name
    const selectedProvince = window.provincesData.find(p => p.name === selectedProvinceName);

    if (selectedProvince && selectedProvince.districts) {
        for (const district of selectedProvince.districts) {
            const option = document.createElement("option");
            option.value = district.name; // ✅ Lưu NAME thay vì code
            option.textContent = district.name;
            option.dataset.code = district.code; // Giữ code nếu cần
            districtDropdown.appendChild(option);
        }
    }

    updateFullAddress();
});

// Sự kiện khi chọn Quận/Huyện
districtDropdown.addEventListener("change", updateFullAddress);

// Sự kiện khi nhập địa chỉ chi tiết
document.getElementById("addressDetail")?.addEventListener("input", updateFullAddress);

// Hàm cập nhật địa chỉ đầy đủ
function updateFullAddress() {
    const addressDetail = document.getElementById("addressDetail")?.value.trim() || "";
    const districtName = districtDropdown.value || "";
    const provinceName = provinceDropdown.value || "";

    const fullAddress = [addressDetail, districtName, provinceName]
        .filter(part => part)
        .join(", ");

    document.getElementById("address").value = fullAddress;
}

// Khởi tạo
renderProvinces();