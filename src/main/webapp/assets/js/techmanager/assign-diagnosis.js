// Lấy context-path từ thẻ meta
const CONTEXT_PATH = document.querySelector('meta[name="context-path"]').getAttribute('content');

/**
 * Chuẩn bị modal khi nhấn nút "Assign".
 * Chuyển dữ liệu từ data-* của nút vào các trường trong form modal.
 */
function prepareAssignment(button) {
  const detailId = button.getAttribute('data-detail-id');
  const woId = button.getAttribute('data-wo-id');
  const vehicle = button.getAttribute('data-vehicle');
  const task = button.getAttribute('data-task');

  // Điền dữ liệu vào modal
  document.getElementById('modalDetailId').value = detailId;
  document.getElementById('modalWorkOrderInfo').textContent = `#${woId}`;
  document.getElementById('modalVehicleInfo').textContent = vehicle;
  document.getElementById('modalTaskDesc').textContent = task;

  // Reset form (xóa dữ liệu cũ)
  const form = document.getElementById('assignForm');
  form.reset();

  // Đặt lại giá trị default cho 'plannedStart' (quan trọng)
  setDefaultStartTime();

  // Ẩn lịch cũ
  document.getElementById('schedulePreview').classList.add('d-none');
  document.getElementById('scheduleContent').innerHTML = '';
}

/**
 * Thiết lập giá trị mặc định và 'min' cho trường plannedStart
 */
function setDefaultStartTime() {
  const startInput = document.getElementById('plannedStart');
  if (!startInput) return;

  const now = new Date();
  // Thêm 1 phút buffer để tránh lỗi validation do server/client time skew
  now.setMinutes(now.getMinutes() + 1);
  const localDateTimeMin = now.toISOString().slice(0, 16);

  // Ngăn chọn ngày quá khứ
  startInput.setAttribute('min', localDateTimeMin);

  // Tự động set giá trị mặc định là NOW + 2 giờ
  const defaultStartTime = new Date(now.getTime() + 120 * 60000); // +2 giờ
  startInput.value = defaultStartTime.toISOString().slice(0, 16);
}

/**
 * Tải lịch của KTV qua AJAX (logic tối giản)
 */
function loadTechnicianSchedule() {
  const technicianId = document.getElementById('technicianId').value;
  const plannedStartInput = document.getElementById('plannedStart');

  const schedulePreview = document.getElementById('schedulePreview');
  const scheduleContent = document.getElementById('scheduleContent');

  // Nếu không chọn KTV hoặc ngày, ẩn lịch
  if (!technicianId || !plannedStartInput.value) {
    schedulePreview.classList.add('d-none');
    scheduleContent.innerHTML = '';
    return;
  }

  const date = plannedStartInput.value.split('T')[0];
  const url = `${CONTEXT_PATH}/techmanager/technician-schedule?technicianId=${technicianId}&date=${date}`;

  scheduleContent.innerHTML = '<span class="text-muted small">Loading schedule...</span>';
  schedulePreview.classList.remove('d-none');

  fetch(url)
    .then((response) => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then((data) => {
      if (data.success) {
        displaySchedule(scheduleContent, data);
      } else {
        scheduleContent.innerHTML = `<span class="text-danger small">Error: ${data.error}</span>`;
      }
    })
    .catch((error) => {
      console.error('Error fetching schedule:', error);
      scheduleContent.innerHTML = '<span class="text-danger small">Could not load schedule.</span>';
    });
}

/**
 * Hiển thị lịch (schedule) vào modal
 */
function displaySchedule(scheduleContentElement, data) {
  if (!data.tasks || data.tasks.length === 0) {
    scheduleContentElement.innerHTML =
      '<span class="text-success small"><i class="bi bi-check-circle"></i> No tasks scheduled. Technician appears available!</span>';
    return;
  }

  let html = '<div class="table-responsive"><table class="table table-sm table-bordered" style="font-size: 0.85em;">';
  html += '<thead class="table-light"><tr><th>Time</th><th>Type</th><th>Status</th></tr></thead><tbody>';

  data.tasks.forEach((task) => {
    const statusBadge = task.isOverdue
      ? '<span class="badge bg-danger">Overdue</span>'
      : `<span class="badge bg-info">${task.status}</span>`;
    html += `<tr>
                    <td>${task.plannedStart} - ${task.plannedEnd}</td>
                    <td>${task.taskType}</td>
                    <td>${statusBadge}</td>
                 </tr>`;
  });

  html += '</tbody></table></div>';
  scheduleContentElement.innerHTML = html;
}

/**
 * Wrapper cho nút Refresh
 */
function refreshSchedule() {
  loadTechnicianSchedule();
}

/**
 * Kiểm tra logic thời gian (End > Start)
 */
function validatePlannedTimes() {
  const startInput = document.getElementById('plannedStart');
  const endInput = document.getElementById('plannedEnd');

  // Reset validation
  startInput.classList.remove('is-invalid');
  endInput.classList.remove('is-invalid');

  const startTime = new Date(startInput.value);
  const now = new Date();

  // 1. Kiểm tra Giờ quá khứ (dù đã có 'min', vẫn check lại)
  if (startTime < now) {
    startInput.classList.add('is-invalid');
    return false;
  }

  // 2. Kiểm tra End > Start
  if (endInput.value && startInput.value) {
    const endTime = new Date(endInput.value);
    if (endTime <= startTime) {
      endInput.classList.add('is-invalid');
      return false;
    }
  }
  return true;
}

/**
 * Kiểm tra toàn bộ Form trước khi Submit
 */
function validateForm(form) {
  const isValid = validatePlannedTimes();
  if (!isValid) {
    // Ngăn form submit
    return false;
  }
  // (Thêm các validation khác nếu cần)
  return true; // Cho phép submit
}

// Tự động gọi setDefaultStartTime khi modal được mở
document.getElementById('assignModal').addEventListener('show.bs.modal', function () {
  setDefaultStartTime();
});
