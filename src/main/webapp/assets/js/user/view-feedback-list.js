// Mock data - Dữ liệu đánh giá mẫu
const mockReviews = [
    {
        id: 1,
        customerName: 'Nguyễn Văn A',
        rating: 5,
        comment: 'Dịch vụ tuyệt vời! Nhân viên nhiệt tình, tư vấn chi tiết. Xe được sửa chữa rất nhanh và chất lượng. Giá cả hợp lý, tôi rất hài lòng và sẽ quay lại.',
        date: '2024-11-04',
        services: ['Bảo dưỡng định kỳ', 'Thay dầu'],
        images: [
            'https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=400',
            'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=400'
        ],
        isAnonymous: false,
        detailedRatings: { quality: 100, price: 90, attitude: 100, time: 90 }
    },
    {
        id: 2,
        customerName: 'Khách hàng ẩn danh',
        rating: 4,
        comment: 'Dịch vụ tốt, nhân viên chuyên nghiệp. Thời gian chờ hơi lâu một chút nhưng chất lượng công việc rất tốt.',
        date: '2024-11-03',
        services: ['Sửa chữa'],
        images: [],
        isAnonymous: true,
        detailedRatings: { quality: 90, price: 80, attitude: 90, time: 70 }
    },
    {
        id: 3,
        customerName: 'Trần Thị B',
        rating: 5,
        comment: 'Lần đầu đến garage này và tôi rất ấn tượng. Mọi thứ đều hoàn hảo từ A-Z. Sẽ giới thiệu bạn bè đến đây!',
        date: '2024-11-02',
        services: ['Kiểm tra tổng quát'],
        images: ['https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=400'],
        isAnonymous: false,
        detailedRatings: { quality: 100, price: 100, attitude: 100, time: 100 }
    },
    {
        id: 4,
        customerName: 'Lê Văn C',
        rating: 3,
        comment: 'Dịch vụ bình thường, giá hơi cao so với mặt bằng chung. Nhân viên thân thiện nhưng cần cải thiện thêm về kỹ thuật.',
        date: '2024-11-01',
        services: ['Thay thế phụ tùng'],
        images: [],
        isAnonymous: false,
        detailedRatings: { quality: 60, price: 50, attitude: 80, time: 70 }
    },
    {
        id: 5,
        customerName: 'Phạm Thị D',
        rating: 5,
        comment: 'Garage uy tín, làm việc chuyên nghiệp. Tôi đã sử dụng dịch vụ nhiều lần và luôn hài lòng.',
        date: '2024-10-31',
        services: ['Bảo dưỡng định kỳ'],
        images: [
            'https://images.unsplash.com/photo-1625047509248-ec889cbff17f?w=400',
            'https://images.unsplash.com/photo-1487754180451-c456f719a1fc?w=400'
        ],
        isAnonymous: false,
        detailedRatings: { quality: 90, price: 90, attitude: 100, time: 90 }
    },
    {
        id: 6,
        customerName: 'Khách hàng ẩn danh',
        rating: 4,
        comment: 'Tốt, nhưng cần cải thiện thời gian chờ đợi. Công việc được thực hiện cẩn thận.',
        date: '2024-10-30',
        services: ['Sơn xe'],
        images: [],
        isAnonymous: true,
        detailedRatings: { quality: 85, price: 80, attitude: 85, time: 60 }
    },
    {
        id: 7,
        customerName: 'Hoàng Văn E',
        rating: 2,
        comment: 'Không hài lòng lắm. Thời gian sửa quá lâu và giá cao hơn báo giá ban đầu.',
        date: '2024-10-29',
        services: ['Sửa chữa'],
        images: [],
        isAnonymous: false,
        detailedRatings: { quality: 50, price: 30, attitude: 60, time: 40 }
    },
    {
        id: 8,
        customerName: 'Vũ Thị F',
        rating: 5,
        comment: 'Xuất sắc! Đây là garage tốt nhất mà tôi từng đến. Mọi người rất chuyên nghiệp và nhiệt tình.',
        date: '2024-10-28',
        services: ['Kiểm tra tổng quát', 'Bảo dưỡng định kỳ'],
        images: ['https://images.unsplash.com/photo-1580273916550-e323be2ae537?w=400'],
        isAnonymous: false,
        detailedRatings: { quality: 100, price: 95, attitude: 100, time: 95 }
    },
    {
        id: 9,
        customerName: 'Khách hàng ẩn danh',
        rating: 4,
        comment: 'Dịch vụ khá tốt, giá cả hợp lý. Sẽ quay lại lần sau.',
        date: '2024-10-27',
        services: ['Thay dầu'],
        images: [],
        isAnonymous: true,
        detailedRatings: { quality: 80, price: 85, attitude: 80, time: 80 }
    },
    {
        id: 10,
        customerName: 'Đỗ Văn G',
        rating: 1,
        comment: 'Rất thất vọng. Xe sửa xong vẫn còn vấn đề. Không recommend.',
        date: '2024-10-26',
        services: ['Sửa chữa'],
        images: [],
        isAnonymous: false,
        detailedRatings: { quality: 20, price: 30, attitude: 40, time: 30 }
    },
    {
        id: 11,
        customerName: 'Bùi Thị H',
        rating: 5,
        comment: 'Tuyệt vời! Nhân viên rất chu đáo, giải thích rõ ràng từng vấn đề của xe.',
        date: '2024-10-25',
        services: ['Kiểm tra tổng quát'],
        images: ['https://images.unsplash.com/photo-1502877338535-766e1452684a?w=400'],
        isAnonymous: false,
        detailedRatings: { quality: 100, price: 90, attitude: 100, time: 90 }
    },
    {
        id: 12,
        customerName: 'Lý Văn I',
        rating: 3,
        comment: 'Tạm được, không có gì đặc biệt. Giá hơi cao.',
        date: '2024-10-24',
        services: ['Bảo dưỡng định kỳ'],
        images: [],
        isAnonymous: false,
        detailedRatings: { quality: 70, price: 50, attitude: 70, time: 70 }
    },
];

