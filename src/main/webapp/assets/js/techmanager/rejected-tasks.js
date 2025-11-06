/**
 * Rejected Tasks - JavaScript
 * Handles task reassignment navigation
 */

// Reassign task navigation
function reassignTask(assignmentId) {
  if (confirm('Reassign this task to another technician?')) {
    const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
    window.location.href = contextPath + '/techmanager/assign-diagnosis?reassign=' + assignmentId;
  }
}
