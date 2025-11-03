// State management
console.log("✅ create-customer.js loaded");


let vehicles = [];
let includeVehicle = false;

// DOM Elements
const form = document.getElementById('createCustomerForm');
const includeVehicleCheckbox = document.getElementById('includeVehicle');
const vehiclesSection = document.getElementById('vehiclesSection');
const vehiclesList = document.getElementById('vehiclesList');
const addVehicleBtn = document.getElementById('addVehicleBtn');
const vehicleCount = document.getElementById('vehicleCount');
const cancelBtn = document.getElementById('cancelBtn');

// Customer form fields
const customerFields = {
    name: document.getElementById('name'),
    email: document.getElementById('email'),
    phone: document.getElementById('phone'),
    gender: document.getElementById('gender'),
    dateOfBirth: document.getElementById('dateOfBirth'),
    address: document.getElementById('address'),
    notes: document.getElementById('notes')
};

// Initialize
function init() {
    includeVehicleCheckbox.addEventListener('change', handleIncludeVehicleChange);
    addVehicleBtn.addEventListener('click', addVehicle);
    form.addEventListener('submit', handleSubmit);
    cancelBtn.addEventListener('click', handleCancel);
}

// Toggle vehicle section
function handleIncludeVehicleChange(e) {
    includeVehicle = e.target.checked;

    if (includeVehicle) {
        vehiclesSection.style.display = 'block';
        if (vehicles.length === 0) {
            addVehicle();
        }
    } else {
        vehiclesSection.style.display = 'none';
        vehicles = [];
        renderVehicles();
    }
}

// Add new vehicle
function addVehicle() {
    const newVehicle = {
        id: Date.now().toString(),
        licensePlate: '',
        type: '',
        model: '',
        year: '',
        color: ''
    };

    vehicles.push(newVehicle);
    renderVehicles();
}

// Remove vehicle
function removeVehicle(vehicleId) {
    vehicles = vehicles.filter(v => v.id !== vehicleId);
    renderVehicles();
}

// Update vehicle field
function updateVehicleField(vehicleId, field, value) {
    const vehicle = vehicles.find(v => v.id === vehicleId);
    if (vehicle) {
        vehicle[field] = value;
    }
}

// Render all vehicles
function renderVehicles() {
    vehicleCount.textContent = vehicles.length;
    vehiclesList.innerHTML = '';

    vehicles.forEach((vehicle, index) => {
        const vehicleCard = createVehicleCard(vehicle, index);
        vehiclesList.appendChild(vehicleCard);
    });
}

