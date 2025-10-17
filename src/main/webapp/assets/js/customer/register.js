let currentStep = 1;
const totalSteps = 3;

    // Chuyển bước
    function showStep(step) {
        document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
        document.getElementById(`step${step}`).classList.add('active');

        const progress = (step / totalSteps) * 100;
        document.getElementById('progressBar').style.width = progress + '%';
        document.getElementById('currentStep').textContent = step;
        document.getElementById('progressPercent').textContent = Math.round(progress);
    }

// Validate từng field
    function validateField(fieldId, regex, errorMsg) {
        const field = document.getElementById(fieldId);
        const validation = document.getElementById(fieldId + 'Validation');

        if (regex.test(field.value)) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
            validation.textContent = '✓ Valid';
            validation.classList.remove('invalid');
            validation.classList.add('valid');
            return true;
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
            validation.textContent = errorMsg;
            validation.classList.remove('valid');
            validation.classList.add('invalid');
            return false;
        }
    }

// Step 1 validation - Personal Information
    document.getElementById('nextStep1').addEventListener('click', function () {
        const nameRegex = /^[\p{L}\s'-]{2,}$/u;

        const firstNameValid = validateField('firstName', nameRegex, 'Invalid first name');
        const lastNameValid = validateField('lastName', nameRegex, 'Invalid last name');
        const phoneValid = validateField('phoneNumber', /^[0-9]{10,11}$/, 'Enter valid phone number (10-11 digits)');
        const emailValid = validateField('email', /^[^\s@]+@[^\s@]+\.[^\s@]+$/, 'Enter valid email');

        if (firstNameValid && lastNameValid && phoneValid && emailValid) {
            currentStep = 2;
            showStep(currentStep);
        }
    });

// Step 2 validation - Address
    document.getElementById('nextStep2').addEventListener('click', function () {
        const birthDateField = document.getElementById('birthDate');
        const birthDateValidation = document.getElementById('birthDateValidation');
        const genderField = document.getElementById('gender');
        const genderValidation = document.getElementById('genderValidation');

        // Validate các dropdown địa chỉ
        const provinceField = document.getElementById('province');
        const provinceValidation = document.getElementById('provinceValidation');
        const districtField = document.getElementById('district');
        const districtValidation = document.getElementById('districtValidation');
        const addressDetailField = document.getElementById('addressDetail');
        const addressDetailValidation = document.getElementById('addressDetailValidation');

        // Validate birth date
        let birthDateValid = false;
        if (birthDateField.value) {
            const birthDate = new Date(birthDateField.value);
            const today = new Date();
            const age = today.getFullYear() - birthDate.getFullYear();

            if (age >= 18 && age <= 100) {
                birthDateField.classList.remove('is-invalid');
                birthDateField.classList.add('is-valid');
                birthDateValidation.textContent = '✓ Valid';
                birthDateValidation.classList.remove('invalid');
                birthDateValidation.classList.add('valid');
                birthDateValid = true;
            } else {
                birthDateField.classList.remove('is-valid');
                birthDateField.classList.add('is-invalid');
                birthDateValidation.textContent = 'You must be between 18 and 100 years old';
                birthDateValidation.classList.remove('valid');
                birthDateValidation.classList.add('invalid');
            }
        } else {
            birthDateField.classList.remove('is-valid');
            birthDateField.classList.add('is-invalid');
            birthDateValidation.textContent = 'Please select your birth date';
            birthDateValidation.classList.remove('valid');
            birthDateValidation.classList.add('invalid');
        }

        // Validate gender
        let genderValid = false;
        if (genderField.value) {
            genderField.classList.remove('is-invalid');
            genderField.classList.add('is-valid');
            genderValidation.textContent = '✓ Valid';
            genderValidation.classList.remove('invalid');
            genderValidation.classList.add('valid');
            genderValid = true;
        } else {
            genderField.classList.remove('is-valid');
            genderField.classList.add('is-invalid');
            genderValidation.textContent = 'Please select your gender';
            genderValidation.classList.remove('valid');
            genderValidation.classList.add('invalid');
        }

        // Validate province
        let provinceValid = false;
        if (provinceField.value) {
            provinceField.classList.remove('is-invalid');
            provinceField.classList.add('is-valid');
            provinceValidation.textContent = '✓ Valid';
            provinceValidation.classList.remove('invalid');
            provinceValidation.classList.add('valid');
            provinceValid = true;
        } else {
            provinceField.classList.remove('is-valid');
            provinceField.classList.add('is-invalid');
            provinceValidation.textContent = 'Please select province';
            provinceValidation.classList.remove('valid');
            provinceValidation.classList.add('invalid');
        }

        // Validate district
        let districtValid = false;
        if (districtField.value) {
            districtField.classList.remove('is-invalid');
            districtField.classList.add('is-valid');
            districtValidation.textContent = '✓ Valid';
            districtValidation.classList.remove('invalid');
            districtValidation.classList.add('valid');
            districtValid = true;
        } else {
            districtField.classList.remove('is-valid');
            districtField.classList.add('is-invalid');
            districtValidation.textContent = 'Please select district';
            districtValidation.classList.remove('valid');
            districtValidation.classList.add('invalid');
        }

        // Validate address detail
        let addressDetailValid = false;
        if (addressDetailField.value.trim().length >= 5) {
            addressDetailField.classList.remove('is-invalid');
            addressDetailField.classList.add('is-valid');
            addressDetailValidation.textContent = '✓ Valid';
            addressDetailValidation.classList.remove('invalid');
            addressDetailValidation.classList.add('valid');
            addressDetailValid = true;
        } else {
            addressDetailField.classList.remove('is-valid');
            addressDetailField.classList.add('is-invalid');
            addressDetailValidation.textContent = 'Address detail must be at least 5 characters';
            addressDetailValidation.classList.remove('valid');
            addressDetailValidation.classList.add('invalid');
        }

        if (birthDateValid && genderValid && provinceValid && districtValid && addressDetailValid) {
            currentStep = 3;
            showStep(currentStep);
        }
    });

// Previous buttons
    document.getElementById('prevStep2').addEventListener('click', function () {
        currentStep = 1;
        showStep(currentStep);
    });

    document.getElementById('prevStep3').addEventListener('click', function () {
        currentStep = 2;
        showStep(currentStep);
    });

// Form submit - Step 3 validation
    document.getElementById('registrationForm').addEventListener('submit', function (e) {
        const usernameValid = validateField('userName', /^[a-zA-Z0-9]{3,20}$/, 'Username must be 3-20 characters (alphanumeric)');
        const passwordValid = validateField('password', /^.*$/, '');

        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const confirmPasswordField = document.getElementById('confirmPassword');
        const confirmValidation = document.getElementById('confirmPasswordValidation');

        let confirmValid = false;
        if (password === confirmPassword && password.length >= 3) {
            confirmPasswordField.classList.remove('is-invalid');
            confirmPasswordField.classList.add('is-valid');
            confirmValidation.textContent = '✓ Passwords match';
            confirmValidation.classList.remove('invalid');
            confirmValidation.classList.add('valid');
            confirmValid = true;
        } else {
            confirmPasswordField.classList.remove('is-valid');
            confirmPasswordField.classList.add('is-invalid');
            confirmValidation.textContent = 'Passwords do not match';
            confirmValidation.classList.remove('valid');
            confirmValidation.classList.add('invalid');
        }

        const agreeChecked = document.getElementById('iAgree').checked;
        if (!agreeChecked) {
            alert('Please agree to the terms and conditions');
        }

        if (!usernameValid || !passwordValid || !confirmValid || !agreeChecked) {
            e.preventDefault();
        }
    });
