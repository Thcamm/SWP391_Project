// ===== HELPER FUNCTIONS - ĐỊNH NGHĨA TRƯỚC =====
function showAlert(message, type = "info") {
    // Remove alert cũ nếu có
    const oldAlert = document.querySelector('.custom-alert');
    if (oldAlert) oldAlert.remove();

    const alertDiv = document.createElement("div");
    alertDiv.className = `alert alert-${type === "error" ? "danger" : type === "success" ? "success" : type === "warning" ? "warning" : "info"} alert-dismissible fade show custom-alert`;
    alertDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        animation: slideIn 0.3s ease;
    `;
    alertDiv.innerHTML = `
        <strong>${message}</strong>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(alertDiv);

    // Auto remove sau 4s
    setTimeout(() => {
        alertDiv.classList.remove('show');
        setTimeout(() => alertDiv.remove(), 150);
    }, 4000);
}

function closeAllDropdowns() {
    document.querySelectorAll(".dropdown-list").forEach(d => {
        d.style.transition = "opacity 0.15s ease";
        d.style.opacity = "0";
        setTimeout(() => d.remove(), 150);
    });
}

function updateVehicleInActiveOrder(vehicleData) {
    const orders = document.querySelectorAll(".service-order");
    let activeOrder = null;

    // Tìm order chưa có vehicle
    for (let order of orders) {
        if (!order.dataset.vehicleId || order.dataset.vehicleId === "") {
            activeOrder = order;
            break;
        }
    }

    // Nếu không tìm thấy order trống, lấy order cuối cùng
    if (!activeOrder && orders.length > 0) {
        activeOrder = orders[orders.length - 1];
    }

    if (activeOrder) {
        const vehicleInput = activeOrder.querySelector(".vehicle-input");
        const vehicleIdField = activeOrder.querySelector(".vehicle-id");

        // Remove old vehicle nếu có
        if (activeOrder.dataset.vehicleId) {
            const allSelectedVehicles = window.allSelectedVehicles;
            if (allSelectedVehicles) {
                allSelectedVehicles.delete(parseInt(activeOrder.dataset.vehicleId));
            }
        }

        // Set giá trị xe mới
        vehicleInput.value = `${vehicleData.licensePlate} | ${vehicleData.brand} ${vehicleData.model}`;
        vehicleIdField.value = vehicleData.vehicleId;

        const allSelectedVehicles = window.allSelectedVehicles;
        if (allSelectedVehicles) {
            allSelectedVehicles.add(vehicleData.vehicleId);
        }

        activeOrder.dataset.vehicleId = vehicleData.vehicleId.toString();
        vehicleInput.setAttribute("readonly", true);

        // Add success animation
        vehicleInput.classList.add("success");
        setTimeout(() => vehicleInput.classList.remove("success"), 600);

        // Thêm nút clear
        let clearBtn = activeOrder.querySelector(".clear-vehicle-btn");
        if (clearBtn) clearBtn.remove();

        clearBtn = document.createElement("button");
        clearBtn.type = "button";
        clearBtn.innerHTML = "✖";
        clearBtn.className = "clear-vehicle-btn";
        clearBtn.title = "Xóa xe đã chọn";

        clearBtn.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            if (allSelectedVehicles) {
                allSelectedVehicles.delete(vehicleData.vehicleId);
            }

            activeOrder.dataset.vehicleId = "";
            vehicleInput.value = "";
            vehicleIdField.value = "";
            vehicleInput.removeAttribute("readonly");
            clearBtn.remove();

            closeAllDropdowns();
            vehicleInput.focus();
        });

        vehicleInput.parentNode.appendChild(clearBtn);
    }
}

