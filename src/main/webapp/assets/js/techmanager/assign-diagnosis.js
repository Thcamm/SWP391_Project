/**
 * Assign Diagnosis - JavaScript
 * Handles assignment preparation, schedule loading via AJAX
 */

// Prepare assignment modal
function prepareAssignment(button) {
    const detailId = button.getAttribute('data-detail-id');
    const workOrderId = button.getAttribute('data-wo-id');
    const vehicleInfo = button.getAttribute('data-vehicle');
    const taskDesc = button.getAttribute('data-task');
    
    document.getElementById('modalDetailId').value = detailId;
    document.getElementById('modalWorkOrderInfo').textContent = 'WorkOrder #' + workOrderId;
    document.getElementById('modalVehicleInfo').textContent = vehicleInfo;
    document.getElementById('modalTaskDesc').textContent = taskDesc;
    
    // Reset scheduling fields
    document.getElementById('plannedStart').value = '';
    document.getElementById('plannedEnd').value = '';
    document.getElementById('schedulePreview').classList.add('d-none');
}

// Load technician schedule via AJAX
function loadTechnicianSchedule() {
    const technicianId = document.getElementById('technicianId').value;
    const plannedStart = document.getElementById('plannedStart').value;
    
    if (!technicianId || !plannedStart) return;
    
    const date = plannedStart.split('T')[0]; // Extract date part
    const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
    const url = contextPath + '/techmanager/technician-schedule?technicianId=' + technicianId + '&date=' + date;
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                displaySchedule(data);
            } else {
                console.error('Failed to load schedule:', data.message);
            }
        })
        .catch(error => console.error('Error:', error));
}

// Display schedule data
function displaySchedule(data) {
    const schedulePreview = document.getElementById('schedulePreview');
    const scheduleContent = document.getElementById('scheduleContent');
    
    if (data.totalTasks === 0) {
        scheduleContent.innerHTML = '<span class="text-success"><i class="bi bi-check-circle"></i> No tasks scheduled for this date. Technician is available!</span>';
    } else {
        let html = '<div class="table-responsive"><table class="table table-sm table-bordered">';
        html += '<thead><tr><th>Time</th><th>Task Type</th><th>Vehicle</th><th>Status</th></tr></thead><tbody>';
        data.tasks.forEach(task => {
            const statusBadge = task.isOverdue ? '<span class="badge bg-danger">Overdue</span>' : '<span class="badge bg-info">Scheduled</span>';
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

// Refresh schedule
function refreshSchedule() {
    loadTechnicianSchedule();
}

// Initialize event listeners when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Auto-load schedule when date changes
    const plannedStartInput = document.getElementById('plannedStart');
    if (plannedStartInput) {
        plannedStartInput.addEventListener('change', loadTechnicianSchedule);
    }
    
    // Auto-dismiss alerts after 5 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
});
