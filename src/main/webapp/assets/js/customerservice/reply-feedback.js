// State
let feedbacks = [];
let currentFilter = 'all';

// Forbidden words list (can be modified)
const forbiddenWords = ["badword1", "badword2", "badword3"];

// Function to censor forbidden words
const censorText = (text) => {
    if (!text) return '';
    let censored = text;
    forbiddenWords.forEach(word => {
        const regex = new RegExp(word, "gi"); // case-insensitive
        censored = censored.replace(regex, "***");
    });
    return censored;
};

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    if (typeof contextPath === 'undefined') contextPath = '';
    console.log('[INIT] contextPath:', contextPath);
    fetchFeedbacks();
});

// Fetch feedbacks
function fetchFeedbacks() {
    console.log('[FETCH] Loading feedbacks...');
    fetch(`${contextPath}/api/feedback-list`)
        .then(res => {
            console.log('[FETCH] Status:', res.status);
            if (!res.ok) throw new Error('Status: ' + res.status);
            return res.json();
        })
        .then(data => {
            console.log('[FETCH] Data received:', data);

            feedbacks = data.map(f => ({
                id: f.feedbackID,
                customerID: f.customerID,
                // Show "Anonymous Customer" if anonymous, otherwise show ID
                customerName: f.isanonymous == 1 ? 'Anonymous Customer' : `Customer ID: ${f.customerID}`,
                workOrderID: f.workOrderID,
                rating: f.rating || 0,
                comment: censorText(f.feedbackText || ''),
                date: f.feedbackDate,
                status: f.Status ? f.Status.toLowerCase() : 'pending',
                response: f.replyText ? {
                    text: censorText(f.replyText),
                    date: f.replyDate,
                    staff: f.repliedBy || 'Customer Service'
                } : null
            }));

            loadFeedbacks();
            updateStats();
        })

        .catch(err => {
            console.error('[FETCH] Error fetching feedbacks:', err);
            feedbacks = [];
            loadFeedbacks();
            updateStats();
        });
}

// Load feedbacks
function loadFeedbacks() {
    const feedbackList = document.getElementById('feedbackList');
    const filtered = filterFeedbacksByStatus(currentFilter);

    console.log('[LOAD] Rendering', filtered.length, 'feedbacks');
    if (filtered.length === 0) {
        feedbackList.innerHTML = renderEmptyState();
    } else {
        feedbackList.innerHTML = filtered.map(renderFeedbackCard).join('');
    }
}

// Filter helpers
function filterFeedbacks(status) {
    currentFilter = status;
    loadFeedbacks();
}
function filterFeedbacksByStatus(status) {
    if (status === 'pending') return feedbacks.filter(f => f.status === 'pending');
    if (status === 'replied') return feedbacks.filter(f => f.status === 'replied');
    return feedbacks;
}

// Update statistics
function updateStats() {
    const total = feedbacks.length;
    const pending = feedbacks.filter(f => f.status === 'pending').length;
    const replied = feedbacks.filter(f => f.status === 'replied').length;

    console.log(`[STATS] Total: ${total}, Pending: ${pending}, Replied: ${replied}`);

    ['totalCount', 'pendingCount', 'repliedCount'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = id === 'totalCount' ? total : (id === 'pendingCount' ? pending : replied);
    });

    ['allTabCount', 'pendingTabCount', 'repliedTabCount'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = id === 'allTabCount' ? total : (id === 'pendingTabCount' ? pending : replied);
    });
}

