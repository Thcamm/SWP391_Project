// State Management
let state = {
    reviews: [],
    filteredReviews: [],
    searchTerm: '',
    selectedRating: 'all', // 'all' hoặc 1-5
    sortBy: 'newest', // 'newest', 'oldest', 'highest', 'lowest'
    currentPage: 1,
    itemsPerPage: 6
};

// Danh sách từ cấm (có thể thêm/bớt tùy ý)
const forbiddenWords = ["cc", "badword2", "badword3"];

// Hàm lọc text
const censorText = (text) => {
    if (!text) return '';
    let censored = text;
    forbiddenWords.forEach(word => {
        const regex = new RegExp(word, "gi"); // không phân biệt hoa thường
        censored = censored.replace(regex, "***");
    });
    return censored;
};

// Utility Functions
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
};

const calculateStats = () => {
    const total = state.reviews.length;
    const ratingCounts = [0, 0, 0, 0, 0]; // index 0 = 1*, index 4 = 5*
    let totalRating = 0;

    state.reviews.forEach(review => {
        ratingCounts[review.rating - 1]++;
        totalRating += review.rating;
    });

    const avgRating = total > 0 ? (totalRating / total).toFixed(1) : 0;

    return {
        total,
        avgRating,
        ratingCounts: ratingCounts.reverse() // [5,4,3,2,1]
    };
};

// Filter and Sort
const filterAndSortReviews = () => {
    let filtered = state.reviews.filter(review => {
        const searchLower = state.searchTerm.toLowerCase();
        const matchesSearch =
            review.feedbackText.toLowerCase().includes(searchLower) ||
            review.customerName.toLowerCase().includes(searchLower) ||
            (review.replyText && review.replyText.toLowerCase().includes(searchLower));

        const matchesRating =
            state.selectedRating === 'all' ||
            review.rating === parseInt(state.selectedRating);

        return matchesSearch && matchesRating;
    });

    filtered.sort((a, b) => {
        switch (state.sortBy) {
            case 'newest': return new Date(b.feedbackDate) - new Date(a.feedbackDate);
            case 'oldest': return new Date(a.feedbackDate) - new Date(b.feedbackDate);
            case 'highest': return b.rating - a.rating;
            case 'lowest': return a.rating - b.rating;
            default: return 0;
        }
    });

    state.filteredReviews = filtered;
    state.currentPage = 1;
};

// Render Functions
const renderStatsOverview = () => {
    const stats = calculateStats();
    document.getElementById('avgRating').textContent = stats.avgRating;
    document.getElementById('totalReviews').textContent = stats.total;

    const distributionHtml = [5,4,3,2,1].map((rating, index) => {
        const count = stats.ratingCounts[index];
        const percentage = stats.total > 0 ? (count / stats.total) * 100 : 0;
        const isActive = state.selectedRating === rating.toString();

        return `
            <button class="rating-bar-button w-full flex items-center gap-3 p-2 rounded-lg ${isActive ? 'active ring-2 ring-gray-900 bg-gray-100' : ''}"
                onclick="handleRatingFilter(${rating})">
                <div class="flex items-center gap-1 w-16">
                    <span class="text-base text-gray-700 font-medium">${rating}</span>
                    <i data-lucide="star" class="w-4 h-4 text-yellow-400 fill-yellow-400"></i>
                </div>
                <div class="flex-1 rating-bar">
                    <div class="rating-bar-fill" style="width: ${percentage}%"></div>
                </div>
                <span class="w-12 text-base text-gray-600 text-right">${count}</span>
            </button>
        `;
    }).join('');

    document.getElementById('ratingDistribution').innerHTML = distributionHtml;
    lucide.createIcons();
};

const renderActiveFilters = () => {
    const filters = [];
    if (state.selectedRating !== 'all') filters.push(`<span class="badge badge-filter" onclick="clearRatingFilter()">${state.selectedRating} star ✕</span>`);
    if (state.searchTerm) filters.push(`<span class="badge badge-filter" onclick="clearSearchFilter()">"${state.searchTerm}" ✕</span>`);
    if (filters.length > 0) filters.push(`<button class="clear-filters text-base" onclick="clearAllFilters()">Delete all</button>`);
    document.getElementById('activeFilters').innerHTML = filters.join('');
};

const renderResultCount = () => {
    const start = (state.currentPage - 1) * state.itemsPerPage + 1;
    const end = Math.min(start + state.itemsPerPage - 1, state.filteredReviews.length);
    const total = state.filteredReviews.length;
    document.getElementById('resultCount').textContent = `Show ${start}-${end} / ${total} review(s)`;
};

