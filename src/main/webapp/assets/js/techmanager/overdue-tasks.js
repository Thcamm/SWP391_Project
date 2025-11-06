/**
 * Overdue Tasks - JavaScript
 * Handles cancellation confirmation modal
 */

// Prepare cancellation modal
function prepareCancellation(assignmentId, vehicleInfo, technicianName) {
  document.getElementById('modalAssignmentId').value = assignmentId;
  document.getElementById('modalTaskId').textContent = '#' + assignmentId;
  document.getElementById('modalVehicle').textContent = vehicleInfo;
  document.getElementById('modalTechnician').textContent = technicianName;
}

// Initialize event listeners when DOM is ready
document.addEventListener('DOMContentLoaded', function () {
  // Auto-dismiss alerts after 5 seconds
  setTimeout(function () {
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach((alert) => {
      const bsAlert = new bootstrap.Alert(alert);
      bsAlert.close();
    });
  }, 5000);
});
