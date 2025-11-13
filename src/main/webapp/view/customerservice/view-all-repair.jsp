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
                        <h2 class="mb-4 text-center">Repair History</h2>

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
                                                       value="${param.customerId != null ? param.customerId : ''}">

                                                <div id="customerSuggestions" class="autocomplete-dropdown w-100 mt-1" style="display: none;"></div>
                                            </div>

                                        </div>

                                        <!-- Vehicle Filter -->
                                        <div class="col-md-4">
                                            <label class="form-label" for="vehicleSearch">Vehicle License Plate</label>
                                            <div class="position-relative">
                                                <input type="text"
                                                       class="form-control"
                                                       id="vehicleSearch"
                                                       placeholder="Type to search license plate..."
                                                       autocomplete="off"
                                                       value="${licensePlate != null ? licensePlate : ''}">
                                                <input type="hidden"
                                                       name="vehicleId"
                                                       id="vehicleIdInput"
                                                       value="${param.vehicleId != null ? param.vehicleId : ''}">

                                                <div id="vehicleSuggestions" class="autocomplete-dropdown w-100 mt-1" style="display: none;"></div>
                                            </div>


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

                        <!-- Table / Empty State -->
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
                                                    <a href="${pageContext.request.contextPath}/customerservice/repair-detail?id=${journey.requestID}
                        <c:if test='${not empty fullName}'> &fullName=${fullName}</c:if>
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
    (function() {
        'use strict';

        function debounce(func, delay) {
            let timeoutId;
            return function(...args) {
                clearTimeout(timeoutId);
                timeoutId = setTimeout(() => func.apply(this, args), delay);
            };
        }

        // ====== CUSTOMER SEARCH ======
        const customerNameInput = document.getElementById('customerName');
        const customerIdHidden = document.getElementById('customerIdHidden');
        const customerNameHidden = document.getElementById('customerNameHidden');
        const customerSuggestionsDiv = document.getElementById('customerSuggestions');

        // Flag để tránh trigger search khi đang chọn từ dropdown
        let isSelectingCustomer = false;

        const searchCustomers = debounce(function() {
            // Skip search nếu đang chọn từ dropdown
            if(isSelectingCustomer) {
                return;
            }

            const query = customerNameInput.value.trim();
            if(query.length < 2){
                customerSuggestionsDiv.style.display = 'none';
                return;
            }

            const ctx = '<%= request.getContextPath() %>';
            const url = ctx + "/customerservice/customer-search?name=" + encodeURIComponent(query) + "&limit=10";

            fetch(url)
                .then(res => res.ok ? res.json() : Promise.reject('HTTP ' + res.status))
                .then(customers => {
                    // Kiểm tra lại flag trước khi hiển thị kết quả
                    if(!isSelectingCustomer) {
                        displayCustomerSuggestions(customers);
                    }
                })
                .catch(err => {
                    console.error(err);
                    if(!isSelectingCustomer) {
                        customerSuggestionsDiv.innerHTML = '<div class="autocomplete-item text-danger">Error</div>';
                        customerSuggestionsDiv.style.display = 'block';
                    }
                });
        }, 300);

        function displayCustomerSuggestions(customers){
            customerSuggestionsDiv.innerHTML = '';
            if(!Array.isArray(customers) || customers.length === 0){
                customerSuggestionsDiv.style.display = 'none';
                return;
            }

            customers.forEach(c => {
                const item = document.createElement('div');
                item.className = 'autocomplete-item';
                const customerId = c.customerID || c.customerId || '';
                const fullName = c.fullName || 'N/A';
                const phone = c.phoneNumber || 'N/A';
                const email = c.email || 'N/A';
                item.dataset.customerId = customerId;
                item.dataset.customerName = fullName;

                const nameDiv = document.createElement('div');
                nameDiv.className = 'customer-name';
                nameDiv.textContent = fullName;
                item.appendChild(nameDiv);

                const detailsDiv = document.createElement('div');
                detailsDiv.className = 'customer-details';
                detailsDiv.textContent = 'Phone '+phone+' | Email '+email;
                item.appendChild(detailsDiv);

                item.addEventListener('mousedown', function(e){
                    e.preventDefault();

                    // Set flag TRƯỚC KHI set giá trị
                    isSelectingCustomer = true;

                    // Ẩn dropdown ngay lập tức
                    customerSuggestionsDiv.style.display = 'none';

                    // Set giá trị
                    customerIdHidden.value = this.dataset.customerId;
                    customerNameHidden.value = this.dataset.customerName;
                    customerNameInput.value = this.dataset.customerName;

                    // reset vehicle
                    document.getElementById('vehicleSearch').value = '';
                    document.getElementById('vehicleIdInput').value = '';

                    // Reset flag sau thời gian đủ lâu
                    setTimeout(() => {
                        isSelectingCustomer = false;
                    }, 500);
                });

                customerSuggestionsDiv.appendChild(item);
            });

            customerSuggestionsDiv.style.display = 'block';
        }

        customerNameInput.addEventListener('input', searchCustomers);

        customerNameInput.addEventListener('blur', function(){
            setTimeout(()=>{
                if(!customerIdHidden.value || customerNameInput.value !== customerNameHidden.value){
                    customerNameInput.value = '';
                    customerIdHidden.value = '';
                    customerNameHidden.value = '';
                }
                customerSuggestionsDiv.style.display = 'none';
            }, 200);
        });

        // ====== VEHICLE SEARCH ======
        const vehicleSearchInput = document.getElementById('vehicleSearch');
        const vehicleIdInput = document.getElementById('vehicleIdInput');
        const vehicleSuggestionsDiv = document.getElementById('vehicleSuggestions');

        // Flag để tránh trigger search khi đang chọn từ dropdown
        let isSelectingVehicle = false;

        const searchVehicles = debounce(function(){
            // Skip search nếu đang chọn từ dropdown
            if(isSelectingVehicle) {
                return;
            }

            const query = vehicleSearchInput.value.trim();
            if(query.length < 1){
                vehicleSuggestionsDiv.style.display = 'none';
                return;
            }
            const customerId = customerIdHidden.value;
            const ctx = '<%= request.getContextPath() %>';
            let url = ctx+"/customerservice/vehicles-search?query="+encodeURIComponent(query);
            if(customerId) url += "&id="+customerId;

            fetch(url)
                .then(res => res.ok ? res.json() : Promise.reject('HTTP '+res.status))
                .then(vehicles => {
                    // Kiểm tra lại flag trước khi hiển thị kết quả
                    if(!isSelectingVehicle) {
                        displayVehicleSuggestions(vehicles);
                    }
                })
                .catch(err => {
                    console.error(err);
                    if(!isSelectingVehicle) {
                        vehicleSuggestionsDiv.innerHTML = '<div class="autocomplete-item text-danger">Error</div>';
                        vehicleSuggestionsDiv.style.display = 'block';
                    }
                });
        }, 300);

        function displayVehicleSuggestions(vehicles){
            vehicleSuggestionsDiv.innerHTML = '';
            if(!Array.isArray(vehicles) || vehicles.length === 0){
                vehicleSuggestionsDiv.style.display = 'none';
                return;
            }

            vehicles.forEach(v => {
                const item = document.createElement('div');
                item.className = 'autocomplete-item';
                const vehicleId = v.vehicleID || v.vehicleId || '';
                const licensePlate = v.licensePlate || 'N/A';
                const model = v.model || 'N/A';
                const brand = v.brand || 'N/A';
                item.dataset.vehicleId = vehicleId;
                item.dataset.licensePlate = licensePlate;

                const plateDiv = document.createElement('div');
                plateDiv.className = 'customer-name';
                plateDiv.textContent = licensePlate;
                item.appendChild(plateDiv);

                const detailsDiv = document.createElement('div');
                detailsDiv.className = 'customer-details';
                detailsDiv.textContent = brand + ' ' + model;
                item.appendChild(detailsDiv);

                item.addEventListener('mousedown', function(e){
                    e.preventDefault();

                    // Set flag TRƯỚC KHI set giá trị
                    isSelectingVehicle = true;

                    // Ẩn dropdown ngay lập tức
                    vehicleSuggestionsDiv.style.display = 'none';

                    // Set giá trị
                    vehicleIdInput.value = this.dataset.vehicleId;
                    vehicleSearchInput.value = this.dataset.licensePlate;

                    // Reset flag sau thời gian đủ lâu
                    setTimeout(() => {
                        isSelectingVehicle = false;
                    }, 500);
                });

                vehicleSuggestionsDiv.appendChild(item);
            });

            vehicleSuggestionsDiv.style.display = 'block';
        }

        vehicleSearchInput.addEventListener('input', searchVehicles);

        vehicleSearchInput.addEventListener('blur', function(){
            setTimeout(()=>{
                if(!vehicleIdInput.value){
                    vehicleSearchInput.value = '';
                }
                vehicleSuggestionsDiv.style.display = 'none';
            }, 200);
        });

        // ====== OUTSIDE CLICK HIDE DROPDOWN ======
        document.addEventListener('click', function(e){
            if(!customerNameInput.contains(e.target) && !customerSuggestionsDiv.contains(e.target)){
                customerSuggestionsDiv.style.display = 'none';
            }
            if(!vehicleSearchInput.contains(e.target) && !vehicleSuggestionsDiv.contains(e.target)){
                vehicleSuggestionsDiv.style.display = 'none';
            }
        });

        // ====== CLEAR FILTER ======
        document.getElementById('clearFilter').addEventListener('click', function(){
            customerNameInput.value = '';
            customerIdHidden.value = '';
            customerNameHidden.value = '';
            vehicleSearchInput.value = '';
            vehicleIdInput.value = '';
            document.getElementById('sortBySelect').value = 'newest';

            window.location.href = '${pageContext.request.contextPath}/customerservice/view-all-repairs';
        });
    })();
</script>
</body>
</html>
