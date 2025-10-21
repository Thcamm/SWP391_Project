document.getElementById('createCustomerForm').addEventListener('submit', function (e) {
    const email = document.getElementById('email');
    const phone = document.getElementById('phone');

    // Email format check
    const emailPattern = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;
    if (!emailPattern.test(email.value)) {
        alert("Please enter a valid email address.");
        e.preventDefault();
        return;
    }

    // Phone number check
    const phonePattern = /^\\d{10,11}$/;
    if (!phonePattern.test(phone.value)) {
        alert("Phone number must contain 10–11 digits.");
        e.preventDefault();
        return;
    }

    // Optional: trim input values
    document.querySelectorAll('#createCustomerForm input, textarea').forEach(el => el.value = el.value.trim());
});