/**
 * assign-repair.js
 * JavaScript for Assign Repair Tasks page
 */

// Set minimum datetime to now for all plannedStart inputs
document.addEventListener('DOMContentLoaded', function() {
  const now = new Date();
  now.setMinutes(0, 0, 0);
  now.setHours(now.getHours() + 1);
  
  const minDateTime = now.toISOString().slice(0, 16);
  
  // Set min attribute and default value
  document.querySelectorAll('input[name="plannedStart"]').forEach(input => {
    input.setAttribute('min', minDateTime);
    const defaultStart = new Date(now);
    defaultStart.setHours(defaultStart.getHours() + 1);
    input.value = defaultStart.toISOString().slice(0, 16);
  });
  
  document.querySelectorAll('input[name="plannedEnd"]').forEach(input => {
    input.setAttribute('min', minDateTime);
  });
  
  // Form validation
  document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', function(e) {
      const plannedStart = this.querySelector('input[name="plannedStart"]');
      const plannedEnd = this.querySelector('input[name="plannedEnd"]');
      const currentTime = new Date();
      
      if (plannedStart && plannedStart.value) {
        const startTime = new Date(plannedStart.value);
        if (startTime < currentTime) {
          e.preventDefault();
          alert('⚠️ Planned Start Time cannot be in the past!');
          plannedStart.focus();
          return false;
        }
      }
      
      if (plannedEnd && plannedEnd.value) {
        const endTime = new Date(plannedEnd.value);
        if (endTime < currentTime) {
          e.preventDefault();
          alert('⚠️ Planned End Time cannot be in the past!');
          plannedEnd.focus();
          return false;
        }
        
        if (plannedStart && plannedStart.value) {
          const startTime = new Date(plannedStart.value);
          if (endTime <= startTime) {
            e.preventDefault();
            alert('⚠️ Planned End must be after Planned Start!');
            plannedEnd.focus();
            return false;
          }
        }
      }
    });
  });
});

// Validate planned times on change
function validatePlannedTimes(detailId) {
  const plannedStart = document.getElementById('plannedStart_' + detailId);
  const plannedEnd = document.getElementById('plannedEnd_' + detailId);
  
  if (!plannedStart || !plannedEnd) return;
  
  const startValue = plannedStart.value;
  const endValue = plannedEnd.value;
  
  if (startValue && endValue) {
    const startTime = new Date(startValue);
    const endTime = new Date(endValue);
    
    if (endTime <= startTime) {
      plannedEnd.setCustomValidity('End time must be after start time');
      plannedEnd.classList.add('is-invalid');
    } else {
      plannedEnd.setCustomValidity('');
      plannedEnd.classList.remove('is-invalid');
    }
  } else {
    plannedEnd.setCustomValidity('');
    plannedEnd.classList.remove('is-invalid');
  }
}

// Load technician schedule via AJAX
function loadSchedule(detailId) {
  const technicianId = document.getElementById('technicianId_' + detailId).value;
  const plannedStart = document.getElementById('plannedStart_' + detailId).value;
  
  if (!technicianId || !plannedStart) return;
  
  const date = plannedStart.split('T')[0];
  // Get context path from script tag
  const scriptSrc = document.currentScript?.src || document.querySelector('script[src*="assign-repair.js"]')?.src || '';
  const contextPath = scriptSrc.split('/assets')[0] || '';
  const url = contextPath + '/techmanager/technician-schedule?technicianId=' + technicianId + '&date=' + date;
  
  fetch(url)
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        displaySchedule(detailId, data);
      } else {
        console.error('Failed to load schedule:', data.error);
      }
    })
    .catch(error => console.error('Error:', error));
}

// Display schedule in preview area
function displaySchedule(detailId, data) {
  const schedulePreview = document.getElementById('schedulePreview_' + detailId);
  const scheduleContent = document.getElementById('scheduleContent_' + detailId);

  if (data.totalTasks === 0) {
    scheduleContent.innerHTML = '<span class="text-success"><i class="bi bi-check-circle"></i> No tasks scheduled. Technician is available!</span>';
  } else {
    let html = '<div class="table-responsive"><table class="table table-sm table-bordered">';
    html += '<thead><tr><th>Time</th><th>Type</th><th>Vehicle</th><th>Status</th></tr></thead><tbody>';
    data.tasks.forEach(task => {
      const statusBadge = task.isOverdue ? '<span class="badge bg-danger">Overdue</span>' : '<span class="badge bg-info">Scheduled</span>';
      html += '<tr><td>' + task.plannedStart + ' - ' + task.plannedEnd + '</td>';
      html += '<td>' + task.taskType + '</td>';
      html += '<td>' + task.vehicleInfo + '</td>';
      html += '<td>' + statusBadge + '</td></tr>';
    });
    html += '</tbody></table></div>';
    scheduleContent.innerHTML = html;
  }

  schedulePreview.classList.remove('d-none');
}

// Refresh schedule
function refreshSchedule(detailId) {
  loadSchedule(detailId);
}
