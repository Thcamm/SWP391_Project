document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("editProfileForm");

    // Re-usable validation function from your registration.js
    function validateField(fieldId, regex, errorMsg) {
        const field = document.getElementById(fieldId);
        const validation = document.getElementById(fieldId + "Validation");

        if (regex.test(field.value)) {
            field.classList.remove("is-invalid");
            field.classList.add("is-valid");
            validation.textContent = "✓ Valid";
            validation.classList.remove("invalid");
            validation.classList.add("valid");
            return true;
        } else {
            field.classList.remove("is-valid");
            field.classList.add("is-invalid");
            validation.textContent = errorMsg;
            validation.classList.remove("valid");
            validation.classList.add("invalid");
            return false;
        }
    }

    // Helper function for required <select> fields
    function validateSelect(fieldId, errorMsg) {
        const field = document.getElementById(fieldId);
        const validation = document.getElementById(fieldId + "Validation");

        if (field.value) { // Check if a value is selected
            field.classList.remove("is-invalid");
            field.classList.add("is-valid");
            validation.textContent = "✓ Valid";
            validation.classList.remove("invalid");
            validation.classList.add("valid");
            return true;
        } else {
            field.classList.remove("is-valid");
            field.classList.add("is-invalid");
            validation.textContent = errorMsg;
            validation.classList.remove("valid");
            validation.classList.add("invalid");
            return false;
        }
    }

    // Helper function for required <textarea>
    function validateTextArea(fieldId, minLength, errorMsg) {
        const field = document.getElementById(fieldId);
        const validation = document.getElementById(fieldId + "Validation");

        if (field.value.trim().length >= minLength) {
            field.classList.remove("is-invalid");
            field.classList.add("is-valid");
            validation.textContent = "✓ Valid";
            validation.classList.remove("invalid");
            validation.classList.add("valid");
            return true;
        } else {
            field.classList.remove("is-valid");
            field.classList.add("is-invalid");
            validation.textContent = errorMsg;
            validation.classList.remove("valid");
            validation.classList.add("invalid");
            return false;
        }
    }

    // Helper function for optional birth date
    function validateOptionalBirthDate(fieldId, errorMsg, minAge, maxAge) {
        const field = document.getElementById(fieldId);
        const validation = document.getElementById(fieldId + "Validation");

        if (!field.value) {
            // It's optional, so empty is valid
            field.classList.remove("is-invalid", "is-valid");
            validation.textContent = "";
            return true;
        }

        const birthDate = new Date(field.value);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }

        if (age >= minAge && age <= maxAge) {
            field.classList.remove("is-invalid");
            field.classList.add("is-valid");
            validation.textContent = "✓ Valid";
            validation.classList.remove("invalid");
            validation.classList.add("valid");
            return true;
        } else {
            field.classList.remove("is-valid");
            field.classList.add("is-invalid");
            validation.textContent = errorMsg;
            validation.classList.remove("valid");
            validation.classList.add("invalid");
            return false;
        }
    }

    // Stop form submission only if the form is invalid
    if (form) {
        form.addEventListener("submit", function (e) {
            // Run all validations
            const fullNameValid = validateField(
                "fullName",
                /^[\p{L}\s'-]{2,}$/u,
                "Invalid full name (at least 2 characters)"
            );

            const emailValid = validateField(
                "email",
                /^[^\s@]+@[^\s@]+\.[^\s@]+$/, // Same email regex
                "Enter valid email"
            );

            // For optional phone: matches 10-11 digits OR an empty string
            const phoneValid = validateField(
                "phoneNumber",
                /(^[0-9]{10,11}$)|(^$)/,
                "Phone number must be 10-11 digits or empty"
            );

            const provinceValid = validateSelect(
                "province",
                "Please select province"
            );

            const districtValid = validateSelect(
                "district",
                "Please select district"
            );

            const addressDetailValid = validateTextArea(
                "addressDetail",
                5, // Same as your registration logic
                "Address detail must be at least 5 characters"
            );

            const genderValid = validateSelect(
                "gender",
                "Please select your gender"
            );

            const birthDateValid = validateOptionalBirthDate(
                "birthDate",
                "You must be between 18 and 100 years old",
                18,
                100 // Same as your registration logic
            );

            // Check if any validation failed
            if (
                !fullNameValid ||
                !emailValid ||
                !phoneValid ||
                !provinceValid ||
                !districtValid ||
                !addressDetailValid ||
                !genderValid ||
                !birthDateValid
            ) {
                // Prevent form submission
                e.preventDefault();
                console.log("Validation failed. Form not submitted.");
            } else {
                // If all valid, let the form submit
                console.log("Validation passed. Submitting form...");
            }
        });
    }
});