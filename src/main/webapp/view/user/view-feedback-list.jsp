<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đánh Giá Từ Khách Hàng - Garage Ô Tô</title>

    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>

    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest"></script>

    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/view-feedback-list.css">

    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        border: 'rgba(0, 0, 0, 0.1)',
                        input: 'transparent',
                        ring: 'rgba(0, 0, 0, 0.3)',
                        background: '#ffffff',
                        foreground: '#030213',
                        primary: {
                            DEFAULT: '#030213',
                            foreground: '#ffffff',
                        },
                        secondary: {
                            DEFAULT: '#f3f3f5',
                            foreground: '#030213',
                        },
                        muted: {
                            DEFAULT: '#ececf0',
                            foreground: '#717182',
                        },
                    },
                }
            }
        }
    </script>
</head>
<body class="bg-gradient-to-br from-gray-50 via-gray-100 to-gray-200 min-h-screen">
<!-- Hero Header -->
<div class="relative bg-gradient-to-r from-gray-900 via-gray-800 to-black py-16 overflow-hidden">
    <div class="absolute inset-0 opacity-20">
        <img
                src="https://images.unsplash.com/photo-1711386689622-1cda23e10217?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXIlMjBnYXJhZ2UlMjByZXBhaXJ8ZW58MXx8fHwxNzYyMzY4MjE5fDA&ixlib=rb-4.1.0&q=80&w=1080"
                alt="Garage background"
                class="w-full h-full object-cover"
        />
    </div>

    <div class="absolute top-10 right-10 w-32 h-32 bg-white/5 rounded-full blur-3xl"></div>
    <div class="absolute bottom-10 left-10 w-40 h-40 bg-white/5 rounded-full blur-3xl"></div>

    <div class="relative max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl font-semibold text-white mb-4">Đánh Giá Từ Khách Hàng</h1>
        <p class="text-lg text-gray-300 max-w-2xl mx-auto">
            Xem các phản hồi chân thực từ khách hàng đã sử dụng dịch vụ của chúng tôi
        </p>
    </div>
</div>

<!-- Stats Overview -->
<div class="max-w-7xl mx-auto px-4 -mt-10 relative z-10">
    <div class="bg-white rounded-2xl shadow-xl border border-gray-200 p-8 mb-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
            <!-- Overall Rating -->
            <div class="text-center md:border-r border-gray-200">
                <div class="flex items-center justify-center gap-2 mb-2">
                    <i data-lucide="star" class="w-8 h-8 text-yellow-400 fill-yellow-400"></i>
                    <span class="text-4xl font-semibold" id="avgRating">4.2</span>
                </div>
                <p class="text-base text-gray-600">Đánh giá trung bình</p>
                <p class="text-sm text-gray-400 mt-1"><span id="totalReviews">12</span> đánh giá</p>
            </div>

            <!-- Rating Distribution -->
            <div class="md:col-span-3">
                <div class="space-y-2" id="ratingDistribution">
                    <!-- Rating bars will be inserted here by JS -->
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Main Content -->
<div class="max-w-7xl mx-auto px-4 pb-12">
    <!-- Filters and Search -->
    <div class="mb-8 space-y-4">
        <div class="flex flex-col md:flex-row gap-4">
            <!-- Search -->
            <div class="flex-1 relative">
                <i data-lucide="search" class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400"></i>
                <input
                        type="text"
                        id="searchInput"
                        placeholder="Tìm kiếm theo nội dung, khách hàng, dịch vụ..."
                        class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:border-gray-900 focus:ring-2 focus:ring-gray-900 focus:outline-none bg-white"
                />
            </div>

            <!-- Sort -->
            <div class="relative w-full md:w-64">
                <select
                        id="sortSelect"
                        class="w-full px-4 py-3 pr-10 border border-gray-300 rounded-xl focus:border-gray-900 focus:ring-2 focus:ring-gray-900 focus:outline-none bg-white appearance-none cursor-pointer"
                >
                    <option value="newest">Mới nhất</option>
                    <option value="oldest">Cũ nhất</option>
                    <option value="highest">Đánh giá cao nhất</option>
                    <option value="lowest">Đánh giá thấp nhất</option>
                </select>
                <i data-lucide="arrow-up-down" class="absolute right-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none"></i>
            </div>
        </div>

        <!-- Active Filters -->
        <div class="flex items-center gap-3 flex-wrap">
            <div class="flex items-center gap-2 text-gray-600">
                <i data-lucide="filter" class="w-4 h-4"></i>
                <span class="text-base">Lọc:</span>
            </div>

            <div id="activeFilters" class="flex items-center gap-2 flex-wrap">
                <!-- Active filter badges will be inserted here -->
            </div>

            <div class="ml-auto text-gray-600 text-base" id="resultCount">
                Hiển thị 6 / 12 đánh giá
            </div>
        </div>
    </div>

    <!-- Reviews Grid -->
    <div id="reviewsGrid" class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <!-- Reviews will be inserted here by JS -->
    </div>

    <!-- Empty State -->
    <div id="emptyState" class="hidden bg-white rounded-2xl shadow-lg border border-gray-200 p-12 text-center">
        <i data-lucide="search" class="w-16 h-16 mx-auto mb-4 text-gray-400"></i>
        <p class="text-lg text-gray-600">Không tìm thấy đánh giá phù hợp</p>
        <p class="text-base text-gray-400 mt-2">Thử điều chỉnh bộ lọc hoặc tìm kiếm</p>
    </div>

    <!-- Pagination -->
    <div id="pagination" class="flex items-center justify-center gap-2">
        <!-- Pagination buttons will be inserted here by JS -->
    </div>
</div>

<!-- JavaScript -->
<script src="${pageContext.request.contextPath}/assets/js/user/view-feedback-list.js"></script>
<script>
    // Initialize Lucide icons
    lucide.createIcons();
</script>
</body>
</html>