// Create vehicle card element
function createVehicleCard(vehicle, index) {
    const card = document.createElement('div');
    card.className = 'vehicle-card';

    card.innerHTML = `
        <div class="vehicle-header">
            <h3 class="vehicle-title">
                <svg class="icon-small" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"></path>
                    <circle cx="7" cy="17" r="2"></circle>
                    <path d="M9 17h6"></path>
                    <circle cx="17" cy="17" r="2"></circle>
                </svg>
                Xe ${index + 1}
            </h3>
            ${vehicles.length > 1 ? `
                <button type="button" class="btn btn-outline btn-small btn-destructive" onclick="removeVehicle('${vehicle.id}')">
                    <svg class="icon-small" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M3 6h18"></path>
                        <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                        <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                    </svg>
                    Xóa
                </button>
            ` : ''}
        </div>
        <div class="card-content">
            <!-- Required Fields -->
            <div class="grid-3">
                <div class="form-group">
                    <label for="licensePlate-${vehicle.id}">
                        Biển số xe <span class="required">*</span>
                    </label>
                    <input 
                        type="text" 
                        id="licensePlate-${vehicle.id}" 
                        class="input" 
                        placeholder="30A-12345"
                        value="${vehicle.licensePlate}"
                        onchange="updateVehicleField('${vehicle.id}', 'licensePlate', this.value)"
                        required
                    >
                </div>

                <div class="form-group">
                    <label for="vehicleType-${vehicle.id}">
                        Loại xe <span class="required">*</span>
                    </label>
                    <select 
                        id="vehicleType-${vehicle.id}" 
                        class="input select"
                        onchange="updateVehicleField('${vehicle.id}', 'type', this.value)"
                        required
                    >
                        <option value="">Chọn loại xe</option>
                        <option value="car" ${vehicle.type === 'car' ? 'selected' : ''}>Ô tô</option>
                        <option value="motorcycle" ${vehicle.type === 'motorcycle' ? 'selected' : ''}>Xe máy</option>
                        <option value="truck" ${vehicle.type === 'truck' ? 'selected' : ''}>Xe tải</option>
                        <option value="bus" ${vehicle.type === 'bus' ? 'selected' : ''}>Xe khách</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="model-${vehicle.id}">
                        Mẫu xe <span class="required">*</span>
                    </label>
                    <input 
                        type="text" 
                        id="model-${vehicle.id}" 
                        class="input" 
                        placeholder="Honda City, Toyota Vios..."
                        value="${vehicle.model}"
                        onchange="updateVehicleField('${vehicle.id}', 'model', this.value)"
                        required
                    >
                </div>
            </div>

            <div class="separator"></div>

            <!-- Optional Fields -->
            <div class="grid-2">
                <div class="form-group">
                    <label for="year-${vehicle.id}">Năm sản xuất</label>
                    <input 
                        type="number" 
                        id="year-${vehicle.id}" 
                        class="input" 
                        placeholder="2020"
                        min="1900"
                        max="2025"
                        value="${vehicle.year}"
                        onchange="updateVehicleField('${vehicle.id}', 'year', this.value)"
                    >
                </div>

                <div class="form-group">
                    <label for="color-${vehicle.id}">Màu xe</label>
                    <input 
                        type="text" 
                        id="color-${vehicle.id}" 
                        class="input" 
                        placeholder="Trắng, Đen, Xanh..."
                        value="${vehicle.color}"
                        onchange="updateVehicleField('${vehicle.id}', 'color', this.value)"
                    >
                </div>
            </div>
        </div>
    `;

    return card;
}

// Get customer data
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

// Validate form
function validateForm() {
    const customerData = getCustomerData();

    // Check required name
    if (!customerData.name) {
        alert('Vui lòng nhập họ tên');
        customerFields.name.focus();
        return false;
    }

    // Check email or phone
    if (!customerData.email && !customerData.phone) {
        alert('Vui lòng nhập email hoặc số điện thoại');
        customerFields.email.focus();
        return false;
    }

    // Validate vehicles if included
    if (includeVehicle && vehicles.length > 0) {
        for (let i = 0; i < vehicles.length; i++) {
            const vehicle = vehicles[i];
            if (!vehicle.licensePlate || !vehicle.type || !vehicle.model) {
                alert(`Vui lòng nhập đầy đủ thông tin xe bắt buộc cho xe ${i + 1} (biển số, loại xe, mẫu xe)`);
                document.getElementById(`licensePlate-${vehicle.id}`).focus();
                return false;
            }
        }
    }

    return true;
}

// Handle form submission
function handleSubmit(e) {

    if (!validateForm()) {
        return;
    }

    const formData = {
        customer: getCustomerData(),
        vehicles: includeVehicle ? vehicles : []
    };

    console.log('Form submitted:', formData);
    alert('Tạo khách hàng thành công!');

    // Optional: Reset form
    // resetForm();
}

// Handle cancel
function handleCancel() {
    if (confirm('Bạn có chắc chắn muốn hủy? Tất cả dữ liệu sẽ bị xóa.')) {
        resetForm();
    }
}

// Reset form
function resetForm() {
    form.reset();
    vehicles = [];
    includeVehicle = false;
    includeVehicleCheckbox.checked = false;
    vehiclesSection.style.display = 'none';
    renderVehicles();
}
//
// // Start the app
document.addEventListener("DOMContentLoaded", init);

