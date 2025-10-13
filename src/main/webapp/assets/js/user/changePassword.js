document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('changePasswordForm');
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const mismatchError = document.getElementById('passwordMismatch');

    function validatePasswords() {
        if (newPassword.value && confirmPassword.value && newPassword.value !== confirmPassword.value) {
            mismatchError.style.display = 'block';
            confirmPassword.style.borderColor = '#721c24'; // Red border
        } else {
            mismatchError.style.display = 'none';
            confirmPassword.style.borderColor = '#ccc'; // Reset border
        }
    }

    newPassword.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);

    form.addEventListener('submit', function (event) {
        if (newPassword.value !== confirmPassword.value) {
            event.preventDefault(); // Stop the form from submitting
            alert('New passwords do not match. Please correct them.');
        }
    });
});