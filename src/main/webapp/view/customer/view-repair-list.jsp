<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Repair History</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <style>
        :root {
            --primary-color: #4361ee;
            --primary-hover: #3a56d4;
            --bg-light: #f8f9fa;
            --card-border: #e9ecef;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: #f3f4f6;
            color: #343a40;
        }

        /* Card Styling */
        .custom-card {
            background: white;
            border: 1px solid var(--card-border);
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
            margin-bottom: 1.5rem;
            transition: transform 0.2s ease;
        }

        .card-header-custom {
            background-color: white;
            border-bottom: 1px solid var(--card-border);
            padding: 1.25rem 1.5rem;
            border-radius: 12px 12px 0 0;
            font-weight: 600;
            color: #1a1a1a;
        }

        /* Form Controls */
        .form-label {
            font-size: 0.85rem;
            font-weight: 600;
            text-transform: uppercase;
            color: #6c757d;
            margin-bottom: 0.5rem;
            letter-spacing: 0.5px;
        }

        .form-control, .form-select {
            border-radius: 8px;
            border: 1px solid #dee2e6;
            padding: 0.6rem 1rem;
            font-size: 0.95rem;
        }

        .form-control:focus, .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.15);
        }

        /* Auto-complete Suggestions */
        #vehicleSuggestions {
            background: white;
            border: 1px solid #e9ecef;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            border-radius: 8px;
            max-height: 300px;
            overflow-y: auto;
            margin-top: 5px;
        }

        #vehicleSuggestions .list-group-item {
            border: none;
            border-bottom: 1px solid #f1f3f5;
            padding: 12px 15px;
        }

        #vehicleSuggestions .list-group-item:hover {
            background-color: #f8f9fa;
        }

        /* Table Styling */
        .table-custom th {
            background-color: #f8f9fa;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 0.5px;
            color: #6c757d;
            padding: 1rem;
            border-bottom: 2px solid #e9ecef;
        }

        .table-custom td {
            padding: 1rem;
            vertical-align: middle;
            border-bottom: 1px solid #e9ecef;
            font-size: 0.95rem;
        }

        .table-hover tbody tr:hover {
            background-color: #fbfbfc;
        }

        /* Badges (Soft UI) */
        .badge-soft-primary { background-color: rgba(67, 97, 238, 0.1); color: var(--primary-color); }
        .badge-soft-success { background-color: rgba(25, 135, 84, 0.1); color: #198754; }
        .badge-soft-warning { background-color: rgba(255, 193, 7, 0.15); color: #997404; }
        .badge-soft-info    { background-color: rgba(13, 202, 240, 0.1); color: #0dcaf0; }
        .badge-soft-secondary { background-color: rgba(108, 117, 125, 0.1); color: #6c757d; }

        .badge {
            padding: 0.5em 0.8em;
            font-weight: 600;
            border-radius: 6px;
        }

        /* Buttons */
        .btn-primary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
            border-radius: 8px;
            font-weight: 500;
            padding: 0.6rem 1.2rem;
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
            border-color: var(--primary-hover);
        }

        .btn-outline-secondary {
            border-radius: 8px;
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 3rem 1rem;
        }
        .empty-state-icon {
            font-size: 3rem;
            color: #dee2e6;
            margin-bottom: 1rem;
        }
    </style>
</head>

<body>
<jsp:include page="/common/header.jsp" />

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-dark mb-1">Repair History</h2>
            <p class="text-muted mb-0">Track and manage your vehicle service records</p>
        </div>
    </div>

    <div class="custom-card">
        <div class="card-header-custom">
            <i class="bi bi-sliders me-2 text-primary"></i> Filter & Sort
        </div>
        <div class="card-body p-4">
            <form method="GET" action="${pageContext.request.contextPath}/customer/repair-list" id="filterForm">
                <div class="row g-3 align-items-end">
                    <div class="col-lg-5 col-md-6">
                        <div class="mt-0" id="selectedVehicleInfo" >
                            <c:choose>
                                <c:when test="${selectedVehicleId != null}">
<%--                                    <span class="badge badge-soft-success">--%>
<%--&lt;%&ndash;                                        <i class="bi bi-check-circle me-1"></i> Active Filter: ${selectedVehicleId}&ndash;%&gt;--%>
<%--                                    </span>--%>
                                </c:when>
                            </c:choose>
                        </div>
                        <label class="form-label" for="vehicleSearch">Vehicle Search</label>
                        <div class="position-relative">

                            <div class="input-group">

                                <span class="input-group-text bg-white border-end-0 text-muted">
                                    <i class="bi bi-car-front"></i>
                                </span>
                                <input type="text"
                                       class="form-control border-start-0 ps-0"
                                       id="vehicleSearch"
                                       placeholder="Search by license plate..."
                                       autocomplete="off">
                            </div>
                            <input type="hidden"
                                   name="vehicleId"
                                   id="vehicleIdInput"
                                   value="${selectedVehicleId != null ? selectedVehicleId : ''}">

                            <div id="vehicleSuggestions"
                                 class="position-absolute w-100 mt-1"
                                 style="display: none; z-index: 1050;">
                            </div>
                        </div>


                    </div>

                    <div class="col-lg-4 col-md-4">
                        <label class="form-label" for="sortBySelect">Sort Order</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white border-end-0 text-muted">
                                <i class="bi bi-sort-down"></i>
                            </span>
                            <select class="form-select border-start-0 ps-0" name="sortBy" id="sortBySelect">
                                <option value="newest" ${empty selectedSortBy || selectedSortBy == 'newest' ? 'selected' : ''}>Newest First</option>
                                <option value="oldest" ${selectedSortBy == 'oldest' ? 'selected' : ''}>Oldest First</option>
                            </select>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-2 d-flex gap-2 mb-3 mb-md-0">
                        <button type="submit" class="btn btn-primary w-100 shadow-sm">
                            Search
                        </button>
                        <button type="button" class="btn btn-outline-secondary px-3" id="clearFilter" data-bs-toggle="tooltip" title="Reset Filters">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="custom-card overflow-hidden">
        <c:choose>
            <c:when test="${not empty journeyList.paginatedData}">
                <div class="table-responsive">
                    <table class="table table-hover table-custom mb-0">
                        <thead>
                        <tr>
                            <th class="text-center" style="width: 5%">#</th>
                            <th style="width: 20%">Vehicle Info</th>
                            <th style="width: 15%">Start Date</th>
                            <th class="text-center" style="width: 15%">Type</th>
                            <th class="text-center" style="width: 25%">Status</th>
                            <th class="text-center" style="width: 10%">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="journey" items="${journeyList.paginatedData}" varStatus="status">
                            <tr>
                                <td class="text-center text-muted fw-bold">
                                        ${status.index + 1 + (journeyList.currentPage - 1) * journeyList.itemsPerPage}
                                </td>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="rounded-circle bg-light p-2 me-3 text-primary">
                                            <i class="bi bi-car-front-fill"></i>
                                        </div>
                                        <div>
                                            <div class="fw-bold text-dark">${journey.vehicleLicensePlate}</div>
                                        </div>
                                    </div>
                                </td>
                                <td class="text-secondary">
                                    <i class="bi bi-calendar3 me-1"></i>
                                    <fmt:formatDate value="${journey.entryDate}" pattern="MMM dd, yyyy" />
                                </td>
                                <td class="text-center" style="color: #0b0f14">
                                    <c:choose>
                                        <c:when test="${journey.entryType == 'Appointment'}">
                                            <span class="badge badge-soft-primary" style="color: #0b0f14">
                                                <i class="bi bi-calendar-check me-1" style="color: #0b0f14"></i> Appointment
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-soft-secondary" style="color: #0b0f14">
                                                <i class="bi bi-person-walking me-1"></i> Walk-in
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center" ">
                                    <span class="badge badge-soft-info border border-info border-opacity-25" style="color: #0b0f14">
                                        ${journey.latestStage} &bull; ${journey.latestStatus}
                                    </span>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/customer/repair-tracker?id=${journey.requestID}
                                         <c:if test='${not empty vehicleId}'> &vehicleId=${vehicleId}</c:if>
                                         <c:if test='${not empty sortBy}'> &sortBy=${sortBy}</c:if>
                                         <c:if test='${not empty page}'> &page=${page}</c:if>"
                                       class="btn btn-sm btn-outline-primary">
                                        Track <i class="bi bi-arrow-right ms-1"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>

            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">
                        <i class="bi bi-clipboard-x"></i>
                    </div>
                    <h5 class="text-muted">No Repair History Found</h5>
                    <p class="text-muted small">Try adjusting your search filters or create a new service request.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="/view/customerservice/pagination.jsp">
        <jsp:param name="currentPage" value="${journeyList.currentPage}" />
        <jsp:param name="totalPages" value="${journeyList.totalPages}" />
        <jsp:param name="baseUrl" value="/customer/repair-list" />
        <jsp:param name="queryString" value="vehicleId=${param.vehicleId}&sortBy=${param.sortBy}" />
    </jsp:include>
</div>

<jsp:include page="/common/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    })
</script>

<script>
    (function() {
        'use strict';

        let debounceTimer;
        let selectedVehicle = null;

        // Get DOM elements
        const vehicleSearchInput = document.getElementById('vehicleSearch');
        const vehicleIdInput = document.getElementById('vehicleIdInput');
        const suggestionsDiv = document.getElementById('vehicleSuggestions');
        const selectedVehicleInfo = document.getElementById('selectedVehicleInfo');
        const clearFilterBtn = document.getElementById('clearFilter');

        // Check if elements exist
        if (!vehicleSearchInput || !vehicleIdInput || !suggestionsDiv || !selectedVehicleInfo) {
            console.error('Required DOM elements not found');
            return;
        }

        // Load selected vehicle on page load
        <c:if test="${selectedVehicleId != null}">
        selectedVehicle = { id: '${selectedVehicleId}', plate: '' };
        </c:if>

        // Show suggestions on focus
        vehicleSearchInput.addEventListener('focus', function() {
            const query = this.value.trim();
            searchVehicles(query);
        });

        // Search on input
        vehicleSearchInput.addEventListener('input', function() {
            const query = this.value.trim();
            clearTimeout(debounceTimer);

            if (query.length < 2) {
                hideSuggestions();
                return;
            }

            debounceTimer = setTimeout(() => {
                searchVehicles(query);
            }, 300);
        });

        // Function to search vehicles
        function searchVehicles(query) {
            const url = '${pageContext.request.contextPath}/customer/vehicles-search?query=' + encodeURIComponent(query);
            console.log('Fetching vehicles from:', url);

            fetch(url)
                .then(response => {
                    if (!response.ok) throw new Error('HTTP ' + response.status);
                    return response.json();
                })
                .then(vehicles => {
                    displaySuggestions(vehicles);
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    suggestionsDiv.innerHTML = '<div class="list-group-item text-danger small"><i class="bi bi-exclamation-triangle"></i> Error loading data</div>';
                    suggestionsDiv.style.display = 'block';
                });
        }

        // Display suggestions
        function displaySuggestions(vehicles) {
            suggestionsDiv.innerHTML = '';

            if (!Array.isArray(vehicles) || vehicles.length === 0) {
                suggestionsDiv.innerHTML = '<div class="list-group-item text-muted small"><i class="bi bi-search"></i> No vehicles found</div>';
                suggestionsDiv.style.display = 'block';
                return;
            }

            const listGroup = document.createElement('div');
            listGroup.className = 'list-group list-group-flush';

            vehicles.forEach(function(vehicle) {
                const item = document.createElement('a');
                item.href = '#';
                item.className = 'list-group-item list-group-item-action py-2';
                item.dataset.id = vehicle.vehicleID || '';
                item.dataset.plate = vehicle.licensePlate || '';

                const contentDiv = document.createElement('div');
                contentDiv.className = 'd-flex justify-content-between align-items-center';

                const infoDiv = document.createElement('div');
                const plateStrong = document.createElement('div');
                plateStrong.className = 'fw-bold text-primary small';
                plateStrong.textContent = vehicle.licensePlate || 'N/A';
                infoDiv.appendChild(plateStrong);

                const detailsSmall = document.createElement('div');
                detailsSmall.className = 'text-muted small';
                detailsSmall.style.fontSize = '0.75rem';
                detailsSmall.textContent = (vehicle.brand || '') + ' ' + (vehicle.model || '') +
                    (vehicle.yearManufacture ? ' (' + vehicle.yearManufacture + ')' : '');
                infoDiv.appendChild(detailsSmall);

                contentDiv.appendChild(infoDiv);

                // Subtle chevron
                const chevron = document.createElement('i');
                chevron.className = 'bi bi-chevron-right text-light-gray';
                contentDiv.appendChild(chevron);

                item.appendChild(contentDiv);

                item.addEventListener('click', function(e) {
                    e.preventDefault();
                    selectVehicle(this.dataset.id, this.dataset.plate);
                });

                listGroup.appendChild(item);
            });

            suggestionsDiv.appendChild(listGroup);
            suggestionsDiv.style.display = 'block';
        }

        // Select vehicle
        function selectVehicle(id, plate) {
            selectedVehicle = { id: id, plate: plate };
            vehicleIdInput.value = id;
            vehicleSearchInput.value = plate;
            // Update UI feedback
            selectedVehicleInfo.innerHTML = '';
            hideSuggestions();
        }

        // Hide suggestions
        function hideSuggestions() {
            suggestionsDiv.style.display = 'none';
            suggestionsDiv.innerHTML = '';
        }

        document.addEventListener('click', function(e) {
            if (!vehicleSearchInput.contains(e.target) && !suggestionsDiv.contains(e.target)) {
                hideSuggestions();
                vehicleSearchInput.blur(); // thêm dòng này để mất focus
            }
        });


        // Clear filter
        if (clearFilterBtn) {
            clearFilterBtn.addEventListener('click', function() {
                window.location.href = '${pageContext.request.contextPath}/customer/repair-list';
            });
        }

        // Keyboard Nav
        vehicleSearchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                if (suggestionsDiv.style.display === 'block') {
                    const firstItem = suggestionsDiv.querySelector('.list-group-item');
                    if (firstItem) firstItem.click();
                }
            } else if (e.key === 'Escape') {
                hideSuggestions();
            }
        });
    })();
</script>
</body>
</html>