<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Repair History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        #vehicleSuggestions {
            background: white;
            border: 1px solid #dee2e6;
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
            border-radius: 0.375rem;
            max-height: 300px;
            overflow-y: auto;
        }

        #vehicleSuggestions .list-group-item {
            border: none;
            border-bottom: 1px solid #f0f0f0;
            cursor: pointer;
            transition: background-color 0.15s ease-in-out;
        }

        #vehicleSuggestions .list-group-item:hover {
            background-color: #f8f9fa;
        }

        #vehicleSuggestions .list-group-item:last-child {
            border-bottom: none;
        }
    </style>
</head>

<body class="bg-light">
<jsp:include page="/common/header.jsp" />

<div class="container py-5">
    <h2 class="mb-4 text-center" style="color: #5a6268">Your Repair History</h2>

    <!-- Filter Section -->
    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title mb-3">
                <i class="bi bi-funnel"></i> Filter & Sort
            </h5>

            <form method="GET" action="${pageContext.request.contextPath}/customer/repair-list" id="filterForm">
                <div class="row g-3">
                    <!-- Vehicle Filter -->
                    <div class="col-md-5">
                        <label class="form-label" for="vehicleSearch">Vehicle License Plate</label>
                        <div class="position-relative">
                            <input type="text"
                                   class="form-control"
                                   id="vehicleSearch"
                                   placeholder="Type to search license plate..."
                                   autocomplete="off">
                            <input type="hidden"
                                   name="vehicleId"
                                   id="vehicleIdInput"
                                   value="${selectedVehicleId != null ? selectedVehicleId : ''}">

                            <!-- Dropdown suggestions -->
                            <div id="vehicleSuggestions"
                                 class="position-absolute w-100 mt-1"
                                 style="display: none; z-index: 1050;">
                            </div>
                        </div>

                        <!-- Selected vehicle info - SINGLE ELEMENT -->
                        <small class="d-block mt-1" id="selectedVehicleInfo">
                            <c:choose>
                                <c:when test="${selectedVehicleId != null}">
                                    <span class="text-success">
                                        <i class="bi bi-check-circle"></i> Filtering by Vehicle ID: ${selectedVehicleId}
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Start typing to search...</span>
                                </c:otherwise>
                            </c:choose>
                        </small>
                    </div>

                    <!-- Sort Order -->
                    <div class="col-md-4">
                        <label class="form-label" for="sortBySelect">Sort By</label>
                        <select class="form-select" name="sortBy" id="sortBySelect">
                            <option value="newest" ${empty selectedSortBy || selectedSortBy == 'newest' ? 'selected' : ''}>Newest First</option>
                            <option value="oldest" ${selectedSortBy == 'oldest' ? 'selected' : ''}>Oldest First</option>
                        </select>
                    </div>

                    <!-- Action Buttons -->
                    <div class="col-md-3 d-flex align-items-end gap-2">
                        <button type="submit" class="btn btn-primary flex-fill">
                            <i class="bi bi-search"></i> Search
                        </button>
                        <button type="button" class="btn btn-outline-secondary" id="clearFilter" title="Clear filters">
                            <i class="bi bi-x-circle"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Active filters badge -->
    <c:choose>
        <c:when test="${not empty journeyList.paginatedData}">
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle">
                    <thead class="table-dark text-center">
                    <tr>
                        <th>No.</th>
                        <th>Vehicle</th>
                        <th>Start Date</th>
                        <th>Type</th>
                        <th>Stage</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="journey" items="${journeyList.paginatedData}" varStatus="status">
                        <tr>
                            <td class="text-center">
                                    ${status.index + 1 + (journeyList.currentPage - 1) * journeyList.itemsPerPage}
                            </td>
                            <td class="text-center">
                                    ${journey.vehicleLicensePlate}
                            </td>
                            <td class="text-center">
                                <fmt:formatDate value="${journey.entryDate}" pattern="dd/MM/yyyy" />
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${journey.entryType == 'Appointment'}">
                                        <span class="badge bg-primary">Appointment</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Walk-in</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <span class="badge bg-dark">${journey.latestStage}-${journey.latestStatus}</span>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/customer/repair-tracker?id=${journey.requestID}
                     <c:if test='${not empty vehicleId}'> &vehicleId=${vehicleId}</c:if>
                     <c:if test='${not empty sortBy}'> &sortBy=${sortBy}</c:if>
                     <c:if test='${not empty page}'> &page=${page}</c:if>"
                                   class="btn btn-primary btn-sm">
                                    Tracking
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>

        <c:otherwise>
            <div class="alert alert-info text-center">
                You have no repair history yet.
            </div>
        </c:otherwise>
    </c:choose>

    <jsp:include page="/view/customerservice/pagination.jsp">
        <jsp:param name="currentPage" value="${journeyList.currentPage}" />
        <jsp:param name="totalPages" value="${journeyList.totalPages}" />
        <jsp:param name="baseUrl" value="/customer/repair-list" />
        <jsp:param name="queryString" value="?vehicleId=${param.vehicleId}&sortBy=${param.sortBy}" />
    </jsp:include>
