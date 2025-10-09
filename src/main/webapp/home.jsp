<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carspa | Dịch Vụ Chăm Sóc Xe Tốt Nhất</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome for Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!-- Google Fonts: Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;700;900&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary-color: #ffffff;
            --secondary-color: #adb5bd;
            --dark-bg: #111111;
            --button-dark: #212529;
            --button-hover: #343a40;
            --brand-red: #dc3545;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--dark-bg);
            color: var(--primary-color);
        }

        /* --- Header Top Bar --- */
        .header-top {
            background-color: var(--dark-bg);
            border-bottom: 1px solid #2a2a2a;
            font-size: 0.85rem;
            padding: 0.5rem 0;
            color: var(--secondary-color);
        }

        .header-top a {
            color: var(--secondary-color);
            text-decoration: none;
        }
        .header-top a:hover {
            color: var(--primary-color);
        }

        .header-top .info-item i {
            margin-right: 8px;
        }

        .header-top .btn-contact-top {
            background-color: var(--button-dark);
            border: 1px solid #444;
            color: var(--primary-color);
            padding: 0.4rem 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .header-top .btn-contact-top:hover {
            background-color: var(--button-hover);
            border-color: #555;
        }

        /* --- Main Navigation --- */
        .main-navbar {
            background-color: rgba(17, 17, 17, 0.8);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            padding: 1rem 0;
        }

        .navbar-brand {
            font-size: 2rem;
            font-weight: 900;
            color: var(--primary-color) !important;
            letter-spacing: -1px;
        }
        .navbar-brand span {
            font-weight: 400;
            font-size: 0.6em;
            vertical-align: middle;
            margin-left: 2px;
        }

        .nav-icons .nav-icon {
            font-size: 1.2rem;
            color: var(--primary-color);
            margin-left: 1.5rem;
            cursor: pointer;
            transition: color 0.3s ease;
        }
        .nav-icons .nav-icon:hover {
            color: var(--secondary-color);
        }

        /* --- Hero Section --- */
        .hero-section {
            height: 100vh;
            min-height: 700px;
            background-image: url('https://images.unsplash.com/photo-1552537595-b30edb7a6331?q=80&w=2070&auto=format&fit=crop');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            position: relative;
            display: flex;
            align-items: center;
            color: var(--primary-color);
        }

        .hero-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(to top, rgba(0,0,0,0.8), rgba(0,0,0,0.3));
        }

        .hero-content {
            position: relative;
            z-index: 2;
            text-shadow: 2px 2px 10px rgba(0,0,0,0.7);
        }

        .hero-content .sub-heading {
            font-size: 1.5rem;
            font-weight: 300;
        }

        .hero-content h1 {
            font-size: 4rem;
            font-weight: 300;
            margin-top: 0.5rem;
        }

        .hero-content h1 strong {
            display: block;
            font-size: 5.5rem;
            font-weight: 900;
            line-height: 1.1;
        }

        .btn-view-services {
            margin-top: 2rem;
            background: transparent;
            border: 1px solid var(--primary-color);
            color: var(--primary-color);
            padding: 0.8rem 2rem;
            font-weight: 500;
            border-radius: 4px;
            text-transform: uppercase;
            font-size: 0.9rem;
            letter-spacing: 1px;
            transition: all 0.3s ease;
        }

        .btn-view-services:hover {
            background-color: var(--primary-color);
            color: var(--dark-bg);
        }

        /* --- Floating Elements --- */
        .side-nav-dots {
            position: fixed;
            top: 50%;
            right: 30px;
            transform: translateY(-50%);
            z-index: 1000;
        }
        .side-nav-dots .dot {
            display: block;
            width: 10px;
            height: 10px;
            margin: 15px 0;
            background-color: rgba(255, 255, 255, 0.5);
            border-radius: 50%;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .side-nav-dots .dot.active, .side-nav-dots .dot:hover {
            background-color: var(--primary-color);
        }

        .floating-contact-btn {
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 1000;
            width: 55px;
            height: 55px;
            background-color: var(--brand-red);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            box-shadow: 0 4px 15px rgba(220, 53, 69, 0.4);
            cursor: pointer;
            transition: transform 0.3s ease;
        }
        .floating-contact-btn:hover {
            transform: scale(1.1);
        }

        /* Responsive */
        @media (max-width: 992px) {
            .header-top .info-item {
                display: none;
            }
            .header-top .container {
                justify-content: flex-end !important;
            }
            .hero-content h1 {
                font-size: 3rem;
            }
            .hero-content h1 strong {
                font-size: 4rem;
            }
        }

        @media (max-width: 768px) {
            .hero-content .sub-heading {
                font-size: 1.2rem;
            }
            .hero-content h1 {
                font-size: 2.5rem;
            }
            .hero-content h1 strong {
                font-size: 3rem;
            }
            .side-nav-dots {
                display: none;
            }
        }
    </style>
</head>
<body>

<!-- SECTION: Header -->
<header class="fixed-top">
    <!-- Top contact bar -->
    <div class="header-top d-none d-lg-block">
        <div class="container d-flex justify-content-between align-items-center">
            <div class="d-flex gap-4">
                <span class="info-item"><i class="fas fa-map-marker-alt"></i> 757 Huỳnh Tấn Phát, Phú Thuận, Q7</span>
                <span class="info-item"><i class="fas fa-clock"></i> Thứ 2 - 7 / 08.00AM - 17.00PM</span>
                <a href="tel:0909579579" class="info-item"><i class="fas fa-phone-alt"></i> Hotline: 0909 579 579 (Mr. Hưng)</a>
            </div>
            <a href="#" class="btn btn-contact-top">LIÊN HỆ ĐẶT LỊCH</a>
        </div>
    </div>
    <!-- Main navigation bar -->
    <nav class="main-navbar">
        <div class="container d-flex justify-content-between align-items-center">
            <a class="navbar-brand" href="#">
                CS<span>CARSPA.VN</span>
            </a>
            <div class="nav-icons d-flex align-items-center">
                <i class="fas fa-search nav-icon"></i>
                <i class="fas fa-bars nav-icon"></i>
            </div>
        </div>
    </nav>
</header>

<!-- SECTION: Hero -->
<main class="hero-section">
    <div class="container">
        <div class="row">
            <div class="col-lg-8">
                <div class="hero-content">
                    <p class="sub-heading">Carspa. Khắc phục mọi vấn đề cho xe</p>
                    <h1>Trung tâm sửa chữa ô tô <strong>chuyên nghiệp.</strong></h1>
                    <a href="#" class="btn btn-view-services">
                        XEM TẤT CẢ DỊCH VỤ <i class="fas fa-arrow-right ms-2"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Floating Action Button -->
<div class="floating-contact-btn">
    <i class="fas fa-comment-dots"></i>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
