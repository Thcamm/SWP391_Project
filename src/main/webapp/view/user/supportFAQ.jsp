<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Frequently Asked Questions</title>
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/supportFAQ.css"> --%>
    <style>
        /* === Global Styles === */
        :root {
            --primary-color: #007bff;
            --primary-hover: #0056b3;
            --bg-color: #f4f7f9;
            --card-bg: #ffffff;
            --text-color: #333;
            --border-color: #e0e0e0;
            --shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: var(--bg-color);
            color: var(--text-color);
            line-height: 1.6;
        }

        .container {
            max-width: 900px;
            margin: 40px auto;
            padding: 20px 30px 40px 30px;
            background-color: var(--card-bg);
            border-radius: 12px;
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            color: var(--primary-color);
            margin-bottom: 30px;
            font-size: 2.25rem;
            font-weight: 700;
        }

        hr {
            border: 0;
            border-top: 1px solid var(--border-color);
            margin: 30px 0;
        }

        /* === Search Form === */
        form {
            display: flex;
            gap: 12px;
            margin-bottom: 25px;
        }

        form input[type="text"] {
            flex-grow: 1;
            padding: 12px 18px;
            font-size: 1rem;
            border: 2px solid var(--border-color);
            border-radius: 8px;
            transition: border-color 0.3s, box-shadow 0.3s;
        }

        form input[type="text"]:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.15);
        }

        form button,
        form a {
            padding: 12px 20px;
            font-size: 1rem;
            font-weight: 600;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            white-space: nowrap;
            transition: background-color 0.3s, color 0.3s, opacity 0.3s;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        form button[type="submit"] {
            background-color: var(--primary-color);
            color: #fff;
            border: none;
        }

        form button[type="submit"]:hover {
            background-color: var(--primary-hover);
        }

        form a {
            background-color: #6c757d;
            color: #fff;
        }

        form a:hover {
            background-color: #5a6268;
        }

        /* === FAQ List === */
        ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        li {
            margin-bottom: 12px;
        }

        li a {
            display: block;
            padding: 18px 25px;
            background-color: #fdfdfd;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            text-decoration: none;
            color: var(--text-color);
            font-weight: 500;
            font-size: 1.05rem;
            transition: all 0.3s ease;
            position: relative;
            padding-right: 50px; /* Space for the arrow */
        }

        li a:hover {
            background-color: #f0f7ff;
            border-color: var(--primary-color);
            color: var(--primary-hover);
            transform: translateY(-2px);
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
        }

        /* Arrow icon for links */
        li a::after {
            content: '›';
            position: absolute;
            right: 25px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 2rem;
            font-weight: 300;
            color: var(--primary-color);
            transition: right 0.3s ease;
        }

        li a:hover::after {
            right: 20px;
        }

        /* === Empty Message === */
        .empty-message {
            text-align: center;
            font-size: 1.1rem;
            color: #777;
            padding: 40px 0;
            font-style: italic;
        }

        /* === Help Widget (FAB) === */
        .help-widget {
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 1000;
        }

        .help-btn {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background-color: var(--primary-color);
            color: white;
            border: none;
            font-size: 2.25rem; /* Icon size */
            line-height: 60px; /* Center icon */
            text-align: center;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            transition: all 0.3s ease;
            padding: 0; /* Reset padding */
        }

        .help-btn:hover {
            background-color: var(--primary-hover);
            transform: scale(1.05);
        }

        .help-options {
            position: absolute;
            bottom: 75px; /* Above the button */
            right: 0;
            background-color: var(--card-bg);
            border-radius: 8px;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            width: 220px;
            /* Hidden by default - JS will toggle this */
            display: none;
            opacity: 0;
            transform: translateY(10px);
            transition: opacity 0.3s ease, transform 0.3s ease;
        }

        /* Class to be added by JS */
        .help-options.show {
            display: block;
            opacity: 1;
            transform: translateY(0);
        }

        .help-options a {
            display: block;
            padding: 15px 20px;
            text-decoration: none;
            color: var(--text-color);
            font-weight: 500;
            transition: background-color 0.3s;
        }

        .help-options a:hover {
            background-color: #f4f4f4;
        }

        /* === Responsive === */
        @media (max-width: 960px) {
            .container {
                margin: 20px;
                padding: 20px;
            }
        }

        @media (max-width: 600px) {
            h2 {
                font-size: 1.75rem;
            }
            form {
                flex-direction: column;
            }
            form button,
            form a {
                width: 100%;
            }

            li a {
                padding: 15px;
                padding-right: 45px; /* Adjust for arrow */
                font-size: 1rem;
            }

            li a::after {
                right: 15px;
            }

            .help-widget {
                bottom: 20px;
                right: 20px;
            }

            .help-btn {
                width: 55px;
                height: 55px;
                line-height: 55px;
                font-size: 2rem;
            }

            .help-options {
                bottom: 65px;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Frequently Asked Questions</h2>

    <form method="get" action="support-faq">
        <input type="text" name="q" placeholder="Search for a question..." value="${param.q}">
        <button type="submit">Search</button>
        <a href="support-faq">Reset</a>
    </form>

    <hr>

    <c:if test="${empty faqs}">
        <p class="empty-message">No matching questions found.</p>
    </c:if>

    <ul>
        <c:forEach var="faq" items="${faqs}">
            <li>
                <a href="${pageContext.request.contextPath}/support-faq?id=${faq.FAQId}">
                        ${faq.question}
                </a>
            </li>
        </c:forEach>
    </ul>
</div>

<div class="help-widget">
    <button id="helpToggle" class="help-btn">💡</button>
    <div id="helpOptions" class="help-options">
        <a href="${pageContext.request.contextPath}/app/create-support-request" title="Submit Request">Submit Request</a>
    </div>
</div>

<%--
    Giả định rằng tệp supportFAQ.js của bạn có logic
    để bật/tắt class "show" trên #helpOptions khi #helpToggle được nhấp.
    Nếu chưa, đây là một ví dụ đơn giản:
--%>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const toggleButton = document.getElementById('helpToggle');
        const optionsMenu = document.getElementById('helpOptions');

        if (toggleButton && optionsMenu) {
            toggleButton.addEventListener('click', function(event) {
                event.stopPropagation(); // Ngăn sự kiện click lan ra ngoài
                optionsMenu.classList.toggle('show');
            });

            // Tùy chọn: Đóng menu khi nhấp ra ngoài
            document.addEventListener('click', function(event) {
                if (optionsMenu.classList.contains('show') && !optionsMenu.contains(event.target)) {
                    optionsMenu.classList.remove('show');
                }
            });
        }
    });
</script>
<%-- <script src="${pageContext.request.contextPath}/assets/js/user/supportFAQ.js"></script> --%>

</body>
</html>