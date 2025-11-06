<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.google.gson.Gson" %>
<%
    // Lấy data từ request attribute (được set bởi servlet)
    List<?> feedbacksList = (List<?>) request.getAttribute("feedbacks");

    // Convert to JSON để JavaScript có thể sử dụng
    Gson gson = new Gson();
    String feedbacksJson = gson.toJson(feedbacksList != null ? feedbacksList : new ArrayList<>());
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đánh giá khách hàng</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Custom CSS -->
    <link rel="stylesheet" href="dashboard.css">

    <!-- Embed data from servlet -->
    <script type="text/javascript">
        // Data được truyền từ servlet
        var feedbacksData = <%= feedbacksJson %>;
    </script>
</head>
<body>
<div class="container-fluid bg-light min-vh-100 py-4">
    <div class="container-custom mx-auto px-3 px-md-4">

        <!-- Header -->
        <div class="mb-4">
            <h1 class="page-title text-dark mb-2">Quản lý đánh giá khách hàng</h1>
            <p class="text-muted">Xem và trả lời các phản hồi từ khách hàng</p>
        </div>

        <!-- Stats Cards -->
        <div class="row g-3 mb-4">
            <div class="col-12 col-md-4">
                <div class="card stat-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                            <div class="stat-icon bg-primary-light rounded">
                                <i class="fas fa-message text-primary"></i>
                            </div>
                            <div>
                                <p class="text-muted mb-1 stat-label">Tổng đánh giá</p>
                                <p class="stat-value mb-0" id="totalCount">0</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-12 col-md-4">
                <div class="card stat-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                            <div class="stat-icon bg-warning-light rounded">
                                <i class="fas fa-clock text-warning"></i>
                            </div>
                            <div>
                                <p class="text-muted mb-1 stat-label">Chờ trả lời</p>
                                <p class="stat-value mb-0" id="pendingCount">0</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-12 col-md-4">
                <div class="card stat-card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                            <div class="stat-icon bg-success-light rounded">
                                <i class="fas fa-check-circle text-success"></i>
                            </div>
                            <div>
                                <p class="text-muted mb-1 stat-label">Đã trả lời</p>
                                <p class="stat-value mb-0" id="repliedCount">0</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tabs -->
        <ul class="nav nav-tabs mb-4 custom-tabs" id="feedbackTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="all-tab" data-bs-toggle="tab" data-bs-target="#all"
                        type="button" role="tab" onclick="filterFeedbacks('all')">
                    Tất cả (<span id="allTabCount">0</span>)
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending"
                        type="button" role="tab" onclick="filterFeedbacks('pending')">
                    Chờ trả lời (<span id="pendingTabCount">0</span>)
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="replied-tab" data-bs-toggle="tab" data-bs-target="#replied"
                        type="button" role="tab" onclick="filterFeedbacks('replied')">
                    Đã trả lời (<span id="repliedTabCount">0</span>)
                </button>
            </li>
        </ul>

        <!-- Feedback List -->
        <div class="tab-content" id="feedbackTabContent">
            <div class="tab-pane fade show active" id="feedbackList">
                <!-- Feedback cards will be dynamically inserted here -->
            </div>
        </div>

    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom JS -->
<script src="dashboard.js"></script>
</body>
</html>