// State Management
let state = {
    reviews: mockReviews,
    filteredReviews: mockReviews,
    searchTerm: '',
    selectedRating: 'all', // 'all' hoặc 1-5
    sortBy: 'newest', // 'newest', 'oldest', 'highest', 'lowest'
    currentPage: 1,
    itemsPerPage: 6
};

// Utility Functions
const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
};

const calculateStats = () => {
    const total = state.reviews.length;
    const ratingCounts = [0, 0, 0, 0, 0];
    let totalRating = 0;

    state.reviews.forEach(review => {
        ratingCounts[review.rating - 1]++;
        totalRating += review.rating;
    });

    const avgRating = (totalRating / total).toFixed(1);

    return {
        total,
        avgRating,
        ratingCounts: ratingCounts.reverse() // [5, 4, 3, 2, 1]
    };
};

// Filter and Sort Functions
const filterAndSortReviews = () => {
    let filtered = state.reviews.filter(review => {
        const searchLower = state.searchTerm.toLowerCase();
        const matchesSearch =
            review.comment.toLowerCase().includes(searchLower) ||
            review.customerName.toLowerCase().includes(searchLower) ||
            review.services.some(s => s.toLowerCase().includes(searchLower));

        const matchesRating =
            state.selectedRating === 'all' ||
            review.rating === parseInt(state.selectedRating);

        return matchesSearch && matchesRating;
    });

    // Sort
    filtered.sort((a, b) => {
        switch (state.sortBy) {
            case 'newest':
                return new Date(b.date) - new Date(a.date);
            case 'oldest':
                return new Date(a.date) - new Date(b.date);
            case 'highest':
                return b.rating - a.rating;
            case 'lowest':
                return a.rating - b.rating;
            default:
                return 0;
        }
    });

    state.filteredReviews = filtered;
    state.currentPage = 1; // Reset to first page when filtering
};

