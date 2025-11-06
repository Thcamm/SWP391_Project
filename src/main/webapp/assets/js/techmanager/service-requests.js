/**
 * Service Requests - JavaScript
 * Handles modal population for approve and reject actions
 */

// Initialize event listeners when DOM is ready
document.addEventListener('DOMContentLoaded', function () {
  // Populate Approve Modal
  const approveModal = document.getElementById('approveModal');
  if (approveModal) {
    approveModal.addEventListener('show.bs.modal', function (event) {
      const button = event.relatedTarget;
      document.getElementById('approveRequestId').value = button.getAttribute('data-request-id');
      document.getElementById('approveCustomer').textContent = button.getAttribute('data-customer');
      document.getElementById('approveVehicle').textContent = button.getAttribute('data-vehicle');
      document.getElementById('approveService').textContent = button.getAttribute('data-service');
      document.getElementById('taskDescription').value = 'Initial service: ' + button.getAttribute('data-service');
    });
  }

  // Populate Reject Modal
  const rejectModal = document.getElementById('rejectModal');
  if (rejectModal) {
    rejectModal.addEventListener('show.bs.modal', function (event) {
      const button = event.relatedTarget;
      document.getElementById('rejectRequestId').value = button.getAttribute('data-request-id');
    });
  }
});
