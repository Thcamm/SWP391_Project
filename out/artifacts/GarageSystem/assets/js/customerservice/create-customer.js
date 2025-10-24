

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("createCustomerForm");
    if (!form) return;

    form.addEventListener("submit", function (e) {
        const email = document.getElementById("email");
        const phone = document.getElementById("phone");
        const birthInput = document.getElementById("birthDate");


        form.querySelectorAll("input, textarea").forEach(el => {
            el.value = el.value.trim();
        });


        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (email.value && !emailPattern.test(email.value)) {
            alert("Please enter a valid email address.");
            e.preventDefault();
            return;
        }


        const phonePattern = /^\d{10,11}$/;
        if (phone.value && !phonePattern.test(phone.value)) {
            alert("Phone number must contain 10–11 digits.");
            e.preventDefault();
            return;
        }


        if (birthInput && !validateAge(birthInput.value)) {
            e.preventDefault();
            return;
        }
    });


    function validateAge(birthValue) {
        if (!birthValue) return true;

        const birthDate = new Date(birthValue);
        const today = new Date();

        if (birthDate > today) {
            alert("Birth date cannot be in the future!");
            return false;
        }

        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();

        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }

        if (age < 18) {
            alert("Customer must be at least 18 years old!");
            return false;
        }

        return true;
    }
});