const renderReviewCard = (review) => {
    const stars = Array(5).fill(0).map((_, i) =>
        i < review.rating ?
            `<i data-lucide="star" class="w-4 h-4 text-yellow-400 fill-yellow-400"></i>` :
            `<i data-lucide="star" class="w-4 h-4 text-gray-200 fill-gray-200"></i>`
    ).join('');

    return `
        <div class="review-card bg-white rounded-2xl border border-gray-200 shadow-lg p-6">
            <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3">
                    <div class="review-avatar">${review.anonymous ? '🔒' : review.customerName.charAt(0)}</div>
                    <div>
                        <p class="text-base font-medium text-gray-900">${review.anonymous ? 'Anonymous' : review.customerName}</p>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="flex gap-0.5">${stars}</div>
                            <span class="text-gray-400">•</span>
                            <span class="text-sm text-gray-500">${formatDate(review.feedbackDate)}</span>
                        </div>
                    </div>
                </div>
                ${review.anonymous ? '<span class="badge badge-anonymous">Anonymous</span>' : ''}
            </div>

            <p class="text-base text-gray-700 mb-2 line-clamp-3">${censorText(review.feedbackText)}</p>

            ${review.replyText ? `
            <div class="reply-section mt-4 p-4 bg-gray-50 rounded-lg border-l-4 border-gray-300">
                <p class="text-sm text-gray-600 font-medium">Reply from the garage:</p>
                <p class="text-sm text-gray-700 mt-1">${censorText(review.replyText)}</p>
                <p class="text-xs text-gray-400 mt-1">${formatDate(review.replyDate)}</p>
            </div>
            ` : ''}
        </div>
    `;
};

// ...phần còn lại giữ nguyên hoàn toàn

const renderReviews = () => {
    const start = (state.currentPage - 1) * state.itemsPerPage;
    const end = start + state.itemsPerPage;
    const paginatedReviews = state.filteredReviews.slice(start, end);

    const reviewsGrid = document.getElementById('reviewsGrid');
    const emptyState = document.getElementById('emptyState');

    if (paginatedReviews.length === 0) {
        reviewsGrid.classList.add('hidden');
        emptyState.classList.remove('hidden');
    } else {
        reviewsGrid.classList.remove('hidden');
        emptyState.classList.add('hidden');
        reviewsGrid.innerHTML = paginatedReviews.map(renderReviewCard).join('');
    }

    lucide.createIcons();
};

const renderPagination = () => {
    const totalPages = Math.ceil(state.filteredReviews.length / state.itemsPerPage);
    if (totalPages <= 1) {
        document.getElementById('pagination').innerHTML = '';
        return;
    }

    let pagesHtml = [];
    pagesHtml.push(`<button class="pagination-button" onclick="changePage(${state.currentPage-1})" ${state.currentPage===1?'disabled':''}><i data-lucide="chevron-left" class="w-4 h-4"></i></button>`);

    for(let i=1;i<=totalPages;i++){
        if(i===1 || i===totalPages || (i>=state.currentPage-1 && i<=state.currentPage+1)){
            pagesHtml.push(`<button class="pagination-button ${i===state.currentPage?'active':''}" onclick="changePage(${i})">${i}</button>`);
        } else if(i===state.currentPage-2 || i===state.currentPage+2){
            pagesHtml.push('<span class="px-2 text-gray-400">...</span>');
        }
    }

    pagesHtml.push(`<button class="pagination-button" onclick="changePage(${state.currentPage+1})" ${state.currentPage===totalPages?'disabled':''}><i data-lucide="chevron-right" class="w-4 h-4"></i></button>`);

    document.getElementById('pagination').innerHTML = pagesHtml.join('');
    lucide.createIcons();
};

// Main render
const render = () => {
    renderStatsOverview();
    renderActiveFilters();
    renderResultCount();
    renderReviews();
    renderPagination();
};

// Event Handlers
const handleSearch = (event) => { state.searchTerm = event.target.value; filterAndSortReviews(); render(); };
const handleSort = (event) => { state.sortBy = event.target.value; filterAndSortReviews(); render(); };
const handleRatingFilter = (rating) => { state.selectedRating = (state.selectedRating===rating.toString())?'all':rating.toString(); filterAndSortReviews(); render(); };
const clearRatingFilter = () => { state.selectedRating='all'; filterAndSortReviews(); render(); };
const clearSearchFilter = () => { state.searchTerm=''; document.getElementById('searchInput').value=''; filterAndSortReviews(); render(); };
const clearAllFilters = () => { state.selectedRating='all'; state.searchTerm=''; document.getElementById('searchInput').value=''; filterAndSortReviews(); render(); };
const changePage = (page) => { const totalPages=Math.ceil(state.filteredReviews.length/state.itemsPerPage); if(page>=1 && page<=totalPages){ state.currentPage=page; render(); window.scrollTo({top:0,behavior:'smooth'}); } };

// Fetch data from API
const loadFeedbackData = async () => {
    try {
        const response = await fetch(`${contextPath}/api/feedback-list`);
        const data = await response.json();

        state.reviews = data.map(fb => ({
            feedbackID: fb.feedbackID,
            customerName: fb.anonymous ? 'Anonymous' : (fb.customerName || `Khách #${fb.customerID}`),
            feedbackText: fb.feedbackText || '',
            rating: fb.rating || 0,
            feedbackDate: fb.feedbackDate ? fb.feedbackDate.replace(' ', 'T') : null,
            anonymous: fb.anonymous || false,
            replyText: fb.replyText || '',
            replyDate: fb.replyDate ? fb.replyDate.replace(' ', 'T') : null
        }));

        filterAndSortReviews();
        render();
    } catch (error) {
        console.error('Failed to load feedback data', error);
    }
};

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('searchInput').addEventListener('input', handleSearch);
    document.getElementById('sortSelect').addEventListener('change', handleSort);
    loadFeedbackData();
});
