document.getElementById("roleId").addEventListener("change", function () {
  const selectedRole = this.options[this.selectedIndex].text;
  const employeeCodeField = document.getElementById("employeeCode");

  if (selectedRole && selectedRole !== "-- Choose Role --") {
    // Generate suggested employee code
    const rolePrefix = selectedRole.substring(0, 3).toUpperCase();
    const timestamp = new Date().getTime().toString().slice(-4);
    const suggestedCode = rolePrefix + timestamp;

    employeeCodeField.placeholder = `${suggestedCode}`;
  } else {
    employeeCodeField.placeholder = "Automatically generated";
  }
});

// Form validation
document
  .getElementById("employeeForm")
  .addEventListener("submit", function (e) {
    const salary = document.getElementById("salary").value;
    if (salary && salary < 0) {
      alert("Salary cannot be negative");
      e.preventDefault();
      return false;
    }
  });
