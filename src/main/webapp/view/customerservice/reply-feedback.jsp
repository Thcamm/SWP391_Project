<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Feedback Management</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/reply-feedback.css">
    <style>
        .custom-tabs {
            border-bottom: 2px solid #dee2e6 !important;
        }

        .custom-tabs .nav-link {
            color: #6c757d !important;
            border: none !important;
            border-bottom: 2px solid transparent !important;
            margin-bottom: -2px !important;
            padding: 0.75rem 1rem !important;
            font-weight: 500 !important;
            transition: all 0.2s ease !important;
        }

        .custom-tabs .nav-link:hover {
            color: #0d6efd !important;
            border-bottom-color: #0d6efd !important;
            background-color: transparent !important;
        }

        .custom-tabs .nav-link.active {
            color: #0d6efd !important;
            border-bottom-color: #0d6efd !important;
            background-color: transparent !important;
        }

    </style>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           align-items: center; justify-content: center;">
                    <!-- Page content -->
                    <div class="container-fluid bg-light min-vh-100 py-4">
                        <div class="container-custom mx-auto px-3 px-md-4">

                            <!-- Header -->
                            <div class="mb-4">
                                <h1 class="page-title text-dark mb-2">Customer Feedback Management</h1>
                                <p class="text-muted">View and respond to customer feedback</p>
                            </div>
                            <div id="forbiddenWordsManager" class="mb-4 p-4 border rounded shadow-sm bg-white">
                                <h5>Manage Forbidden Words</h5>
                                <div class="mb-2">
                                    <input type="text" id="newForbiddenWord" class="form-control" placeholder="Enter a new forbidden word">
                                    <button class="btn btn-primary mt-2" onclick="addForbiddenWord()">Add Word</button>
                                </div>
                                <ul id="forbiddenWordsList" class="list-group">
                                    <!-- Danh sách từ cấm sẽ hiển thị ở đây -->
                                </ul>
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
                                                    <p class="text-muted mb-1 stat-label">Total Feedback</p>
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
                                                    <p class="text-muted mb-1 stat-label">Pending</p>
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
                                                    <p class="text-muted mb-1 stat-label">Replied</p>
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
                                        All (<span id="allTabCount">0</span>)
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending"
                                            type="button" role="tab" onclick="filterFeedbacks('pending')">
                                        Pending (<span id="pendingTabCount">0</span>)
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" id="replied-tab" data-bs-toggle="tab" data-bs-target="#replied"
                                            type="button" role="tab" onclick="filterFeedbacks('replied')">
                                        Replied (<span id="repliedTabCount">0</span>)
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
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

<script>
    const contextPath = '<%= request.getContextPath() %>';
</script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customerservice/reply-feedback.js"></script>

</body>
</html>