// Render Functions
const renderStatsOverview = () => {
    const stats = calculateStats();

    // Update overall rating
    document.getElementById('avgRating').textContent = stats.avgRating;
    document.getElementById('totalReviews').textContent = stats.total;

    // Render rating distribution
    const distributionHtml = [5, 4, 3, 2, 1].map((rating, index) => {
        const count = stats.ratingCounts[index];
        const percentage = stats.total > 0 ? (count / stats.total) * 100 : 0;
        const isActive = state.selectedRating === rating.toString();

        return `
            <button 
                class="rating-bar-button w-full flex items-center gap-3 p-2 rounded-lg ${isActive ? 'active ring-2 ring-gray-900 bg-gray-100' : ''}"
                onclick="handleRatingFilter(${rating})"
            >
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

    // Rating filter
    if (state.selectedRating !== 'all') {
        filters.push(`
            <span class="badge badge-filter" onclick="clearRatingFilter()">
                ${state.selectedRating} sao ✕
            </span>
        `);
    }

    // Search filter
    if (state.searchTerm) {
        filters.push(`
            <span class="badge badge-filter" onclick="clearSearchFilter()">
                "${state.searchTerm}" ✕
            </span>
        `);
    }

    // Clear all button
    if (filters.length > 0) {
        filters.push(`
            <button class="clear-filters text-base" onclick="clearAllFilters()">
                Xóa tất cả
            </button>
        `);
    }

    document.getElementById('activeFilters').innerHTML = filters.join('');
};

const renderResultCount = () => {
    const start = (state.currentPage - 1) * state.itemsPerPage + 1;
    const end = Math.min(start + state.itemsPerPage - 1, state.filteredReviews.length);
    const total = state.filteredReviews.length;

    document.getElementById('resultCount').textContent =
        `Hiển thị ${start}-${end} / ${total} đánh giá`;
};

const renderReviewCard = (review) => {
    const stars = Array(5).fill(0).map((_, i) => {
        const filled = i < review.rating;
        return `<i data-lucide="star" class="w-4 h-4 ${filled ? 'text-yellow-400 fill-yellow-400' : 'text-gray-200 fill-gray-200'}"></i>`;
    }).join('');

    const servicesHtml = review.services.map(service =>
        `<span class="badge badge-outline">${service}</span>`
    ).join('');

    const imagesHtml = review.images.length > 0 ? `
        <div class="flex gap-2 mb-4">
            ${review.images.slice(0, 3).map(img => `
                <div class="review-image">
                    <img src="${img}" alt="Review image" loading="lazy" />
                </div>
            `).join('')}
            ${review.images.length > 3 ? `
                <div class="review-image bg-gray-100 flex items-center justify-center">
                    <span class="text-gray-600 font-medium">+${review.images.length - 3}</span>
                </div>
            ` : ''}
        </div>
    ` : '';

    return `
        <div class="review-card bg-white rounded-2xl border border-gray-200 shadow-lg p-6">
            <!-- Header -->
            <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3">
                    <div class="review-avatar">
                        ${review.isAnonymous ? '🔒' : review.customerName.charAt(0)}
                    </div>
                    <div>
                        <p class="text-base font-medium text-gray-900">${review.customerName}</p>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="flex gap-0.5">
                                ${stars}
                            </div>
                            <span class="text-gray-400">•</span>
                            <span class="text-sm text-gray-500">${formatDate(review.date)}</span>
                        </div>
                    </div>
                </div>
                
                ${review.isAnonymous ? '<span class="badge badge-anonymous">Ẩn danh</span>' : ''}
            </div>

            <!-- Services -->
            <div class="flex flex-wrap gap-2 mb-3">
                ${servicesHtml}
            </div>

            <!-- Comment -->
            <p class="text-base text-gray-700 mb-4 line-clamp-3">${review.comment}</p>

            <!-- Images -->
            ${imagesHtml}

            <!-- Detailed Ratings -->
            <div class="pt-4 border-t border-gray-200">
                <div class="grid grid-cols-2 gap-3">
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600">Chất lượng</span>
                        <span class="badge badge-secondary">${review.detailedRatings.quality}%</span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600">Giá cả</span>
                        <span class="badge badge-secondary">${review.detailedRatings.price}%</span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600">Thái độ</span>
                        <span class="badge badge-secondary">${review.detailedRatings.attitude}%</span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="text-sm text-gray-600">Thời gian</span>
                        <span class="badge badge-secondary">${review.detailedRatings.time}%</span>
                    </div>
                </div>
            </div>
        </div>
    `;
};

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

        reviewsGrid.innerHTML = paginatedReviews.map(review =>
            renderReviewCard(review)
        ).join('');
    }

    // Reinitialize Lucide icons
    lucide.createIcons();
};

const renderPagination = () => {
    const totalPages = Math.ceil(state.filteredReviews.length / state.itemsPerPage);

    if (totalPages <= 1) {
        document.getElementById('pagination').innerHTML = '';
        return;
    }

    let pagesHtml = [];

    // Previous button
    pagesHtml.push(`
        <button 
            class="pagination-button flex items-center gap-2"
            onclick="changePage(${state.currentPage - 1})"
            ${state.currentPage === 1 ? 'disabled' : ''}
        >
            <i data-lucide="chevron-left" class="w-4 h-4"></i>
        </button>
    `);

    // Page numbers
    for (let i = 1; i <= totalPages; i++) {
        if (
            i === 1 ||
            i === totalPages ||
            (i >= state.currentPage - 1 && i <= state.currentPage + 1)
        ) {
            pagesHtml.push(`
                <button 
                    class="pagination-button ${i === state.currentPage ? 'active' : ''}"
                    onclick="changePage(${i})"
                >
                    ${i}
                </button>
            `);
        } else if (i === state.currentPage - 2 || i === state.currentPage + 2) {
            pagesHtml.push('<span class="px-2 text-gray-400">...</span>');
        }
    }

    // Next button
    pagesHtml.push(`
        <button 
            class="pagination-button flex items-center gap-2"
            onclick="changePage(${state.currentPage + 1})"
            ${state.currentPage === totalPages ? 'disabled' : ''}
        >
            <i data-lucide="chevron-right" class="w-4 h-4"></i>
        </button>
    `);

    document.getElementById('pagination').innerHTML = pagesHtml.join('');
    lucide.createIcons();
};

// Main render function
const render = () => {
    renderStatsOverview();
    renderActiveFilters();
    renderResultCount();
    renderReviews();
    renderPagination();
};

// Event Handlers
const handleSearch = (event) => {
    state.searchTerm = event.target.value;
    filterAndSortReviews();
    render();
};

const handleSort = (event) => {
    state.sortBy = event.target.value;
    filterAndSortReviews();
    render();
};

const handleRatingFilter = (rating) => {
    if (state.selectedRating === rating.toString()) {
        state.selectedRating = 'all';
    } else {
        state.selectedRating = rating.toString();
    }
    filterAndSortReviews();
    render();
};

const clearRatingFilter = () => {
    state.selectedRating = 'all';
    filterAndSortReviews();
    render();
};

const clearSearchFilter = () => {
    state.searchTerm = '';
    document.getElementById('searchInput').value = '';
    filterAndSortReviews();
    render();
};

const clearAllFilters = () => {
    state.selectedRating = 'all';
    state.searchTerm = '';
    document.getElementById('searchInput').value = '';
    filterAndSortReviews();
    render();
};

const changePage = (page) => {
    const totalPages = Math.ceil(state.filteredReviews.length / state.itemsPerPage);
    if (page >= 1 && page <= totalPages) {
        state.currentPage = page;
        render();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
};

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // Setup event listeners
    document.getElementById('searchInput').addEventListener('input', handleSearch);
    document.getElementById('sortSelect').addEventListener('change', handleSort);

    // Initial render
    filterAndSortReviews();
    render();
});

// Debounce function for search
const debounce = (func, wait) => {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
};

// Export functions for testing or external use (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        calculateStats,
        filterAndSortReviews,
        formatDate
    };
}
