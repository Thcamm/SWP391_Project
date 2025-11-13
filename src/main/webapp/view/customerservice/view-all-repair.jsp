<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Repair History</title>
    <style>
        .autocomplete-dropdown {
            position: absolute;
            background: white;
            border: 1px solid #ddd;
            border-radius: 4px;
            max-height: 300px;
            overflow-y: auto;
            z-index: 1050;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .autocomplete-item {
            padding: 10px;
            cursor: pointer;
            border-bottom: 1px solid #f0f0f0;
        }
        .autocomplete-item:hover {
            background-color: #f8f9fa;
        }
        .autocomplete-item:last-child {
            border-bottom: none;
        }
        .customer-name {
            font-weight: 600;
            color: #333;
        }
        .customer-details {
            font-size: 0.85em;
            color: #666;
            margin-top: 2px;
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);">
                    <div class="container py-5">
                        <h2 class="mb-4 text-center">Lịch Sử Sửa Chữa</h2>

                        <div class="card mb-4">
                            <div class="card-body">
                                <h5 class="card-title mb-3">
                                    <i class="bi bi-funnel"></i> Filter & Sort
                                </h5>

                                <form method="GET" action="${pageContext.request.contextPath}/customerservice/view-all-repairs" id="filterForm">
                                    <div class="row g-3">
                                        <!-- Customer Name Filter -->
                                        <div class="col-md-4">
                                            <label class="form-label" for="customerName">Customer Name</label>
                                            <div class="position-relative">
                                                <input type="text"
                                                       class="form-control"
                                                       id="customerName"
                                                       placeholder="Type to search customer..."
                                                       autocomplete="off"
                                                       value="${param.fullName != null ? param.fullName : ''}">
                                                <input type="hidden"
                                                       name="fullName"
                                                       id="customerNameHidden"
                                                       value="${param.fullName != null ? param.fullName : ''}">
                                                <input type="hidden"
                                                       id="customerIdHidden"
                                                       value="">

                                                <div id="customerSuggestions" class="autocomplete-dropdown w-100 mt-1" style="display: none;"></div>
                                            </div>
                                            <small class="d-block mt-1" id="selectedCustomerInfo">
                                                <c:choose>
                                                    <c:when test="${not empty param.fullName}">
                                                        <span class="text-success">
                                                            <i class="bi bi-check-circle"></i> Selected: ${param.fullName}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Start typing to search...</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </small>
                                        </div>

                                        <!-- Vehicle Filter -->
                                        <div class="col-md-4">
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
                                                       value="${param.vehicleId != null ? param.vehicleId : ''}">

                                                <div id="vehicleSuggestions" class="autocomplete-dropdown w-100 mt-1" style="display: none;"></div>
                                            </div>

                                            <small class="d-block mt-1" id="selectedVehicleInfo">
                                                <c:choose>
                                                    <c:when test="${not empty param.vehicleId}">
                                                        <span class="text-success">
                                                            <i class="bi bi-check-circle"></i> Filtering by Vehicle ID: ${param.vehicleId}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Start typing to search...</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </small>
                                        </div>

                                        <!-- Sort Order -->
                                        <div class="col-md-2">
                                            <label class="form-label" for="sortBySelect">Sort By</label>
                                            <select class="form-select" name="sortBy" id="sortBySelect">
                                                <option value="newest" ${empty param.sortBy || param.sortBy == 'newest' ? 'selected' : ''}>Newest First</option>
                                                <option value="oldest" ${param.sortBy == 'oldest' ? 'selected' : ''}>Oldest First</option>
                                            </select>
                                        </div>

                                        <!-- Action Buttons -->
                                        <div class="col-md-2 d-flex align-items-end gap-2">
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

                        <c:choose>
                            <c:when test="${not empty journeyList.paginatedData}">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover align-middle">
                                        <thead class="table-dark text-center">
                                        <tr>
                                            <th>No.</th>
                                            <th>Customer Name</th>
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
                                                <td class="text-center">${journey.fullName}</td>
                                                <td class="text-center">${journey.vehicleLicensePlate}</td>
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
                                                    <a href="${pageContext.request.contextPath}/customerservice/repair-detail?id=${journey.requestID}"
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
                                    No repair history found.
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <jsp:include page="/view/customerservice/pagination.jsp">
                            <jsp:param name="currentPage" value="${journeyList.currentPage}" />
                            <jsp:param name="totalPages" value="${journeyList.totalPages}" />
                            <jsp:param name="baseUrl" value="/customerservice/view-all-repairs" />
                        </jsp:include>

                    </div>
                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<script>
    // Debounce utility
    function debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
    }

    // Customer search with autocomplete
    const customerNameInput = document.getElementById('customerName');
    const customerSuggestionsDiv = document.getElementById('customerSuggestions');
    const customerIdHidden = document.getElementById('customerIdHidden');
    const customerNameHidden = document.getElementById('customerNameHidden');
    const selectedCustomerInfo = document.getElementById('selectedCustomerInfo');

    const searchCustomers = debounce(async function() {
        const query = customerNameInput.value.trim();

        if (query.length < 2) {
            customerSuggestionsDiv.style.display = 'none';
            return;
        }

        try {
            const ctx = '<%= request.getContextPath() %>'; // chỉ lấy contextPath từ JSP
            const response = await fetch(
                ctx + "/customerservice/customer-search?name=" + encodeURIComponent(query) + "&limit=10"
            );

            const customers = await response.json();

            if (customers.length > 0) {
                customerSuggestionsDiv.innerHTML = customers.map(c => {
                    const customerId = c.customerId ?? '';
                    const fullName = String(c.fullName ?? 'N/A');
                    const phone = String(c.phoneNumber ?? 'N/A');
                    const email = String(c.email ?? 'N/A');

                    return `
        <div class="autocomplete-item"
             data-customer-id="${customerId}"
             data-customer-name="${fullName}"
             data-phone="${phone}"
             data-email="${email}">
            <div class="customer-name">${fullName}</div>
            <div class="customer-details">
                📞 ${phone} | 📧 ${email}
            </div>
        </div>
    `;
                }).join('');

                customerSuggestionsDiv.style.display = 'block';

                // Add click handlers
                document.querySelectorAll('#customerSuggestions .autocomplete-item').forEach(item => {
                    item.addEventListener('click', function() {
                        const customerId = this.dataset.customerId;
                        const customerName = this.dataset.customerName;

                        customerNameInput.value = customerName;
                        customerIdHidden.value = customerId;
                        customerNameHidden.value = customerName;
                        selectedCustomerInfo.innerHTML = `
                            <span class="text-success">
                                <i class="bi bi-check-circle"></i> Selected: ${customerName}
                            </span>
                        `;
                        customerSuggestionsDiv.style.display = 'none';

                        // Reset vehicle selection and fetch vehicles for this customer
                        document.getElementById('vehicleSearch').value = '';
                        document.getElementById('vehicleIdInput').value = '';
                        document.getElementById('selectedVehicleInfo').innerHTML = `
                            <span class="text-muted">Search vehicles for this customer...</span>
                        `;
                    });
                });
            } else {
                customerSuggestionsDiv.style.display = 'none';
            }
        } catch (error) {
            console.error('Error fetching customers:', error);
        }
    }, 300);

    customerNameInput.addEventListener('input', searchCustomers);

    // Vehicle search with autocomplete
    const vehicleSearchInput = document.getElementById('vehicleSearch');
    const vehicleSuggestionsDiv = document.getElementById('vehicleSuggestions');
    const vehicleIdInput = document.getElementById('vehicleIdInput');
    const selectedVehicleInfo = document.getElementById('selectedVehicleInfo');

    const searchVehicles = debounce(async function() {
        const query = vehicleSearchInput.value.trim();

        if (query.length < 1) {
            vehicleSuggestionsDiv.style.display = 'none';
            return;
        }

        try {
            const customerId = customerIdHidden.value;
            const ctx = '<%= request.getContextPath() %>'; // chỉ lấy contextPath từ JSP

            let url = ctx + "/customerservice/vehicles-search?query=" + encodeURIComponent(query);

            // If customer is selected, add customer ID to filter vehicles
            if (customerId) {
                url += `&id=${customerId}`;
            }

            const response = await fetch(url);
            const vehicles = await response.json();

            if (vehicles.length > 0) {
                vehicleSuggestionsDiv.innerHTML = vehicles.map(v => {
                    const vehicleId = v.vehicleID ?? '';
                    const licensePlate = String(v.licensePlate ?? 'N/A');
                    const model = String(v.model ?? 'N/A');
                    const brand = String(v.brand ?? 'N/A');

                    return `
        <div class="autocomplete-item"
             data-vehicle-id="${vehicleId}"
             data-license-plate="${licensePlate}">
            <div class="customer-name">${licensePlate}</div>
            <div class="customer-details">${model} | ${brand}</div>
        </div>
    `;
                }).join('');

                vehicleSuggestionsDiv.style.display = 'block';

                // Add click handlers
                document.querySelectorAll('#vehicleSuggestions .autocomplete-item').forEach(item => {
                    item.addEventListener('click', function() {
                        const vehicleId = this.dataset.vehicleId;
                        const licensePlate = this.dataset.licensePlate;

                        vehicleSearchInput.value = licensePlate;
                        vehicleIdInput.value = vehicleId;
                        selectedVehicleInfo.innerHTML = `
                            <span class="text-success">
                                <i class="bi bi-check-circle"></i> Selected: ${licensePlate}
                            </span>
                        `;
                        vehicleSuggestionsDiv.style.display = 'none';
                    });
                });
            } else {
                vehicleSuggestionsDiv.innerHTML = `
                    <div class="autocomplete-item">
                        <div class="customer-details text-center">No vehicles found</div>
                    </div>
                `;
                vehicleSuggestionsDiv.style.display = 'block';
            }
        } catch (error) {
            console.error('Error fetching vehicles:', error);
        }
    }, 300);

    vehicleSearchInput.addEventListener('input', searchVehicles);

    // Hide dropdowns when clicking outside
    document.addEventListener('click', function(e) {
        if (!customerNameInput.contains(e.target) && !customerSuggestionsDiv.contains(e.target)) {
            customerSuggestionsDiv.style.display = 'none';
        }
        if (!vehicleSearchInput.contains(e.target) && !vehicleSuggestionsDiv.contains(e.target)) {
            vehicleSuggestionsDiv.style.display = 'none';
        }
    });

    // Clear filter button
    document.getElementById('clearFilter').addEventListener('click', function() {
        customerNameInput.value = '';
        customerIdHidden.value = '';
        customerNameHidden.value = '';
        vehicleSearchInput.value = '';
        vehicleIdInput.value = '';
        document.getElementById('sortBySelect').value = 'newest';

        selectedCustomerInfo.innerHTML = '<span class="text-muted">Start typing to search...</span>';
        selectedVehicleInfo.innerHTML = '<span class="text-muted">Start typing to search...</span>';

        // Clear URL and reload
        window.location.href = '${pageContext.request.contextPath}/customerservice/view-all-repairs';
    });

    // Form submit - normal form submission (not AJAX)
    document.getElementById('filterForm').addEventListener('submit', function(e) {
        // Let the form submit normally - no preventDefault
        // The form will submit with all the hidden inputs containing the selected values
    });
</script>
</body>
</html>