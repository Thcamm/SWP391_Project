<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Vehicle</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Select2 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css" rel="stylesheet" />

    <style>
        .select2-container--bootstrap-5 .select2-selection {
            min-height: 38px;
        }
        .popular-brands {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 15px;
        }
        .brand-badge {
            cursor: pointer;
            transition: all 0.2s;
        }
        .brand-badge:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        }
        .brand-badge.selected {
            background-color: #0d6efd !important;
            color: white !important;
        }
        .search-hint {
            font-size: 0.85rem;
            color: #6c757d;
            margin-top: 5px;
        }
    </style>
</head>
<body class="bg-light">

<div class="container py-5">
    <div class="card shadow-lg">
        <div class="card-body">
            <h3 class="card-title mb-4 text-center">Add New Vehicle</h3>

            <form action="${pageContext.request.contextPath}/customer/addVehicle" method="post" id="vehicleForm">

                <!-- Popular Brands Quick Select -->
                <div class="mb-3">
                    <label class="form-label fw-bold">Popular Brands (Quick Select)</label>
                    <div class="popular-brands" id="popularBrands">
                        <!-- Will be populated by JS -->
                    </div>
                </div>

                <!-- Brand (Searchable) -->
                <div class="mb-3">
                    <label for="brand" class="form-label fw-bold">
                        Brand
                        <small class="text-muted">(Type to search)</small>
                    </label>
                    <select id="brand" name="brand" class="form-select" required>
                        <option value="">Select or search brand...</option>
                    </select>
                    <div class="search-hint">
                        <i class="bi bi-search"></i> Start typing to quickly find your brand
                    </div>
                </div>

                <!-- Model (Searchable) -->
                <div class="mb-3">
                    <label for="model" class="form-label fw-bold">
                        Model
                        <small class="text-muted">(Type to search)</small>
                    </label>
                    <select id="model" name="model" class="form-select" required disabled>
                        <option value="">Select brand first</option>
                    </select>
                    <div id="loadingText" class="form-text text-primary" style="display:none;">
                        Loading models...
                    </div>
                    <div id="modelCount" class="form-text text-muted" style="display:none;">
                        <!-- Will show: "Found X models" -->
                    </div>
                </div>

                <!-- Year -->
                <div class="mb-3">
                    <label for="year" class="form-label fw-bold">Year</label>
                    <select id="year" name="year" class="form-select" required>
                        <option value="">Select year</option>
                    </select>
                </div>

                <!-- License Plate -->
                <div class="mb-3">
                    <label for="licensePlate" class="form-label fw-bold">License Plate</label>
                    <input type="text" id="licensePlate" name="licensePlate"
                           class="form-control" placeholder="VD: 36A-36363"
                           pattern="[0-9]{2}[A-Z]{1,2}[-\s]?[0-9]{4,5}" required>
                    <div class="form-text">Format: 36A-36363 or 30L1-12345</div>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-lg">
                    <i class="bi bi-save"></i> Save Vehicle
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    const contextPath = '<%= request.getContextPath() %>';
</script>

<!-- jQuery (required for Select2) -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Select2 JS -->
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<!-- Bootstrap Icons (optional) -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

<script src="${pageContext.request.contextPath}/assets/js/customer/vehicleApi.js"></script>

</body>
</html>