// Format date
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${String(date.getDate()).padStart(2,'0')}/${String(date.getMonth()+1).padStart(2,'0')}/${date.getFullYear()} ${String(date.getHours()).padStart(2,'0')}:${String(date.getMinutes()).padStart(2,'0')}`;
}

// Render feedback
function renderFeedbackCard(f) {
    const badge = f.status === 'replied' ? '<span class="badge badge-replied">Replied</span>' : '<span class="badge badge-pending">Pending</span>';
    const stars = Array.from({length:5}, (_, i)=>`<i class="fas fa-star ${i<f.rating?'star-filled':'star-empty'}"></i>`).join('');
    const responseHtml = f.response ? renderResponse(f.response) : renderReplyForm(f.id);

    return `
    <div class="card feedback-card border shadow-sm mb-3">
        <div class="card-body p-4">
            <div class="feedback-header d-flex justify-content-between align-items-start">
                <div class="customer-info">
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <span class="customer-name">${f.customerName}</span>
                        ${badge}
                    </div>
                    <div class="customer-meta">
                        <div><i class="fas fa-envelope"></i> ${f.customerEmail || 'N/A'}</div>
                        <div><i class="fas fa-calendar"></i> ${formatDate(f.date)}</div>
                    </div>
                </div>
                <div class="rating-stars">${stars}</div>
            </div>
            <div class="feedback-comment"><p class="mb-0">${f.comment}</p></div>
            ${responseHtml}
        </div>
    </div>
    `;
}

function renderResponse(resp) {
    return `
    <div class="response-section">
        <div class="response-display">
            <div class="response-header d-flex align-items-start gap-2">
                <i class="fas fa-comment-dots"></i>
                <div>
                    <div class="response-meta"><strong>Response from ${resp.staff}</strong> • ${formatDate(resp.date)}</div>
                    <div class="response-text">${resp.text}</div>
                </div>
            </div>
        </div>
    </div>
    `;
}

// Reply form
function renderReplyForm(id) {
    return `
    <div class="response-section">
        <div class="reply-form" id="replyForm-${id}">
            <textarea id="replyText-${id}" class="reply-textarea" rows="4" placeholder="Enter your reply..."></textarea>
            <div class="reply-actions">
                <button class="btn btn-submit-reply" onclick="submitReply(${id}, this)">Send Reply</button>
                <button class="btn btn-outline-secondary btn-cancel-reply" onclick="cancelReply(${id})">Cancel</button>
            </div>
        </div>
        <button class="btn btn-outline-primary btn-reply" id="replyBtn-${id}" onclick="showReplyForm(${id})">
            <i class="fas fa-comment-dots me-2"></i> Reply to Feedback
        </button>
    </div>
    `;
}

// Show / cancel
function showReplyForm(id){
    console.log('[SHOW] Reply form for', id);
    document.getElementById(`replyForm-${id}`).classList.add('active');
    document.getElementById(`replyBtn-${id}`).style.display='none';
    document.getElementById(`replyText-${id}`).focus();
}
function cancelReply(id){
    console.log('[CANCEL] Reply form for', id);
    const form = document.getElementById(`replyForm-${id}`);
    const btn = document.getElementById(`replyBtn-${id}`);
    const txt = document.getElementById(`replyText-${id}`);
    form.classList.remove('active');
    btn.style.display='block';
    txt.value='';
}
function submitReply(feedbackId, btn) {
    const textarea = document.getElementById(`replyText-${feedbackId}`);
    const replyText = textarea.value.trim();

    console.log('[DEBUG] Feedback form data before submit:');
    console.log('feedbackId:', feedbackId);
    console.log('replyText:', replyText);

    if (!replyText) {
        alert('Please enter a reply');
        return;
    }

    const formData = new FormData();
    formData.append('feedbackID', feedbackId);
    formData.append('replyText', replyText);

    fetch(`${contextPath}/api/feedback-reply`, {
        method: 'POST',
        body: formData
    })
        .then(res => {
            console.log('[DEBUG] Response status:', res.status);
            if (!res.ok) throw new Error('Status: ' + res.status);
            return res.json();
        })
        .then(data => {
            console.log('[DEBUG] Response data:', data);
            if (data.success) {
                const feedback = feedbacks.find(f => f.id === feedbackId);
                if (feedback) {
                    feedback.status = 'replied';
                    feedback.response = {
                        text: censorText(replyText),
                        date: new Date().toISOString(),
                        staff: 'You'
                    };
                }
                loadFeedbacks();
                updateStats();
                showToast('Reply sent successfully!');
            } else {
                throw new Error(data.message || 'An error occurred');
            }
        })
        .catch(err => {
            console.error('[SUBMIT ERROR]', err);
            alert('An error occurred while sending the reply: ' + err.message);
        });
}

// Empty state
function renderEmptyState(){
    return `<div class="empty-state text-center py-5"><i class="fas fa-inbox fa-2x mb-2"></i><p>No feedback available</p></div>`;
}

// Toast
function showToast(msg){
    console.log('[TOAST]', msg);
    const toast = document.createElement('div');
    toast.className='position-fixed bottom-0 end-0 p-3';
    toast.style.zIndex='9999';
    toast.innerHTML = `<div class="toast show" role="alert">
        <div class="toast-header bg-success text-white">
            <i class="fas fa-check-circle me-2"></i>
            <strong class="me-auto">Success</strong>
            <button type="button" class="btn-close btn-close-white" onclick="this.closest('.position-fixed').remove()"></button>
        </div>
        <div class="toast-body">${msg}</div>
    </div>`;
    document.body.appendChild(toast);
    setTimeout(()=>toast.remove(), 3000);
}
