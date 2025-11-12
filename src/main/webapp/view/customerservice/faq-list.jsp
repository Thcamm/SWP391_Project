<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FAQ Management</title>
    <style>
        /* --- Biến màu (Dễ dàng thay đổi) --- */
        :root {
            --bs-blue: #0d6efd;
            --bs-green: #198754; /* Dịu hơn #28a745 */
            --bs-yellow: #ffc107;
            --bs-red: #dc3545;
            --bs-gray: #6c757d;
            --bs-border-color: #dee2e6;
            --bs-body-bg: #f8f9fa;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            /* Thêm font chữ mượt mà hơn */
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            background: var(--bs-body-bg);
        }

        /* --- Dọn dẹp Style Inline --- */
        .sidebar-col {
            width: 280px;
        }

        .main-content {
            padding: 1.25rem; /* 20px */
            padding-bottom: 0;
        }

        .content-card {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            /* Căn lề nhất quán */
            padding: 2.5rem;
            min-height: calc(100vh - 64px - 1.25rem);
            display: flex;
            flex-direction: column;
            /* Bỏ align-items: center để nội dung trải full-width */
        }

        h1 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 20px; /* Chuyển style inline vào đây */
            font-size: 28px;
            border-bottom: 1px solid #e5e7eb; /* Thêm đường kẻ phân cách */
        }

        /* --- Các style cũ của bạn --- */
        .alert {
            padding: 12px 20px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-success {
            background-color: #d1e7dd; /* Dịu hơn */
            color: #0f5132;
            border: 1px solid #badbcc;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            gap: 15px;
            flex-wrap: wrap;
        }

        .search-form {
            display: flex;
            gap: 10px;
            flex: 1;
            max-width: 500px;
        }

        .search-input {
            flex: 1;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 6px; /* Bo tròn đẹp hơn */
            font-size: 14px;
            transition: all 0.3s;
        }
        .search-input:focus {
            outline: none;
            border-color: var(--bs-blue);
            box-shadow: 0 0 0 3px rgba(13,110,253,0.15);
        }

        /* --- [SỬA] Màu Nút Dịu Hơn --- */
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 6px; /* Bo tròn đẹp hơn */
            cursor: pointer;
            font-size: 14px;
            font-weight: 500; /* Thêm độ đậm */
            text-decoration: none;
            display: inline-block;
            transition: all 0.2s;
        }

        .btn-primary {
            background: var(--bs-blue);
            color: white;
        }
        .btn-primary:hover { background: #0b5ed7; }

        .btn-success {
            background: var(--bs-green); /* Dùng màu đã định nghĩa */
            color: white;
        }
        .btn-success:hover { background: #157347; }

        .btn-warning {
            background: var(--bs-yellow);
            color: #212529;
        }
        .btn-warning:hover { background: #ffca2c; }

        .btn-danger {
            background: var(--bs-red);
            color: white;
        }
        .btn-danger:hover { background: #bb2d3b; }

        .btn-secondary {
            background: var(--bs-gray);
            color: white;
        }
        .btn-secondary:hover { background: #5a6268; }

        .btn-sm {
            padding: 6px 12px;
            font-size: 13px;
        }

        /* --- [SỬA] Bảng Responsive --- */
        .table-wrapper {
            width: 100%;
            overflow-x: auto; /* Thêm cuộn ngang cho bảng trên di động */
            border: 1px solid var(--bs-border-color);
            border-radius: 8px;
            margin-top: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            /* margin-top: 20px; Bỏ vì .table-wrapper đã có */
        }
        thead { background: #f8f9fa; }
        th {
            padding: 12px 15px; /* Thống nhất padding */
            text-align: left;
            font-weight: 600;
            color: #495057;
            border-bottom: 2px solid var(--bs-border-color);
        }
        td {
            padding: 12px 15px;
            border-bottom: 1px solid var(--bs-border-color);
            vertical-align: top;
        }
        tbody tr:last-child td {
            border-bottom: none; /* Bỏ border dòng cuối */
        }
        tbody tr:hover { background: #f8f9fa; }

        .question-col {
            font-weight: 500;
            color: #333;
            max-width: 300px;
        }
        .answer-col {
            color: #666;
            max-width: 400px;
            line-height: 1.5;
        }
        .actions {
            display: flex;
            gap: 8px;
            white-space: nowrap;
        }

        /* --- [MỚI] CSS CHO PHÂN TRANG --- */
        .pagination-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap; /* Cho di động */
            gap: 15px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
        }
        .pagination-info {
            font-size: 14px;
            color: #6c757d;
        }
        ul.pagination {
            display: flex;
            list-style: none;
            padding: 0;
            margin: 0;
            border-radius: 6px;
            overflow: hidden;
            border: 1px solid var(--bs-border-color);
        }
        li.page-item {
            /* Không cần style */
        }
        li.page-item:not(:last-child) a.page-link {
            border-right: 1px solid var(--bs-border-color);
        }
        a.page-link {
            display: block;
            padding: 8px 14px;
            color: var(--bs-blue);
            text-decoration: none;
            background: white;
            transition: all 0.2s;
        }
        li.page-item a.page-link:hover {
            background: #f4f4f4;
        }
        li.page-item.active a.page-link {
            background: var(--bs-blue);
            color: white;
            border-color: var(--bs-blue);
            pointer-events: none;
        }
        li.page-item.disabled a.page-link {
            color: #6c757d;
            pointer-events: none;
            background: #f8f9fa;
        }

        /* --- Các style cũ (giữ nguyên) --- */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
            border: 2px dashed var(--bs-border-color);
            border-radius: 8px;
            margin-top: 20px;
        }
        .empty-state-icon { font-size: 64px; margin-bottom: 20px; opacity: 0.3; }
        .empty-state-text { font-size: 18px; margin-bottom: 10px; }
        .empty-state-subtext { font-size: 14px; color: #999; }

        @media (max-width: 768px) {
            .toolbar { flex-direction: column; align-items: stretch; }
            .search-form { max-width: 100%; }
            .pagination-container { justify-content: center; } /* Căn giữa phân trang trên di động */
            .actions { flex-direction: column; }
        }

        .loading {
            opacity: 0.5;
            pointer-events: none;
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto sidebar-col">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <main class="main-content">
                <div class="content-card">

                    <h1> FAQ Edit</h1>

                    <c:if test="${param.success == 'added'}">
                        <div class="alert alert-success">Add FAQ successfully/div>
                    </c:if>
                    <c:if test="${param.success == 'updated'}">
                        <div class="alert alert-success">Update FAQ successfully</div>
                    </c:if>
                    <c:if test="${param.success == 'deleted'}">
                        <div class="alert alert-success"> Unactivate FAQ successfully</div>
                    </c:if>
                    <c:if test="${param.error == 'notfound'}">
                        <div class="alert alert-error"> Not find FAQ!</div>
                    </c:if>
                    <c:if test="${param.error == 'empty'}">
                        <div class="alert alert-error"> Question and answer must not be empty</div>
                    </c:if>

                    <div class="toolbar">
                        <div class="search-form">
                            <input type="text"
                                   id="searchInput"
                                   placeholder="Find question according to word..."
                                   class="search-input"
                                   value="${keyword}">
                            <button type="button" id="clearBtn" class="btn btn-secondary" style="display: none;">Xóa</button>
                        </div>
                        <a href="${pageContext.request.contextPath}/customerservice/faq?action=add"
                           class="btn btn-success">Add new FAQ</a>
                    </div>

                    <div id="faqTableContainer">
                        <c:choose>
                            <c:when test="${empty faqList}">
                                <div class="empty-state">
                                    <div class="empty-state-icon">📝</div>
                                    <div class="empty-state-text">No FAQ found</div>
                                    <div class="empty-state-subtext">
                                        <c:choose>
                                            <c:when test="${not empty keyword}">
                                                Try searching for another keyword or <a href="${pageContext.request.contextPath}/customerservice/faq">
                                                see all</a>
                                            </c:when>
                                            <c:otherwise>
                                                Start by adding first FAQ
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-wrapper">
                                    <table>
                                        <thead>
                                        <tr>
                                            <th style="width: 60px;">ID</th>
                                            <th style="width: 30%;">Question</th>
                                            <th style="width: 45%;">Answer</th>
                                            <th style="width: 160px;">Action</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="faq" items="${faqList}">
                                            <tr>
                                                <td>${faq.FAQId}</td>
                                                <td class="question-col"><c:out value="${faq.question}"/></td>
                                                <td class="answer-col"><c:out value="${faq.answer}"/></td>
                                                <td class="actions">
                                                    <a href="${pageContext.request.contextPath}/customerservice/faq?action=edit&id=${faq.FAQId}"
                                                       class="btn btn-warning btn-sm">Edit</a>
                                                    <a href="${pageContext.request.contextPath}/customerservice/faq?action=delete&id=${faq.FAQId}"
                                                       class="btn btn-danger btn-sm"
                                                       onclick="return confirm('Are you sure you want to delete this FAQ?')">Delete</a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty faqList && totalPages > 1}">
                        <div class="pagination-container">
                            <div class="pagination-info">
                                Trang ${currentPage} / ${totalPages} (Total: ${totalRecords} FAQs)
                            </div>

                            <ul class="pagination">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?${not empty keyword ? 'action=search&keyword='.concat(keyword).concat('&') : ''}page=1">
                                        «
                                    </a>
                                </li>

                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?${not empty keyword ? 'action=search&keyword='.concat(keyword).concat('&') : ''}page=${currentPage - 1}">
                                        ‹
                                    </a>
                                </li>

                                <c:forEach begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                                           end="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}"
                                           var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="?${not empty keyword ? 'action=search&keyword='.concat(keyword).concat('&') : ''}page=${i}">
                                                ${i}
                                        </a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?${not empty keyword ? 'action=search&keyword='.concat(keyword).concat('&') : ''}page=${currentPage + 1}">
                                        ›
                                    </a>
                                </li>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?${not empty keyword ? 'action=search&keyword='.concat(keyword).concat('&') : ''}page=${totalPages}">
                                        »
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:if>
                </div>

            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

<script>
    let debounceTimer;
    const searchInput = document.getElementById('searchInput');
    const clearBtn = document.getElementById('clearBtn');
    const container = document.getElementById('faqTableContainer');

    // Get current page from URL
    function getCurrentPage() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('page') || '1';
    }

    // Show/hide clear button
    function toggleClearButton() {
        clearBtn.style.display = searchInput.value.trim() ? 'block' : 'none';
    }

    // Debounced search function
    function performSearch() {
        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(() => {
            const keyword = searchInput.value.trim();
            let url;

            if (keyword) {
                url = '${pageContext.request.contextPath}/customerservice/faq?action=search&keyword=' + encodeURIComponent(keyword) + '&page=1';
            } else {
                url = '${pageContext.request.contextPath}/customerservice/faq?page=1';
            }

            // Add loading state
            container.classList.add('loading');

            // Navigate to search URL
            window.location.href = url;
        }, 500); // 500ms debounce delay
    }

    // Event listeners
    searchInput.addEventListener('input', () => {
        toggleClearButton();
        performSearch();
    });

    clearBtn.addEventListener('click', () => {
        searchInput.value = '';
        toggleClearButton();
        window.location.href = '${pageContext.request.contextPath}/customerservice/faq';
    });

    // Initialize clear button visibility
    toggleClearButton();
</script>
</body>
</html>