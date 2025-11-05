<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty message}">
    <div id="popupOverlay"></div>

    <div id="popupMessage" class="${messageType}">
        <div class="icon-container">
            <c:choose>
                <c:when test="${messageType == 'success'}">
                    <i class="fa-solid fa-circle-check success-icon"></i>
                </c:when>
                <c:otherwise>
                    <i class="fa-solid fa-circle-xmark error-icon"></i>
                </c:otherwise>
            </c:choose>
        </div>
        <h3>${message}</h3>
        <p class="countdown">This message will disappear in <span id="countdown">3</span> seconds...</p>
        <button id="closeBtn">&times;</button>
    </div>

    <style>
        /* Overlay mờ nền */
        #popupOverlay {
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            background: rgba(0, 0, 0, 0.3);
            z-index: 999;
            animation: fadeIn 0.3s ease;
        }

        /* Hộp popup */
        #popupMessage {
            position: fixed;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            border-radius: 18px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            padding: 40px 60px;
            z-index: 1000;
            text-align: center;
            min-width: 380px;
            animation: slideDown 0.4s ease;
            font-family: "Segoe UI", sans-serif;
        }

        /* Phần icon */
        .icon-container {
            font-size: 60px;
            margin-bottom: 15px;
        }

        /* Icon màu và hiệu ứng */
        .success-icon {
            color: #2ECC71;
            animation: rotateIn 0.6s ease;
        }

        .error-icon {
            color: #E74C3C;
            animation: shake 0.5s ease;
        }

        /* Viền màu popup */
        .success {
            border-top: 6px solid #2ECC71;
        }

        .error {
            border-top: 6px solid #E74C3C;
        }

        #popupMessage h3 {
            font-size: 22px;
            margin: 10px 0;
            color: #333;
        }

        .countdown {
            color: #888;
            font-size: 14px;
            margin-top: 5px;
        }

        #closeBtn {
            position: absolute;
            top: 10px; right: 15px;
            background: none;
            border: none;
            font-size: 26px;
            color: #777;
            cursor: pointer;
        }

        #closeBtn:hover {
            color: #333;
        }

        /* Hiệu ứng xuất hiện popup */
        @keyframes slideDown {
            from { opacity: 0; transform: translate(-50%, -60%); }
            to { opacity: 1; transform: translate(-50%, -50%); }
        }

        @keyframes fadeIn {
            from { opacity: 0; } to { opacity: 1; }
        }

        /* Icon xoay khi thành công */
        @keyframes rotateIn {
            0% { transform: scale(0.3) rotate(-90deg); opacity: 0; }
            100% { transform: scale(1) rotate(0deg); opacity: 1; }
        }

        /* Icon rung khi lỗi */
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            50% { transform: translateX(5px); }
            75% { transform: translateX(-3px); }
        }
    </style>

    <script>
        let countdown = 3;
        const countdownEl = document.getElementById("countdown");
        const popup = document.getElementById("popupMessage");
        const overlay = document.getElementById("popupOverlay");
        const closeBtn = document.getElementById("closeBtn");

        const timer = setInterval(() => {
            countdown--;
            if (countdownEl) countdownEl.textContent = countdown;
            if (countdown === 0) {
                clearInterval(timer);
                popup.style.opacity = "0";
                overlay.style.opacity = "0";
                setTimeout(() => {
                    popup.remove();
                    overlay.remove();
                }, 400);
            }
        }, 1000);

        closeBtn.onclick = () => {
            popup.remove();
            overlay.remove();
            clearInterval(timer);
        };
    </script>


    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</c:if>