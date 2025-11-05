<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Edit Vehicle</title>

    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css"
      rel="stylesheet"
    />

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
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }
      .brand-badge.selected {
        background-color: #0d6efd !important;
        color: white !important;
      }
    </style>
  </head>

  <body class="bg-light">
    <jsp:include page="/common/customer/header.jsp" />

    <div class="container py-5">
      <div class="card shadow-lg">
        <div class="card-body">
          <h3 class="card-title mb-4 text-center">Edit Vehicle</h3>

          <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
          </c:if>

          <form
            action="${pageContext.request.contextPath}/customer/editVehicle"
            method="post"
            id="vehicleForm"
          >
            <input
              type="hidden"
              name="vehicleId"
              value="${vehicle.vehicleID}"
            />

            <!-- Popular Brands Quick Select -->
            <div class="mb-3">
              <label class="form-label fw-bold"
                >Popular Brands (Quick Select)</label
              >
              <div class="popular-brands" id="popularBrands">
                <!-- Will be populated by JS -->
              </div>
            </div>

            <!-- Brand -->
            <div class="mb-3">
              <label for="brand" class="form-label fw-bold">
                Brand <small class="text-muted">(Type to search)</small>
              </label>
              <select
                id="brand"
                name="brandName"
                class="form-select"
                data-current-brand="${vehicle.brand}"
                required
              >
                <option value="">Select or search brand...</option>
              </select>
            </div>

            <!-- Model -->
            <div class="mb-3">
              <label for="model" class="form-label fw-bold">
                Model <small class="text-muted">(Type to search)</small>
              </label>
              <select
                id="model"
                name="modelName"
                class="form-select"
                data-current-model="${vehicle.model}"
                required
              >
                <option value="">Select model...</option>
              </select>

              <div
                id="loadingText"
                class="form-text text-primary"
                style="display: none"
              >
                Loading models...
              </div>
              <div
                id="modelCount"
                class="form-text text-muted"
                style="display: none"
              ></div>
            </div>

            <!-- Year -->
            <div class="mb-3">
              <label for="year" class="form-label fw-bold">Year</label>
              <select
                id="year"
                name="yearManufacture"
                class="form-select"
                data-current-year="${vehicle.yearManufacture}"
                required
              >
                <option value="">Select year</option>
              </select>
            </div>

            <!-- License Plate -->
            <div class="mb-3">
              <label for="licensePlate" class="form-label fw-bold"
                >License Plate</label
              >
              <input
                type="text"
                id="licensePlate"
                name="licensePlate"
                class="form-control"
                placeholder="VD: 36A-36363"
                value="${vehicle.licensePlate}"
                pattern="[0-9]{2}[A-Z]{1,2}[-\\s]?[0-9]{4,5}"
                required
              />
              <div class="form-text">Format: 36A-36363 or 30L1-12345</div>
            </div>

            <div class="d-flex justify-content-between mt-4">
              <a
                href="${pageContext.request.contextPath}/customer/garage"
                class="btn btn-outline-secondary"
              >
                Back to Garage
              </a>
              <button type="submit" class="btn btn-primary px-4">
                <i class="bi bi-save"></i> Save Changes
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Scripts -->
    <script>
      const contextPath = "<%= request.getContextPath() %>";
      const currentBrand = "${vehicle.brand}";
      const currentModel = "${vehicle.model}";
      const currentYear = "${vehicle.yearManufacture}";
    </script>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css"
    />

    <!-- Tái sử dụng JS cũ -->
    <script src="${pageContext.request.contextPath}/assets/js/customer/vehicleApi.js"></script>

    <!-- Tự động set giá trị hiện tại -->
    <script>
      $(document).ready(function () {
        // Populate years
        const currentYear = new Date().getFullYear();
        for (let y = currentYear; y >= 1980; y--) {
          $("#year").append(`<option value="${y}">${y}</option>`);
        }

        // Khi dữ liệu từ API đã load xong, đặt giá trị mặc định
        const observer = new MutationObserver(() => {
          if ($("#brand option").length > 1) {
            $("#brand").val(currentBrand).trigger("change");
          }
          if (currentYear) {
            $("#year").val(currentYear);
          }
        });
        observer.observe(document.getElementById("brand"), { childList: true });
      });
    </script>

    <jsp:include page="/common/customer/footer.jsp" />
  </body>
</html>