</div>

<jsp:include page="/common/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
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
                    console.log('Response status:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.json();
                })
                .then(vehicles => {
                    console.log('Vehicles received:', vehicles);
                    displaySuggestions(vehicles);
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    suggestionsDiv.innerHTML = '<div class="list-group-item text-danger"><i class="bi bi-exclamation-triangle"></i> Error: ' + error.message + '</div>';
                    suggestionsDiv.style.display = 'block';
                });
        }

        // Display suggestions
        function displaySuggestions(vehicles) {
            suggestionsDiv.innerHTML = '';

            if (!Array.isArray(vehicles)) {
                console.error('Invalid vehicles data:', vehicles);
                suggestionsDiv.innerHTML = '<div class="list-group-item text-danger">Invalid data received</div>';
                suggestionsDiv.style.display = 'block';
                return;
            }

            if (vehicles.length === 0) {
                suggestionsDiv.innerHTML = '<div class="list-group-item text-muted"><i class="bi bi-search"></i> No vehicles found</div>';
                suggestionsDiv.style.display = 'block';
                return;
            }

            // Create list group
            const listGroup = document.createElement('div');
            listGroup.className = 'list-group';

            vehicles.forEach(function(vehicle) {
                const item = document.createElement('a');
                item.href = '#';
                item.className = 'list-group-item list-group-item-action';
                item.dataset.id = vehicle.vehicleID || '';
                item.dataset.plate = vehicle.licensePlate || '';

                // Create item content
                const contentDiv = document.createElement('div');
                contentDiv.className = 'd-flex justify-content-between align-items-center';

                const infoDiv = document.createElement('div');
                const plateStrong = document.createElement('strong');
                plateStrong.className = 'text-primary';
                plateStrong.textContent = vehicle.licensePlate || 'N/A';
                infoDiv.appendChild(plateStrong);

                const detailsSmall = document.createElement('small');
                detailsSmall.className = 'text-muted d-block';
                detailsSmall.textContent = (vehicle.brand || '') + ' ' + (vehicle.model || '') +
                    (vehicle.yearManufacture ? ' (' + vehicle.yearManufacture + ')' : '');
                infoDiv.appendChild(detailsSmall);

                contentDiv.appendChild(infoDiv);

                const chevron = document.createElement('i');
                chevron.className = 'bi bi-chevron-right text-muted';
                contentDiv.appendChild(chevron);

                item.appendChild(contentDiv);

                // Add click handler
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
            selectedVehicleInfo.innerHTML = '<span class="text-success"><i class="bi bi-check-circle"></i> Selected: ' + plate + '</span>';
            hideSuggestions();
        }

        // Hide suggestions
        function hideSuggestions() {
            suggestionsDiv.style.display = 'none';
            suggestionsDiv.innerHTML = '';
        }

        // Hide suggestions when clicking outside
        document.addEventListener('click', function(e) {
            if (!vehicleSearchInput.contains(e.target) && !suggestionsDiv.contains(e.target)) {
                hideSuggestions();
            }
        });

        // Clear filter button
        if (clearFilterBtn) {
            clearFilterBtn.addEventListener('click', function() {
                window.location.href = '${pageContext.request.contextPath}/customer/repair-list';
            });
        }

        // Handle keyboard navigation
        vehicleSearchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                if (suggestionsDiv.style.display === 'block') {
                    const firstItem = suggestionsDiv.querySelector('.list-group-item');
                    if (firstItem) {
                        firstItem.click();
                    }
                }
            } else if (e.key === 'Escape') {
                hideSuggestions();
            }
        });
    })();
</script>
</body>
</html>