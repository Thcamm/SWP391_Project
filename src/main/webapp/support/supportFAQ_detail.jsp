<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết câu hỏi</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
            min-height: 100vh;
            padding: 40px 20px;
            color: #333;
            line-height: 1.6;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background: #fff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
        }

        h2 {
            color: #2c3e50;
            font-size: 2rem;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e0e0e0;
            font-weight: 600;
        }

        h2::before {
            content: "Q: ";
            color: #666;
            font-weight: 400;
        }

        p {
            color: #555;
            font-size: 1.1rem;
            margin-bottom: 30px;
            text-align: justify;
            padding: 20px;
            background: #fafafa;
            border-left: 4px solid #2c3e50;
            border-radius: 4px;
        }

        .back-link {
            display: inline-block;
            padding: 12px 24px;
            background: #2c3e50;
            color: #fff;
            text-decoration: none;
            border-radius: 6px;
            transition: all 0.3s ease;
            font-weight: 500;
        }

        .back-link:hover {
            background: #34495e;
            transform: translateX(-5px);
            box-shadow: 0 4px 12px rgba(44, 62, 80, 0.2);
        }

        .back-link::before {
            content: "← ";
            margin-right: 5px;
        }

        @media (max-width: 768px) {
            .container {
                padding: 25px;
            }

            h2 {
                font-size: 1.5rem;
            }

            p {
                font-size: 1rem;
                padding: 15px;
            }
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .container {
            animation: fadeIn 0.5s ease;
        }
        .help-widget {
            position: fixed;
            bottom: 25px;
            right: 25px;
            z-index: 9999;
        }

        .help-btn {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            font-size: 28px;
            border: none;
            cursor: pointer;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
            transition: all 0.3s ease;
        }

        .help-btn:hover {
            transform: scale(1.1);
            box-shadow: 0 6px 25px rgba(102, 126, 234, 0.5);
        }

        .help-options {
            display: none;
            flex-direction: column;
            position: absolute;
            bottom: 70px;
            right: 0;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            animation: fadeInUp 0.3s ease forwards;
        }

        .help-options.open {
            display: flex;
        }

        .help-options a {
            padding: 12px 20px;
            color: #333;
            text-decoration: none;
            font-weight: 500;
            transition: background 0.2s ease;
            border-bottom: 1px solid #eee;
        }

        .help-options a:hover {
            background: #f5f5f5;
        }

        .help-options a:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>${faqDetail.question}</h2>
    <p>${faqDetail.answer}</p>
    <a href="support-faq" class="back-link">Quay lại danh sách</a>
</div>
<div class="help-widget">
    <button id="helpToggle" class="help-btn">💡</button>
    <div id="helpOptions" class="help-options">
        <a href="#" title="Chatbot">💬 Chat bot</a>
        <a href="tel:19001234" title="Hotline">📞 Hotline</a>
        <a href="mailto:support@garage.vn" title="Send email">📧 Send email</a>
        <a href="${pageContext.request.contextPath}/create-support-request" title="Send Request">❓ Send Request</a>
    </div>
</div>

<script>
    const helpToggle = document.getElementById("helpToggle");
    const helpOptions = document.getElementById("helpOptions");

    helpToggle.addEventListener("click", () => {
        helpOptions.classList.toggle("open");
    });

    // Đóng menu khi click ra ngoài
    document.addEventListener("click", (e) => {
        if (!helpOptions.contains(e.target) && !helpToggle.contains(e.target)) {
            helpOptions.classList.remove("open");
        }
    });
</script>
</body>
</html>