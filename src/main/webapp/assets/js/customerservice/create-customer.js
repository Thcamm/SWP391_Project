document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('createCustomerForm');
    const cancelBtn = document.getElementById('cancelBtn');

    const customerFields = {
        fullName: document.getElementById('fullName'),
        email: document.getElementById('email'),
        phone: document.getElementById('phone'),
        gender: document.getElementById('gender'),
        birthDate: document.getElementById('birthDate'),
        province: document.getElementById('province'),
        district: document.getElementById('district'),
        addressDetail: document.getElementById('addressDetail'),
        address: document.getElementById('address'),
    };

    // ========== INIT ==========
    function init() {
        form.addEventListener('submit', handleSubmit);
        cancelBtn.addEventListener('click', handleCancel);

        // Giới hạn ngày sinh tối đa là hôm nay
        const today = new Date().toISOString().split('T')[0];
        customerFields.birthDate.setAttribute('max', today);
    }

    // ========== DATA ==========
    function getCustomerData() {
        const province = customerFields.province.options[customerFields.province.selectedIndex]?.text || '';
        const district = customerFields.district.options[customerFields.district.selectedIndex]?.text || '';
        const detail = customerFields.addressDetail.value.trim();

        const fullAddress = [detail, district, province].filter(Boolean).join(', ');
        customerFields.address.value = fullAddress;

        return {
            fullName: customerFields.fullName.value.trim(),
            email: customerFields.email.value.trim(),
            phone: customerFields.phone.value.trim(),
            gender: customerFields.gender.value,
            birthDate: customerFields.birthDate.value,
            address: fullAddress,
        };
    }

    // ========== VALIDATION ==========
    function calculateAge(dateOfBirth) {
        const today = new Date();
        const birthDate = new Date(dateOfBirth);
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();

        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }

    function validateDateOfBirth(dateOfBirth) {
        if (!dateOfBirth) {
            return { valid: false, message: 'Please enter date of birth' };
        }

        const selectedDate = new Date(dateOfBirth);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate > today) {
            return { valid: false, message: 'Date of birth cannot be in the future' };
        }

        const age = calculateAge(dateOfBirth);
        if (age < 18) {
            return { valid: false, message: 'Customer must be at least 18 years old' };
        }

        return { valid: true };
    }

    function validateEmail(email) {
        const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return pattern.test(email);
    }

    function validatePhone(phone) {
        const pattern = /^(0|\+84)[0-9]{9}$/;
        return pattern.test(phone);
    }

    function validateForm() {
        const c = getCustomerData();

        if (!c.fullName) {
            alert('Please enter full name');
            return false;
        }

        if (!c.email && !c.phone) {
            alert('Please enter at least email or phone number');
            return false;
        }

        if (c.email && !validateEmail(c.email)) {
            alert('Invalid email format');
            return false;
        }

        if (c.phone && !validatePhone(c.phone)) {
            alert('Invalid phone number format');
            return false;
        }

        const dobCheck = validateDateOfBirth(c.birthDate);
        if (!dobCheck.valid) {
            alert(dobCheck.message);
            return false;
        }

        if (!customerFields.province.value) {
            alert('Please select province / city');
            return false;
        }

        if (!customerFields.district.value) {
            alert('Please select district');
            return false;
        }

        if (!customerFields.addressDetail.value.trim()) {
            alert('Please enter detailed address');
            return false;
        }

        return true;
    }

    // ========== HANDLERS ==========
    function handleSubmit(e) {
        e.preventDefault();
        if (!validateForm()) return;

        const payload = getCustomerData();
        console.log('Submitting:', payload);

        // Gửi form đến servlet (hành vi mặc định)
        form.submit();
    }

    function handleCancel() {
        if (confirm('Are you sure you want to cancel?')) {
            form.reset();
        }
    }

    // ========== EXECUTE ==========
    init();
});
