/**
 * ADD VEHICLE FORM HANDLER
 * Handle adding a new vehicle in the modal form
 * File: assets/js/customerservice/add-vehicle.js
 */

(function() {
    'use strict';

    // Wait until DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAddVehicleForm);
    } else {
        initAddVehicleForm();
    }

    function initAddVehicleForm() {
        // Get required elements
        const form = document.getElementById("addVehicleForm");
        const brandSelect = document.getElementById("brandId");
        const modelSelect = document.getElementById("modelName");
        const licensePlateInput = document.getElementById("licensePlate");
        const yearInput = document.getElementById("yearManufacture");
        const customerIdInput = document.getElementById("modalCustomerId");
        const modal = document.getElementById('addVehicleModal');

        // Check if form exists
        if (!form) {
            console.warn("Form 'addVehicleForm' not found");
            return;
        }

        console.log("Add vehicle form initialized successfully");

        // ============================================
        // PART 1: HANDLE BRAND → MODEL DROPDOWN
        // ============================================
        if (brandSelect && modelSelect) {
            // Clone to remove old event listeners (if any)
            const newBrandSelect = brandSelect.cloneNode(true);
            brandSelect.parentNode.replaceChild(newBrandSelect, brandSelect);

            // Listen for brand selection change
            newBrandSelect.addEventListener("change", function() {
                const brandId = this.value;
                console.log("Selected brand:", brandId);

                if (!brandId) {
                    modelSelect.disabled = true;
                    modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";
                    return;
                }

                // Enable model dropdown and show loading
                modelSelect.disabled = false;
                modelSelect.innerHTML = "<option value=''>Loading models...</option>";

                // Build API URL
                const apiUrl = (typeof contextPath !== 'undefined' ? contextPath : '') +
                    '/customerservice/addVehicle?action=getModels&brandId=' + encodeURIComponent(brandId);

                // Fetch model list
                fetch(apiUrl)
                    .then(function(response) {
                        if (!response.ok) {
                            throw new Error('HTTP error: ' + response.status);
                        }
                        return response.json();
                    })
                    .then(function(models) {
                        console.log("Loaded " + models.length + " models");

                        modelSelect.innerHTML = "<option value=''>-- Select model --</option>";

                        if (models.length === 0) {
                            modelSelect.innerHTML = "<option value=''>-- No models available --</option>";
                        } else {
                            models.forEach(function(model) {
                                const option = document.createElement("option");
                                option.value = model.name;
                                option.textContent = model.name;
                                modelSelect.appendChild(option);
                            });
                        }
                    })
                    .catch(function(error) {
                        console.error('Error loading models:', error);
                        modelSelect.innerHTML = "<option value=''>Error loading models</option>";

                        if (typeof showAlert === 'function') {
                            showAlert("Unable to load model list", "error");
                        }
                    });
            });
        }

        // ============================================
        // PART 2: HANDLE FORM SUBMISSION
        // ============================================
        form.addEventListener("submit", function(e) {
            e.preventDefault();
            e.stopPropagation();

            console.log("\n==========================================");
            console.log("START ADD VEHICLE FORM SUBMISSION");
            console.log("==========================================");

            // Collect form data
            const brandId = document.getElementById("brandId").value;
            const modelName = document.getElementById("modelName").value;
            const licensePlate = document.getElementById("licensePlate").value.trim();
            const yearManufacture = document.getElementById("yearManufacture").value;
            let customerId = customerIdInput ? customerIdInput.value : "";

            console.log("Form data:", {
                brandId,
                modelName,
                licensePlate,
                yearManufacture,
                customerId
            });

            // Step 1: Validation
            const missingFields = [];

            if (!brandId) missingFields.push("Brand");
            if (!modelName) missingFields.push("Model");
            if (!licensePlate) missingFields.push("License plate");
            if (!yearManufacture) missingFields.push("Year of manufacture");

            if (missingFields.length > 0) {
                const errorMessage = "Please fill out the following fields: " + missingFields.join(", ");
                console.warn(errorMessage);

                if (typeof showAlert === 'function') {
                    showAlert(errorMessage, "error");
                } else {
                    alert(errorMessage);
                }
                return false;
            }

            // Get customerId from global variable if not available
            if (!customerId) {
                if (typeof window.customerId !== 'undefined' && window.customerId !== "") {
                    customerId = window.customerId;
                    console.log("Using customerId from global variable:", customerId);
                } else {
                    console.error("Customer ID not found");

                    if (typeof showAlert === 'function') {
                        showAlert("Customer information not found", "error");
                    } else {
                        alert("Customer information not found");
                    }
                    return false;
                }
            }

            // Step 2: Disable submit button and show loading
            const submitBtn = form.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.textContent;

            submitBtn.disabled = true;
            submitBtn.textContent = "Saving...";
            console.log("Submit button disabled");

            // Step 3: Prepare payload and send request
            const apiUrl = (typeof contextPath !== 'undefined' ? contextPath : '') +
                '/customerservice/addVehicle?action=saveVehicle';

            const payload = new URLSearchParams();
            payload.append('customerId', customerId);
            payload.append('brandId', brandId);
            payload.append('modelName', modelName);
            payload.append('yearManufacture', yearManufacture);
            payload.append('licensePlate', licensePlate);

            console.log("Sending request to:", apiUrl);
            console.log("Payload:", payload.toString());

            fetch(apiUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: payload.toString()
            })
                .then(function(response) {
                    console.log("Response received - Status:", response.status, response.statusText);

                    const contentType = response.headers.get("content-type");

                    if (!contentType || !contentType.includes("application/json")) {
                        return response.text().then(function(text) {
                            console.error("Server did not return JSON:", text.substring(0, 300));
                            throw new Error('Server did not return JSON. Possibly a servlet error.');
                        });
                    }

                    if (!response.ok) {
                        throw new Error('HTTP error! Status: ' + response.status);
                    }

                    return response.json();
                })
                .then(function(data) {
                    console.log("Received JSON response:", data);

                    if (data.success) {
                        console.log("Vehicle added successfully");

                        // Close modal
                        const modalElement = document.getElementById("addVehicleModal");
                        const modalInstance = bootstrap.Modal.getInstance(modalElement);

                        if (modalInstance) {
                            modalInstance.hide();
                            console.log("Modal closed");
                        }

                        // Reset form
                        form.reset();

                        if (modelSelect) {
                            modelSelect.disabled = true;
                            modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";
                        }
                        console.log("Form reset");

                        // Update vehicle list if function exists
                        if (typeof updateVehicleInActiveOrder === 'function') {
                            console.log("Calling updateVehicleInActiveOrder with data:", data);
                            updateVehicleInActiveOrder(data);
                        } else {
                            console.warn("Function updateVehicleInActiveOrder not found");
                        }

                        if (typeof showAlert === 'function') {
                            showAlert("Vehicle added successfully", "success");
                        } else {
                            alert("Vehicle added successfully");
                        }

                    } else {
                        const errorMsg = data.message || "Error occurred while adding vehicle";
                        console.error("Server error:", errorMsg);

                        if (typeof showAlert === 'function') {
                            showAlert(errorMsg, "error");
                        } else {
                            alert(errorMsg);
                        }
                    }
                })
                .catch(function(error) {
                    console.error("Unexpected error:", error);

                    if (typeof showAlert === 'function') {
                        showAlert("Error: " + error.message, "error");
                    } else {
                        alert("Error: " + error.message);
                    }
                })
                .finally(function() {
                    // Step 5: Re-enable button
                    submitBtn.disabled = false;
                    submitBtn.textContent = originalBtnText;
                    console.log("Submit button re-enabled");
                    console.log("==========================================");
                    console.log("END FORM PROCESS\n");
                });

            return false;
        });

        // ============================================
        // PART 3: HANDLE MODAL EVENTS
        // ============================================
        if (modal) {
            // Reset form when modal is closed
            modal.addEventListener('hidden.bs.modal', function() {
                console.log("Modal closed, resetting form");

                form.reset();

                if (modelSelect) {
                    modelSelect.disabled = true;
                    modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";
                }
            });

            // Set customerId when modal is opened
            modal.addEventListener('show.bs.modal', function() {
                console.log("Modal opened");

                if (typeof window.customerId !== 'undefined' && customerIdInput) {
                    customerIdInput.value = window.customerId;
                    console.log("Set customerId in modal:", window.customerId);
                }
            });
        }

        console.log("Add vehicle form fully initialized!");
    }

})();
