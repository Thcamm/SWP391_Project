<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Footer Carspa</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">

    <style>
        /* --- CÀI ĐẶT CHUNG --- */
        body {
            margin: 0;
            font-family: 'Roboto', sans-serif;
            background-color: #f0f0f0; /* Màu nền xám nhạt cho trang web */
        }

        /* --- THANH LIÊN HỆ TRÊN CÙNG --- */
        .contact-bar {
            background-color: #222222;
            color: white;
            padding: 30px 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap; /* Cho responsive */
            gap: 20px;
        }

        .contact-bar .text-content h2 {
            margin: 0 0 5px 0;
            font-size: 2rem;
            font-weight: 700;
        }

        .contact-bar .text-content p {
            margin: 0;
            color: #aaaaaa;
            font-size: 0.9rem;
        }

        .contact-bar .contact-button {
            background-color: #444444;
            color: white;
            padding: 12px 25px;
            text-decoration: none;
            font-weight: 500;
            transition: background-color 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .contact-bar .contact-button:hover {
            background-color: #555555;
        }

        /* --- FOOTER CHÍNH --- */
        .main-footer {
            background-color: #3e3e3e;
            /* Hiệu ứng vân chéo giống trong ảnh */
            background-image: repeating-linear-gradient(45deg, transparent, transparent 10px, rgba(0,0,0,0.1) 10px, rgba(0,0,0,0.1) 20px);
            color: white;
            padding: 60px 20px;
            text-align: center;
        }

        .footer-logo img {
            max-width: 180px; /* Chỉnh kích thước logo của bạn ở đây */
            margin-bottom: 20px;
        }

        .footer-commitment {
            max-width: 600px;
            margin: 0 auto 50px auto;
            color: #cccccc;
            font-size: 0.95rem;
            line-height: 1.6;
        }

        .footer-info-grid {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            flex-wrap: wrap; /* Cho responsive */
            gap: 30px;
            max-width: 1200px;
            margin: 0 auto;
        }

        .info-col {
            flex: 1;
            min-width: 220px; /* Độ rộng tối thiểu cho mỗi cột */
        }

        .info-col .icon-wrapper {
            width: 70px;
            height: 70px;
            border: 1px solid #777777;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 0 auto 20px auto;
            font-size: 1.8rem;
            color: #cccccc;
        }

        .info-col h4 {
            margin: 0 0 5px 0;
            font-size: 1.1rem;
            font-weight: 500;
        }

        .info-col p {
            margin: 0;
            font-size: 0.9rem;
            color: #cccccc;
        }

        /* --- PHẦN BẢN QUYỀN --- */
        .footer-bottom {
            background-color: #3e3e3e;
            padding: 20px;
            text-align: center;
            font-size: 0.85rem;
            color: #aaaaaa;
            border-top: 1px solid #555555;
        }
    </style>
</head>
<body>

<section class="contact-bar">
    <div class="text-content">
        <h2>carspavn@gmail.com</h2>
        <p>Hãy để đội ngũ chuyên nghiệp của chúng tôi giúp bạn giải quyết mọi vấn đề về xe. Liên hệ với chúng tôi ngay để nhận được dịch vụ tốt nhất.</p>
    </div>

</section>

<footer class="main-footer">
    <div class="footer-logo">
    </div>
    <p class="footer-commitment">
        Carspa cam kết đem đến sự hài lòng và tin tưởng cho khách hàng với dịch vụ chăm sóc và làm đẹp xe chất lượng hàng đầu.
    </p>
    <div class="footer-info-grid">
        <div class="info-col">
            <div class="icon-wrapper">
                <i class="fa-solid fa-mobile-screen-button"></i>
            </div>
            <h4>(+84) 909-579-579</h4>
            <p>Round-the-clock</p>
        </div>
        <div class="info-col">
            <div class="icon-wrapper">
                <i class="fa-solid fa-location-dot"></i>
            </div>
            <h4>757 Huỳnh Tấn Phát</h4>
            <p>Phú Thuận, Q7</p>
        </div>
        <div class="info-col">
            <div class="icon-wrapper">
                <i class="fa-solid fa-comments"></i>
            </div>
            <h4>CarSpaVN@gmail.com</h4>
            <p>Address Email</p>
        </div>
        <div class="info-col">
            <div class="icon-wrapper">
                <i class="fa-solid fa-plane"></i>
            </div>
            <h4>T2-T7: 8:00-17:00</h4>
            <p>Chủ Nhật nghỉ</p>
        </div>
    </div>
</footer>

<div class="footer-bottom">
    CARSPA CENTER VIET NAM © 2023 CREATED BY BUI TUAN DUONG
</div>

</body>
</html>