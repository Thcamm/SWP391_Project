/**
 * Assign Repair - JavaScript
 * Handles repair assignment preparation, per-task schedule loading via AJAX
 */

// Prepare repair assignment modal
function prepareRepairAssignment(button) {
  const detailId = button.getAttribute('data-detail-id');
  const workOrderId = button.getAttribute('data-wo-id');
  const vehicleInfo = button.getAttribute('data-vehicle');
  const serviceDesc = button.getAttribute('data-service');
  const estHours = button.getAttribute('data-hours');
  const estAmount = button.getAttribute('data-amount');

  document.getElementById('modalDetailId_' + detailId).value = detailId;
  document.getElementById('modalWorkOrderInfo_' + detailId).textContent = 'WorkOrder #' + workOrderId;
  document.getElementById('modalVehicleInfo_' + detailId).textContent = vehicleInfo;
  document.getElementById('modalServiceDesc_' + detailId).textContent = serviceDesc;
  document.getElementById('modalEstimate_' + detailId).textContent = estHours + 'h / $' + estAmount;

  // Reset scheduling fields
  document.getElementById('plannedStart_' + detailId).value = '';
  document.getElementById('plannedEnd_' + detailId).value = '';
  document.getElementById('schedulePreview_' + detailId).classList.add('d-none');
}

// Load technician schedule for specific task (detailId)
function loadSchedule(detailId) {
  const technicianId = document.getElementById('technicianId_' + detailId).value;
  const plannedStart = document.getElementById('plannedStart_' + detailId).value;

  if (!technicianId || !plannedStart) return;

  const date = plannedStart.split('T')[0]; // Extract date part
  const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
  const url = contextPath + '/techmanager/technician-schedule?technicianId=' + technicianId + '&date=' + date;

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        displaySchedule(detailId, data);
      } else {
        console.error('Failed to load schedule:', data.message);
      }
    })
    .catch((error) => console.error('Error:', error));
}

// Display schedule data for specific task
function displaySchedule(detailId, data) {
  const schedulePreview = document.getElementById('schedulePreview_' + detailId);
  const scheduleContent = document.getElementById('scheduleContent_' + detailId);

  if (data.totalTasks === 0) {
    scheduleContent.innerHTML =
      '<span class="text-success"><i class="bi bi-check-circle"></i> No tasks scheduled for this date. Technician is available!</span>';
  } else {
    let html = '<div class="table-responsive"><table class="table table-sm table-bordered">';
    html += '<thead><tr><th>Time</th><th>Task Type</th><th>Vehicle</th><th>Status</th></tr></thead><tbody>';
    data.tasks.forEach((task) => {
      const statusBadge = task.isOverdue
        ? '<span class="badge bg-danger">Overdue</span>'
        : '<span class="badge bg-info">Scheduled</span>';
      html += `<tr>
                <td>${task.plannedStart} - ${task.plannedEnd}</td>
                <td>${task.taskType}</td>
                <td>${task.vehicleInfo}</td>
                <td>${statusBadge}</td>
            </tr>`;
    });
    html += '</tbody></table></div>';
    scheduleContent.innerHTML = html;
  }

  schedulePreview.classList.remove('d-none');
}

// Refresh schedule for specific task
function refreshSchedule(detailId) {
  loadSchedule(detailId);
}

// Initialize event listeners when DOM is ready
document.addEventListener('DOMContentLoaded', function () {
  // Auto-dismiss alerts after 5 seconds
  setTimeout(function () {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach((alert) => {
      const bsAlert = new bootstrap.Alert(alert);
      bsAlert.close();
    });
  }, 5000);
});
