/**
 * Birth Date Validation Script
 * Validates birth date to ensure:
 * - Not in the future
 * - Not more than 100 years ago
 */

document.addEventListener("DOMContentLoaded", function () {
  const birthDateInput = document.getElementById("birthDate");
  
  if (!birthDateInput) {
    console.warn("Birth date input not found");
    return;
  }

  const today = new Date();
  const todayStr = today.toISOString().split("T")[0];

  // Calculate min date (100 years ago from today)
  const minDate = new Date();
  minDate.setFullYear(today.getFullYear() - 100);
  const minDateStr = minDate.toISOString().split("T")[0];

  // Set HTML5 constraints
  birthDateInput.setAttribute("max", todayStr);
  birthDateInput.setAttribute("min", minDateStr);

  /**
   * Validate birth date value
   * @param {string} dateValue - Date string to validate
   * @returns {object} - Validation result {isValid: boolean, message: string}
   */
  function validateBirthDate(dateValue) {
    if (!dateValue) {
      return { isValid: true, message: "" };
    }

    const selectedDate = new Date(dateValue);
    const todayDate = new Date(todayStr);
    const minDateObj = new Date(minDateStr);

    if (selectedDate > todayDate) {
      return {
        isValid: false,
        message: "Birth date cannot be in the future!",
      };
    }

    if (selectedDate < minDateObj) {
      return {
        isValid: false,
        message: "Birth date cannot be more than 100 years ago!",
      };
    }

    return { isValid: true, message: "" };
  }

  /**
   * Display validation error
   * @param {string} message - Error message to display
   */
  function showError(message) {
    const errorDiv = document.getElementById("birthDateError");
    if (errorDiv) {
      birthDateInput.classList.add("is-invalid");
      errorDiv.textContent = message;
      errorDiv.style.display = "block";
    }
  }

  /**
   * Clear validation error
   */
  function clearError() {
    const errorDiv = document.getElementById("birthDateError");
    if (errorDiv) {
      birthDateInput.classList.remove("is-invalid");
      errorDiv.textContent = "";
      errorDiv.style.display = "none";
    }
  }

  // Validate on change
  birthDateInput.addEventListener("change", function () {
    const validation = validateBirthDate(this.value);

    if (!validation.isValid) {
      showError(validation.message);
    } else {
      clearError();
    }
  });

  // Validate on form submit
  const form = document.querySelector("form");
  if (form) {
    form.addEventListener("submit", function (e) {
      const validation = validateBirthDate(birthDateInput.value);

      if (!validation.isValid) {
        e.preventDefault();
        showError(validation.message);
        birthDateInput.focus();
        alert("Please correct the birth date. " + validation.message);
        return false;
      }
    });
  }
});
