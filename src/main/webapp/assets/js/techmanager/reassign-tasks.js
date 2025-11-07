/**
 * Reassign Tasks - JavaScript
 * Handles technician schedule loading for reassignment
 */

// Load technician schedule for specific task
function loadTechSchedule(taskId) {
  const techId = document.getElementById('techSelect_' + taskId).value;
  const startTime = document.getElementById('startInput_' + taskId).value;

  if (!techId || !startTime) return;

  const date = startTime.split('T')[0];
  const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
  const url = contextPath + '/techmanager/technician-schedule?technicianId=' + techId + '&date=' + date;

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        displayTechSchedule(taskId, data);
      }
    })
    .catch((error) => console.error('Error:', error));
}

// Display technician schedule
function displayTechSchedule(taskId, data) {
  const scheduleDiv = document.getElementById('scheduleDiv_' + taskId);
  const scheduleContent = document.getElementById('scheduleContent_' + taskId);

  if (data.totalTasks === 0) {
    scheduleContent.innerHTML =
      '<span class="text-success"><i class="bi bi-check-circle"></i> Available - No conflicts!</span>';
  } else {
    let html = '<div class="table-responsive"><table class="table table-sm table-bordered">';
    html += '<thead><tr><th>Time</th><th>Type</th><th>Vehicle</th></tr></thead><tbody>';
    data.tasks.forEach((task) => {
      html += '<tr><td>' + task.plannedStart + ' - ' + task.plannedEnd + '</td>';
      html += '<td>' + task.taskType + '</td>';
      html += '<td>' + task.vehicleInfo + '</td></tr>';
    });
    html += '</tbody></table></div>';
    scheduleContent.innerHTML = html;
  }

  scheduleDiv.classList.remove('d-none');
}

// Refresh technician schedule
function refreshTechSchedule(taskId) {
  loadTechSchedule(taskId);
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