// Add animation keyframes
if (!document.getElementById('alert-animation-styles')) {
    const style = document.createElement('style');
    style.id = 'alert-animation-styles';
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(400px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        .form-control.success {
            border-color: #28a745 !important;
            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25) !important;
        }
        .clear-vehicle-btn {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background-color: #e5e5e5;
            color: #666;
            border: none;
            cursor: pointer;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s ease;
            z-index: 10;
        }
        .clear-vehicle-btn:hover {
            background-color: #dc3545;
            color: white;
            transform: translateY(-50%) rotate(90deg);
        }
    `;
    document.head.appendChild(style);
}

// ===== MAIN CODE =====
document.addEventListener("DOMContentLoaded", () => {
    const ordersContainer = document.getElementById("ordersContainer");
    const addOrderBtn = document.getElementById("addOrderBtn");
    const allSelectedVehicles = new Set();
    const allSelectedServices = new Set();

    // Expose to global scope for updateVehicleInActiveOrder
    window.allSelectedVehicles = allSelectedVehicles;

    setupOrder(ordersContainer.querySelector(".service-order"));

    addOrderBtn.addEventListener("mousedown", () => {
        closeAllDropdowns();

        const index = ordersContainer.children.length;
        const newOrder = ordersContainer.firstElementChild.cloneNode(true);
        newOrder.dataset.index = index;

        // Update order badge
        const badge = newOrder.querySelector(".service-order-badge");
        if (badge) {
            badge.textContent = `Order #${index + 1}`;
        }

        // Reset vehicle
        newOrder.dataset.vehicleId = "";
        const vehicleInput = newOrder.querySelector(".vehicle-input");
        vehicleInput.value = "";
        vehicleInput.removeAttribute("readonly");

        const vehicleIdField = newOrder.querySelector(".vehicle-id");
        vehicleIdField.value = "";
        vehicleIdField.setAttribute("name", `vehicleIds[${index}]`);

        const clearBtn = newOrder.querySelector(".clear-vehicle-btn");
        if (clearBtn) clearBtn.remove();

        // Reset services
        const serviceInput = newOrder.querySelector(".service-input");
        serviceInput.value = "";

        const servicesTableBody = newOrder.querySelector(".selected-services tbody");
        servicesTableBody.innerHTML = `<tr class="no-service"><td colspan="3">Chưa có dịch vụ nào được chọn</td></tr>`;

        // Show remove button
        const removeBtn = newOrder.querySelector(".remove-order-btn");
        removeBtn.classList.remove("d-none");

        // Remove old hidden inputs
        newOrder.querySelectorAll("input[type='hidden']").forEach(input => {
            if (input.name.startsWith("serviceIds")) input.remove();
        });

        // Add to container with animation
        ordersContainer.appendChild(newOrder);

        // Trigger fade-in animation
        newOrder.style.opacity = "0";
        setTimeout(() => {
            newOrder.style.transition = "opacity 0.3s ease";
            newOrder.style.opacity = "1";
        }, 10);

        setupOrder(newOrder);

        // Scroll to new order
        newOrder.scrollIntoView({ behavior: "smooth", block: "center" });
    });

    function setupOrder(orderEl) {
        const index = orderEl.dataset.index;
        const removeOrderBtn = orderEl.querySelector(".remove-order-btn");
        const vehicleInput = orderEl.querySelector(".vehicle-input");
        const vehicleIdField = orderEl.querySelector(".vehicle-id");
        const serviceInput = orderEl.querySelector(".service-input");
        const selectedTable = orderEl.querySelector(".selected-services tbody");

        const selectedServiceIDs = new Set();
        let isSelectingVehicle = false;

        // Remove order
        removeOrderBtn.addEventListener("click", (e) => {
            e.preventDefault();
            const totalOrders = document.querySelectorAll(".service-order").length;
            if (totalOrders <= 1) {
                showAlert("Must have at least 1 Service Order", "warning");
                return;
            }

            // Remove from global sets
            if (orderEl.dataset.vehicleId) {
                allSelectedVehicles.delete(parseInt(orderEl.dataset.vehicleId));
                orderEl.dataset.vehicleId = "";
            }
            selectedServiceIDs.forEach(id => allSelectedServices.delete(id));

            const clearBtn = orderEl.querySelector(".clear-vehicle-btn");
            if (clearBtn) clearBtn.remove();

            // Fade out animation
            orderEl.style.transition = "opacity 0.3s ease, transform 0.3s ease";
            orderEl.style.opacity = "0";
            orderEl.style.transform = "translateX(-20px)";

            setTimeout(() => {
                orderEl.remove();
                updateOrderBadges();
            }, 300);

            closeAllDropdowns();
        });

        // Vehicle input handlers
        vehicleInput.addEventListener("focus", () => {
            if (!vehicleInput.hasAttribute("readonly") && !isSelectingVehicle) {
                fetchVehicles(vehicleInput, vehicleIdField, orderEl);
            }
        });

        vehicleInput.addEventListener("input", () => {
            if (!vehicleInput.hasAttribute("readonly") && !isSelectingVehicle) {
                const keyword = vehicleInput.value.trim();
                if (!keyword && orderEl.dataset.vehicleId) {
                    allSelectedVehicles.delete(parseInt(orderEl.dataset.vehicleId));
                    orderEl.dataset.vehicleId = "";
                    vehicleIdField.value = "";
                }
                fetchVehicles(vehicleInput, vehicleIdField, orderEl);
            }
        });

        // Service input handlers
        serviceInput.addEventListener("focus", () => {
            fetchServices(serviceInput, selectedServiceIDs, selectedTable, orderEl, index);
        });

        serviceInput.addEventListener("input", () => {
            fetchServices(serviceInput, selectedServiceIDs, selectedTable, orderEl, index);
        });

        async function fetchVehicles(inputEl, hiddenField, orderEl) {
            const keyword = inputEl.value.trim();
            try {
                const res = await fetch(`${contextPath}/customerservice/vehicles-search?query=${encodeURIComponent(keyword)}&customerId=${customerId}`);
                const data = await res.json();

                const mapped = data.length === 0
                    ? [{ label: "\n" +"No car found – Add car", value: null, addNew: true }]
                    : data.map(v => ({
                        label: `${v.licensePlate} | ${v.brand} ${v.model}`,
                        value: v.vehicleID,
                        disabled: allSelectedVehicles.has(v.vehicleID)
                    }));

                showVehicleDropdown(inputEl, mapped, selected => {
                    isSelectingVehicle = true;

                    if (selected.addNew) {
                        const modal = new bootstrap.Modal(document.getElementById("addVehicleModal"));
                        modal.show();
                        setTimeout(() => isSelectingVehicle = false, 100);
                        return;
                    }

                    // Remove old vehicle
                    if (orderEl.dataset.vehicleId) {
                        allSelectedVehicles.delete(parseInt(orderEl.dataset.vehicleId));
                    }

                    // Set new vehicle
                    inputEl.value = selected.label;
                    hiddenField.value = selected.value;
                    allSelectedVehicles.add(selected.value);
                    orderEl.dataset.vehicleId = selected.value.toString();
                    inputEl.setAttribute("readonly", true);

                    // Add success animation
                    inputEl.classList.add("success");
                    setTimeout(() => inputEl.classList.remove("success"), 600);

                    // Remove old clear button
                    let clearBtn = orderEl.querySelector(".clear-vehicle-btn");
                    if (clearBtn) clearBtn.remove();

                    // Create clear button
                    clearBtn = document.createElement("button");
                    clearBtn.type = "button";
                    clearBtn.innerHTML = "✖";
                    clearBtn.className = "clear-vehicle-btn";
                    clearBtn.title = "Xóa xe đã chọn";

                    clearBtn.addEventListener("click", (e) => {
                        e.preventDefault();
                        e.stopPropagation();

                        allSelectedVehicles.delete(parseInt(orderEl.dataset.vehicleId));
                        orderEl.dataset.vehicleId = "";
                        inputEl.value = "";
                        hiddenField.value = "";
                        inputEl.removeAttribute("readonly");
                        clearBtn.remove();
                        closeAllDropdowns();

                        inputEl.focus();
                    });

                    inputEl.parentNode.appendChild(clearBtn);

                    setTimeout(() => isSelectingVehicle = false, 100);
                });
            } catch (e) {
                console.error("Error fetching vehicles:", e);
                showAlert("Error fetching vehicles", "error");
            }
        }

        async function fetchServices(inputEl, selectedServiceIDs, selectedTable, orderEl, index) {
            const keyword = inputEl.value.trim();
            try {
                const res = await fetch(`${contextPath}/customerservice/services-search?query=${encodeURIComponent(keyword)}`);
                const data = await res.json();

                const mapped = data.map(s => ({
                    label: `${s.serviceName}`,
                    price: s.price,
                    category: s.category,
                    value: s.serviceTypeID,
                    disabled: selectedServiceIDs.has(s.serviceTypeID)
                }));

                showServiceDropdown(inputEl, mapped, selected => {
                    if (selectedServiceIDs.has(selected.value)) return;

                    selectedServiceIDs.add(selected.value);
                    allSelectedServices.add(selected.value);

                    // Remove "no service" row
                    const noServiceRow = selectedTable.querySelector(".no-service");
                    if (noServiceRow) noServiceRow.remove();

                    // Create new row
                    const tr = document.createElement("tr");
                    tr.style.opacity = "0";

                    const tdName = document.createElement("td");
                    tdName.className = "service-name";
                    tdName.textContent = selected.label;

                    const tdPrice = document.createElement("td");
                    tdPrice.className = "service-price";
                    tdPrice.textContent = `${selected.price.toLocaleString('vi-VN')}₫`;

                    const tdAction = document.createElement("td");
                    tdAction.className = "service-action";

                    const removeBtn = document.createElement("button");
                    removeBtn.type = "button";
                    removeBtn.className = "remove-btn";
                    removeBtn.innerHTML = "✖";
                    removeBtn.title = "Xóa dịch vụ";

                    removeBtn.addEventListener("click", (e) => {
                        e.preventDefault();
                        selectedServiceIDs.delete(selected.value);
                        allSelectedServices.delete(selected.value);

                        // Fade out animation
                        tr.style.transition = "opacity 0.2s ease";
                        tr.style.opacity = "0";

                        setTimeout(() => {
                            tr.remove();
                            orderEl.querySelector(`input[type='hidden'][value='${selected.value}']`)?.remove();

                            if (selectedServiceIDs.size === 0) {
                                selectedTable.innerHTML = `<tr class="no-service"><td colspan="3">Chưa có dịch vụ nào được chọn</td></tr>`;
                            }
                        }, 200);
                    });

                    tdAction.appendChild(removeBtn);
                    tr.appendChild(tdName);
                    tr.appendChild(tdPrice);
                    tr.appendChild(tdAction);
                    selectedTable.appendChild(tr);

                    // Fade in animation
                    setTimeout(() => {
                        tr.style.transition = "opacity 0.3s ease";
                        tr.style.opacity = "1";
                    }, 10);

                    // Create hidden input
                    const hidden = document.createElement("input");
                    hidden.type = "hidden";
                    hidden.name = `serviceIds[${index}][]`;
                    hidden.value = selected.value;
                    orderEl.appendChild(hidden);

                    // Clear input
                    inputEl.value = "";
                });
            } catch (e) {
                console.error("Error fetching services:", e);
                showAlert("Error fetching services", "error");
            }
        }
    }

    function showVehicleDropdown(inputEl, data, onSelect) {
        closeAllDropdowns();

        const dropdown = document.createElement("div");
        dropdown.className = "dropdown-list";

        data.forEach((item, idx) => {
            const div = document.createElement("div");
            div.className = "dropdown-item";
            div.textContent = item.label;
            div.style.animationDelay = `${idx * 0.05}s`;

            if (!item.disabled && !item.addNew) {
                div.addEventListener("mousedown", e => {
                    e.preventDefault();
                    e.stopPropagation();
                    onSelect(item);
                    dropdown.remove();
                });
            } else if (item.addNew) {
                div.style.fontWeight = "600";
                div.style.color = "#000";
                div.addEventListener("mousedown", e => {
                    e.preventDefault();
                    e.stopPropagation();
                    onSelect(item);
                    dropdown.remove();
                });
            } else {
                div.classList.add("disabled");
            }

            dropdown.appendChild(div);
        });

        inputEl.parentNode.appendChild(dropdown);

        // Close on outside click
        document.addEventListener("mousedown", function closeOutside(e) {
            if (!dropdown.contains(e.target) && e.target !== inputEl) {
                if (!inputEl.closest(".service-order").dataset.vehicleId) {
                    inputEl.value = "";
                }
                dropdown.remove();
                document.removeEventListener("mousedown", closeOutside);
            }
        });
    }

    function showServiceDropdown(inputEl, data, onSelect) {
        closeAllDropdowns();

        const dropdown = document.createElement("div");
        dropdown.className = "dropdown-list";

        if (data.length === 0) {
            const div = document.createElement("div");
            div.className = "dropdown-item disabled";
            div.textContent = "Cannot find services";
            dropdown.appendChild(div);
        } else {
            data.forEach((item, idx) => {
                const div = document.createElement("div");
                div.className = "dropdown-item";
                div.style.animationDelay = `${idx * 0.05}s`;

                const nameSpan = document.createElement("div");
                nameSpan.textContent = item.label;
                nameSpan.style.fontWeight = "500";

                const detailSpan = document.createElement("div");
                detailSpan.textContent = `${item.category} • ${item.price.toLocaleString('vi-VN')}₫`;
                detailSpan.style.fontSize = "0.875rem";
                detailSpan.style.color = "#666";
                detailSpan.style.marginTop = "2px";

                div.appendChild(nameSpan);
                div.appendChild(detailSpan);

                if (item.disabled) {
                    div.classList.add("disabled");
                } else {
                    div.addEventListener("mousedown", function handleClick(e) {
                        e.preventDefault();
                        e.stopPropagation();
                        onSelect(item);
                        div.classList.add("disabled");
                    });
                }

                dropdown.appendChild(div);
            });
        }

        inputEl.parentNode.appendChild(dropdown);

        // Close on outside click
        document.addEventListener("mousedown", function closeOutside(e) {
            if (!dropdown.contains(e.target) && e.target !== inputEl) {
                dropdown.remove();
                document.removeEventListener("mousedown", closeOutside);
            }
        });
    }

    function updateOrderBadges() {
        const orders = ordersContainer.querySelectorAll(".service-order");
        orders.forEach((order, idx) => {
            order.dataset.index = idx;
            const badge = order.querySelector(".service-order-badge");
            if (badge) {
                badge.textContent = `Order #${idx + 1}`;
            }
        });
    }

    // === FORM VALIDATION ===
    const form = document.getElementById("serviceOrderForm");
    form.addEventListener("submit", (e) => {
        let valid = true;
        const orders = ordersContainer.querySelectorAll(".service-order");

        orders.forEach((order, idx) => {
            // Check services
            const rows = order.querySelectorAll(".selected-services tbody tr");
            const serviceCount = Array.from(rows).filter(r => !r.classList.contains("no-service")).length;

            if (serviceCount < 1) {
                valid = false;
                const serviceInput = order.querySelector(".service-input");
                serviceInput.classList.add("error");
                setTimeout(() => serviceInput.classList.remove("error"), 500);
                showAlert(`Service Order #${idx + 1} have to select at least 1 service`, "warning");
            }

            // Check vehicle
            if (!order.dataset.vehicleId || order.dataset.vehicleId === "") {
                valid = false;
                const vehicleInput = order.querySelector(".vehicle-input");
                vehicleInput.classList.add("error");
                setTimeout(() => vehicleInput.classList.remove("error"), 500);
                showAlert(`Service Order #${idx + 1} not select vehicle`, "warning");
            }
        });

        if (!valid) {
            e.preventDefault();
            return;
        }

        // Add loading state to submit button
        const submitBtn = form.querySelector(".btn-submit");
        submitBtn.classList.add("loading");
        submitBtn.disabled = true;
    });
});