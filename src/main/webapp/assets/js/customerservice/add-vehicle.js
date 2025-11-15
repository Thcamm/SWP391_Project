/**
 * ADD VEHICLE FORM HANDLER (FINAL, STABLE VERSION)
 * Clean, safe, no cloneNode, brand+model dropdown works 100%
 */

(function() {
    'use strict';

    let vehicleData = [];

    // INIT
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    function init() {
        console.log("🚀 initAddVehicleForm()");

        const form = document.getElementById("addVehicleForm");
        let brandSelect = document.getElementById("brandName");
        let modelSelect = document.getElementById("modelName");
        const modal = document.getElementById("addVehicleModal");
        const customerIdInput = document.getElementById("modalCustomerId");

        if (!form || !brandSelect || !modelSelect) {
            console.error("❌ Missing form or selects");
            return;
        }

        modelSelect.disabled = true;
        modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";

        // -------------------------------------------------------
        // LOAD JSON (cached)
        // -------------------------------------------------------
        function loadCarModelsData() {
            if (vehicleData.length > 0) return Promise.resolve(vehicleData);

            const jsonUrl = (typeof contextPath !== "undefined" ? contextPath : "") + "/assets/car-models.json";
            console.log("📥 Loading car models:", jsonUrl);

            return fetch(jsonUrl)
                .then(res => {
                    if (!res.ok) throw new Error("Failed to fetch JSON: " + res.status);
                    return res.json();
                })
                .then(data => {
                    console.log("✔ Car models loaded:", data.length, "brands");
                    vehicleData = data;
                    return data;
                })
                .catch(err => {
                    console.error("❌ JSON load error:", err);
                    if (typeof showAlert === "function") showAlert("Cannot load vehicle data!", "error");
                });
        }

        // -------------------------------------------------------
        // POPULATE BRAND SELECT
        // -------------------------------------------------------
        function populateBrands() {
            if (!vehicleData || vehicleData.length === 0) return;

            brandSelect.innerHTML = `<option value="">-- Select brand --</option>`;

            vehicleData.forEach(b => {
                const opt = document.createElement("option");
                opt.value = b.brand;
                opt.textContent = b.brand;
                brandSelect.appendChild(opt);
            });

            console.log("✔ Brand options populated:", vehicleData.length);
        }

        // -------------------------------------------------------
        // HANDLE BRAND CHANGE -> LOAD MODELS
        // -------------------------------------------------------
        brandSelect.addEventListener("change", function() {
            const brandName = this.value;

            console.log("📌 Brand selected:", brandName);

            if (!brandName) {
                modelSelect.disabled = true;
                modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";
                return;
            }

            modelSelect.disabled = false;
            modelSelect.innerHTML = "<option>Loading models...</option>";

            const brandData = vehicleData.find(x => x.brand === brandName);

            modelSelect.innerHTML = "<option value=''>-- Select model --</option>";

            if (brandData && brandData.models.length > 0) {
                brandData.models.forEach(m => {
                    const opt = document.createElement("option");
                    opt.value = m;
                    opt.textContent = m;
                    modelSelect.appendChild(opt);
                });

                console.log("✔ Models loaded:", brandData.models.length);
            } else {
                modelSelect.innerHTML = "<option value=''>-- No models available --</option>";
            }
        });

        // -------------------------------------------------------
        // SUBMIT FORM
        // -------------------------------------------------------
        form.addEventListener("submit", function(e) {
            e.preventDefault();

            const brandName = brandSelect.value;
            const modelName = modelSelect.value;
            const licensePlate = document.getElementById("licensePlate").value.trim();
            const yearManufacture = document.getElementById("yearManufacture").value;
            let customerId = customerIdInput ? customerIdInput.value : "";

            const missing = [];
            if (!brandName) missing.push("Brand");
            if (!modelName) missing.push("Model");
            if (!licensePlate) missing.push("License Plate");
            if (!yearManufacture) missing.push("Year");

            if (missing.length > 0) {
                showAlert("Please fill out: " + missing.join(", "), "error");
                return;
            }

            if (!customerId) {
                customerId = window.customerId;
                if (!customerId) {
                    showAlert("Customer not found.", "error");
                    return;
                }
            }

            const apiUrl = (typeof contextPath !== "undefined" ? contextPath : "")
                + "/customerservice/addVehicle?action=saveVehicle";

            const payload = new URLSearchParams({
                customerId,
                brandName,
                modelName,
                licensePlate,
                yearManufacture
            });

            const submitBtn = form.querySelector("button[type='submit']");
            const textOld = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = "Saving...";

            fetch(apiUrl, {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: payload.toString()
            })
                .then(r => r.json())
                .then(data => {
                    if (!data.success) {
                        showAlert(data.message || "Server error.", "error");
                        return;
                    }

                    showAlert("Vehicle added successfully!", "success");

                    const modalObj = bootstrap.Modal.getInstance(modal);
                    if (modalObj) modalObj.hide();

                    form.reset();
                    modelSelect.disabled = true;
                    modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";

                    if (typeof updateVehicleInActiveOrder === "function") {
                        updateVehicleInActiveOrder(data);
                    }
                })
                .catch(err => {
                    console.error("❌ Unexpected error:", err);
                    showAlert("Error: " + err.message, "error");
                })
                .finally(() => {
                    submitBtn.disabled = false;
                    submitBtn.textContent = textOld;
                });
        });

        // -------------------------------------------------------
        // MODAL EVENTS
        // -------------------------------------------------------
        if (modal) {
            modal.addEventListener("show.bs.modal", function() {
                if (window.customerId && customerIdInput) {
                    customerIdInput.value = window.customerId;
                }

                loadCarModelsData().then(populateBrands);
            });

            modal.addEventListener("hidden.bs.modal", function() {
                form.reset();
                modelSelect.disabled = true;
                modelSelect.innerHTML = "<option value=''>-- Select brand first --</option>";
            });
        }

        console.log("✔ Add Vehicle Form initialized!");
    }

})();