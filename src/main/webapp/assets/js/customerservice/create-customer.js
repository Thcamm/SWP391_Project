const form = document.getElementById('createCustomerForm');
const cancelBtn = document.getElementById('cancelBtn');

const customerFields = {
    name: document.getElementById('fullName'),
    email: document.getElementById('email'),
    phone: document.getElementById('phone'),
    gender: document.getElementById('gender'),
    dateOfBirth: document.getElementById('dateOfBirth'),
    address: document.getElementById('address'),
    notes: document.getElementById('notes')
};

function init() {
    form.addEventListener('submit', handleSubmit);
    cancelBtn.addEventListener('click', handleCancel);

    // Set max date for date of birth (today)
    const today = new Date().toISOString().split('T')[0];
    customerFields.dateOfBirth.setAttribute('max', today);
}

function getCustomerData() {
    return {
        name: customerFields.name.value.trim(),
        email: customerFields.email.value.trim(),
        phone: customerFields.phone.value.trim(),
        gender: customerFields.gender.value,
        dateOfBirth: customerFields.dateOfBirth.value,
        address: customerFields.address.value.trim(),
        notes: customerFields.notes.value.trim()
    };
}

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

    // Check if date is in the future
    if (selectedDate > today) {
        return { valid: false, message: 'Date of birth cannot be in the future' };
    }

    // Check if age is at least 18
    const age = calculateAge(dateOfBirth);
    if (age < 18) {
        return { valid: false, message: 'Customer must be at least 18 years old' };
    }

    return { valid: true };
}

function validateForm() {
    const customer = getCustomerData();

    if (!customer.name) {
        alert('Please enter full name');
        return false;
    }

    if (!customer.email && !customer.phone) {
        alert('Please enter email or phone number');
        return false;
    }

    // Validate date of birth
    const dobValidation = validateDateOfBirth(customer.dateOfBirth);
    if (!dobValidation.valid) {
        alert(dobValidation.message);
        return false;
    }

    return true;
}

function handleSubmit(e) {
    e.preventDefault();
    if (!validateForm()) return;

    const payload = {
        customer: getCustomerData()
    };

    console.log('Submitting:', payload);
    alert('Customer created successfully!');
}

function handleCancel() {
    if (confirm('Are you sure you want to cancel?')) {
        form.reset();
    }
}

document.addEventListener('DOMContentLoaded', init);