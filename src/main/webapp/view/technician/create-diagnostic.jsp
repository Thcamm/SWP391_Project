<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/technician-diagnostic.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css"/>
<jsp:include page="header.jsp"/>

<div class="layout">
    <jsp:include page="sidebar.jsp"/>
    <main class="main">
        <!-- Breadcrumb -->
<%--        <div class="breadcrumb">--%>
<%--            <a href="${pageContext.request.contextPath}/technician/home">Home</a>--%>
<%--            <span>/</span>--%>
<%--            --%>
<%--            <span>/</span>--%>
<%--            <span>Create</span>--%>
<%--        </div>--%>

        <!-- Header -->
        <div class="page-header">
            <h1 class="page-title">🩺 Create Diagnostic Report</h1>
        </div>

        <!-- Task Info -->
        <div class="card">
            <div class="card-header bg-info">📋 Task Information</div>
            <div class="card-body">
                <div class="row">
                    <div class="col">
                        <p class="mb-2"><strong>Task ID:</strong> #<c:out value="${task.assignmentID}"/></p>
                        <p class="mb-2"><strong>Description:</strong> <c:out value="${task.taskDescription}"/></p>
                        <p class="mb-2"><strong>Vehicle:</strong>
                            <span class="badge badge-secondary"><c:out value="${task.vehicleInfo}"/></span>
                        </p>
                    </div>
                    <div class="col">
                        <p class="mb-2"><strong>Customer:</strong> <c:out value="${task.customerName}"/></p>
                        <p class="mb-2"><strong>Service:</strong> <c:out value="${task.serviceInfo}"/></p>
                        <p class="mb-2"><strong>Assigned Date:</strong> <c:out value="${task.assignedDateFormatted}"/>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Form -->
        <form action="${pageContext.request.contextPath}/technician/create-diagnostic" method="post"
              id="diagnosticForm">
            <c:set var="assignmentIdValue" value="${not empty task.assignmentID ? task.assignmentID : param.assignmentId}"/>
            <input type="hidden" name="assignmentId" value="<c:out value='${assignmentIdValue}'/>"/>


            <!-- Hiển thị error message nếu có -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ❌ <strong>Error:</strong> <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <c:if test="${not empty formErrors}">
                <div class="alert alert-danger"><ul class="mb-0">
                    <c:forEach items="${formErrors}" var="e"><li>${e}</li></c:forEach>
                </ul></div>
            </c:if>


            <!-- Diagnostic details -->
            <div class="card">
                <div class="card-header bg-primary">📝 Diagnostic Details</div>
                <div class="card-body">
                    <div class="form-group">
                        <label for="issueFound" class="form-label">Issue Found <span
                                class="required-star">*</span></label>
                        <textarea name="issueFound" id="issueFound" class="form-control" rows="6"
                                  placeholder="Describe the problems found in detail... (minimum 20 characters)"
                                  required minlength="20"><c:out value="${not empty param.issueFound ? param.issueFound : ''}"/></textarea>
                        <small class="form-hint">Be specific (symptoms, tests, observations...).</small>
                    </div>

                    <div class="form-group">
                        <label for="laborCost" class="form-label">Estimated Labor Cost ($)</label>
                        <input type="number" name="laborCost" id="laborCost" class="form-control"
                               step="0.01" min="0"
                               value="<c:out value='${not empty param.laborCost ? param.laborCost : "0"}'/>"
                               placeholder="0.00"/>
                        <small class="form-hint">Labor/service only (excluding parts).</small>
                    </div>
                </div>
            </div>

            <!-- Parts -->
            <div class="card">
                <div class="card-header bg-success">🔧 Parts Required/Recommended</div>
                <div class="card-body">
                    <p class="text-muted mb-3">💡 Add all parts that need to be replaced or are recommended.</p>

                    <!-- Search -->
                    <div class="row align-items-end mb-3">
                        <div class="col-6">
                            <label class="form-label">Search parts (name / SKU)</label>
                            <input type="text" name="partQuery" class="form-control"
                                   placeholder="Search name or SKU"
                                   value="<c:out value='${partQuery}'/>"/>
                        </div>

                        <div class="col-2">
                            <label class="form-label">Items/dropdown</label>
                            <input type="number" name="size" min="5" max="200" step="5" class="form-control"
                                   value="<c:out value='${partsPageSize != null ? partsPageSize : 20}'/>"/>
                        </div>

                        <div class="col-auto">
                            <button type="submit" name="action" value="filterParts"
                                    class="btn btn-success btn-sm" formnovalidate>Search
                            </button>
                            <button type="submit" name="action" value="clearFilter"
                                    class="btn btn-outline-secondary btn-sm" formnovalidate>Clear
                            </button>
                        </div>
                    </div>

                    <!-- Status + pagination -->
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div class="text-muted">
                            <c:if test="${not empty partQuery}">
                                Filtering by: "<strong><c:out value='${partQuery}'/></strong>"
                            </c:if>
                            <c:if test="${empty availableParts}">
                                <span class="ms-2">No parts found.</span>
                            </c:if>
                        </div>

                        <div class="btn-group">
                            <button type="submit" name="action" value="filterParts"
                                    class="btn btn-sm btn-outline-primary"
                            ${partsPage <= 1 ? 'disabled' : ''}
                                    formaction="${pageContext.request.contextPath}/technician/create-diagnostic?assignmentId=${assignmentIdValue}&page=${partsPage - 1}"
                                    formnovalidate>Prev
                            </button>

                            <span class="btn btn-sm btn-light disabled">
                Page <c:out value="${partsPage != null ? partsPage : 1}"/> /
                <c:out value="${partsTotalPages != null ? partsTotalPages : 1}"/>
              </span>

                            <button type="submit" name="action" value="filterParts"
                                    class="btn btn-sm btn-outline-primary"
                            ${partsTotalPages != null && partsPage >= partsTotalPages ? 'disabled' : ''}
                                    formaction="${pageContext.request.contextPath}/technician/create-diagnostic?assignmentId=${assignmentIdValue}&page=${partsPage + 1}"
                                    formnovalidate>Next
                            </button>
                        </div>
                    </div>

                    <!-- Lines -->
                    <div class="parts-container">
                        <c:choose>
                            <c:when test="${not empty paramValues['partDetailId[]']}">
                                <c:forEach begin="0" end="${fn:length(paramValues['partDetailId[]']) - 1}" var="i">
                                    <c:if test="${empty removeIndex || i ne removeIndex}">
                                        <div class="part-row">
                                            <div class="row">
                                                <div class="col-4">
                                                    <label class="form-label">Part <span class="required-star">*</span></label>
                                                    <%
                                                        // Ý tưởng: nếu selectedId không có trong availableParts, vẫn render option selected từ selectedPartMap
                                                    %>
                                                    <c:set var="selectedId"
                                                           value="${paramValues['partDetailId[]'][i]}"/>
                                                    <c:set var="isInList" value="false"/>
                                                    <c:forEach items="${availableParts}" var="p">
                                                        <c:if test="${p.partDetailId == selectedId}">
                                                            <c:set var="isInList" value="true"/>
                                                        </c:if>
                                                    </c:forEach>

                                                    <select name="partDetailId[]" class="form-control part-select"
                                                            required>
                                                        <option value="">-- Select Part --</option>

                                                        <c:set var="selectedIdInt" value="${selectedId * 1}"/>
                                                        <c:if test="${not empty selectedId && not isInList && selectedPartMap[selectedId] ne null}">
                                                            <option value="${selectedId}" selected
                                                                    data-price="${selectedPartMap[selectedId].unitPrice}"
                                                                    data-stock="${selectedPartMap[selectedId].quantity}">
                                                                (Selected) ${selectedPartMap[selectedId].partName}
                                                                (${selectedPartMap[selectedId].sku}) -
                                                                Stock: ${selectedPartMap[selectedId].quantity}
                                                            </option>
                                                        </c:if>

                                                        <c:forEach items="${availableParts}" var="part">
                                                            <option value="${part.partDetailId}"
                                                                    data-price="${part.unitPrice}"
                                                                    data-stock="${part.quantity}"
                                                                ${paramValues['partDetailId[]'][i] == part.partDetailId ? 'selected' : ''}>
                                                                    ${part.partName} (${part.sku}) -
                                                                Stock: ${part.quantity}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>

                                                <div class="col-2">
                                                    <label class="form-label">Qty <span
                                                            class="required-star">*</span></label>
                                                    <input type="number" name="quantity[]" class="form-control"
                                                           min="1"
                                                           value="<c:out value='${paramValues["quantity[]"][i]}'/>"
                                                           required/>
                                                </div>

                                                <div class="col-2">
                                                    <label class="form-label">Unit Price</label>
                                                    <input type="number" name="unitPrice[]" class="form-control"
                                                           step="0.01"
                                                           value="<c:out value='${paramValues["unitPrice[]"][i]}'/>"
                                                           readonly required/>
                                                </div>

                                                <div class="col-3">
                                                    <label class="form-label">Condition <span
                                                            class="required-star">*</span></label>
                                                    <select name="condition[]" class="form-control" required>
                                                        <option value="REQUIRED"    ${paramValues['condition[]'][i] == 'REQUIRED'    ? 'selected' : ''}>
                                                            Required
                                                        </option>
                                                        <option value="RECOMMENDED" ${paramValues['condition[]'][i] == 'RECOMMENDED' ? 'selected' : ''}>
                                                            Recommended
                                                        </option>
                                                        <option value="OPTIONAL"    ${paramValues['condition[]'][i] == 'OPTIONAL'    ? 'selected' : ''}>
                                                            Optional
                                                        </option>
                                                    </select>
                                                </div>

                                                <div class="col-1">
                                                    <label class="form-label">&nbsp;</label>
                                                    <button type="submit" name="action" value="removePart${i}"
                                                            class="btn btn-danger btn-sm btn-block" formnovalidate>✕
                                                    </button>
                                                </div>
                                            </div>

                                            <div class="row mt-2">
                                                <div class="col">
                                                    <label class="form-label">Reason for Replacement</label>
                                                    <input type="text" name="reason[]" class="form-control"
                                                           value="<c:out value='${paramValues["reason[]"][i]}'/>"
                                                           placeholder="e.g., Brake pad worn 90%..."/>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>

                                <c:if test="${appendEmptyRow}">
                                    <div class="part-row">
                                        <div class="row">
                                            <div class="col-4">
                                                <label class="form-label">Part <span
                                                        class="required-star">*</span></label>
                                                <select name="partDetailId[]" class="form-control part-select" required>
                                                    <option value="">-- Select Part --</option>
                                                    <c:forEach items="${availableParts}" var="part">
                                                        <option value="${part.partDetailId}"
                                                                data-price="${part.unitPrice}"
                                                                data-stock="${part.quantity}">
                                                                ${part.partName} (${part.sku}) - Stock: ${part.quantity}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="col-2">
                                                <label class="form-label">Qty <span
                                                        class="required-star">*</span></label>
                                                <input type="number" name="quantity[]" class="form-control" min="1"
                                                       value="1" required/>
                                            </div>
                                            <div class="col-2">
                                                <label class="form-label">Unit Price</label>
                                                <input type="number" name="unitPrice[]" class="form-control" step="0.01"
                                                       value="0.00" readonly required/>
                                            </div>
                                            <div class="col-3">
                                                <label class="form-label">Condition <span class="required-star">*</span></label>
                                                <select name="condition[]" class="form-control" required>
                                                    <option value="REQUIRED">Required</option>
                                                    <option value="RECOMMENDED" selected>Recommended</option>
                                                    <option value="OPTIONAL">Optional</option>
                                                </select>
                                            </div>
                                            <div class="col-1">
                                                <label class="form-label">&nbsp;</label>
                                                <div class="text-muted text-center">—</div>
                                            </div>
                                        </div>
                                        <div class="row mt-2">
                                            <div class="col">
                                                <label class="form-label">Reason for Replacement</label>
                                                <input type="text" name="reason[]" class="form-control"
                                                       placeholder="e.g., Brake pad worn 90%..."/>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>

                            </c:when>

                            <c:otherwise>
                                <!-- 1 dòng trống mặc định -->
                                <div class="part-row">
                                    <div class="row">
                                        <div class="col-4">
                                            <label class="form-label">Part <span class="required-star">*</span></label>
                                            <select name="partDetailId[]" class="form-control part-select" required>
                                                <option value="">-- Select Part --</option>
                                                <c:forEach items="${availableParts}" var="part">
                                                    <option value="${part.partDetailId}"
                                                            data-price="${part.unitPrice}"
                                                            data-stock="${part.quantity}">
                                                            ${part.partName} (${part.sku}) - Stock: ${part.quantity}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="col-2">
                                            <label class="form-label">Qty <span class="required-star">*</span></label>
                                            <input type="number" name="quantity[]" class="form-control" min="1"
                                                   value="1" required/>
                                        </div>

                                        <div class="col-2">
                                            <label class="form-label">Unit Price</label>
                                            <input type="number" name="unitPrice[]" class="form-control" step="0.01"
                                                   value="0.00" readonly required/>
                                        </div>

                                        <div class="col-3">
                                            <label class="form-label">Condition <span
                                                    class="required-star">*</span></label>
                                            <select name="condition[]" class="form-control" required>
                                                <option value="REQUIRED">Required</option>
                                                <option value="RECOMMENDED" selected>Recommended</option>
                                                <option value="OPTIONAL">Optional</option>
                                            </select>
                                        </div>

                                        <div class="col-1">
                                            <label class="form-label">&nbsp;</label>
                                            <div class="text-muted text-center">—</div>
                                        </div>
                                    </div>

                                    <div class="row mt-2">
                                        <div class="col">
                                            <label class="form-label">Reason for Replacement</label>
                                            <input type="text" name="reason[]" class="form-control"
                                                   placeholder="e.g., Brake pad worn 90%..."/>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Add row -->
                    <div class="mt-3">
                        <button type="submit" name="action" value="addPart" class="btn btn-success btn-sm"
                                formnovalidate>
                            🤞 Add Another Part
                        </button>
                        <small class="text-muted ms-2">Click to add more parts to this diagnostic</small>
                    </div>
                </div>
            </div>

            <!-- Estimate -->
            <div class="alert alert-info">
                <h5 class="alert-heading">💰 Estimate Summary</h5>
                <hr/>
                <div class="row">
                    <div class="col-4">
                        <strong>Labor Cost:</strong>
                        $<span id="laborPreview"><fmt:formatNumber
                            value="${not empty param.laborCost ? param.laborCost : 0}" pattern="#,##0.00"/></span>
                    </div>
                    <div class="col-4">
                        <strong>Parts Cost:</strong>
                        $<span id="partsPreview">0.00</span>
                    </div>
                    <div class="col-4">
                        <strong>Total Estimate:</strong>
                        <span class="text-primary" style="font-size:18px;font-weight:600;">
              $<span id="totalPreview">0.00</span>
            </span>
                    </div>
                </div>
            </div>

            <c:if test="${requestScope.showSuccessInline}">
                <div class="alert alert-success mt-4" role="alert" style="display:flex;align-items:center;justify-content:space-between;">
                    <div>
                        ✅ <strong>Diagnostic report created successfully!</strong>
                        <c:if test="${not empty successMessage}">
                            <span class="ms-2"><c:out value="${successMessage}"/></span>
                        </c:if>
                    </div>
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/technician/tasks" class="btn btn-primary">
                            Go to My Tasks
                        </a>
                        <a href="${pageContext.request.contextPath}/technician/create-diagnostic?assignmentId=${task.assignmentID}" class="btn btn-outline-secondary">
                            Create Another
                        </a>
                    </div>
                </div>
            </c:if>


            <!-- Actions -->
            <div class="mt-4 text-center">
                <button type="submit" name="action" value="submit" class="btn btn-primary btn-lg">
                    😶‍🌫️ Submit Diagnostic Report
                </button>
                <a href="${pageContext.request.contextPath}/technician/tasks" class="btn btn-secondary btn-lg ms-2">
                    💣 Cancel
                </a>
            </div>
        </form>
    </main>
</div>

<jsp:include page="footer.jsp"/>

<!-- ===== Client helpers: unit price + realtime totals ===== -->
<script>
    (function () {
        function toNum(v){ return Number.isFinite(+v) ? +v : 0; }

        function applyFromOption(row, opt){
            if(!row || !opt) return;
            const priceInput = row.querySelector('input[name="unitPrice[]"]');
            const qtyInput   = row.querySelector('input[name="quantity[]"]');
            const p = opt.getAttribute('data-price');
            const s = opt.getAttribute('data-stock');
            if (priceInput && p != null) priceInput.value = Number(p).toFixed(2);
            if (qtyInput && s != null) {
                const stock = parseInt(s, 10);
                qtyInput.max = stock;
                if (!qtyInput.value || Number(qtyInput.value) < 1) qtyInput.value = 1;
                if (Number(qtyInput.value) > stock) qtyInput.value = stock;
            }
        }

        function wireRow(row){
            const select = row.querySelector('select.part-select');
            if(!select) return;
            // init
            applyFromOption(row, select.selectedOptions && select.selectedOptions[0]);
            // on change
            select.addEventListener('change', function(){
                applyFromOption(row, select.selectedOptions && select.selectedOptions[0]);
                recalc();
            });
        }

        document.querySelectorAll('.parts-container .part-row').forEach(wireRow);

        function recalc(){
            let parts = 0;
            document.querySelectorAll('.part-row').forEach(row=>{
                const q = toNum(row.querySelector('input[name="quantity[]"]')?.value);
                const p = toNum(row.querySelector('input[name="unitPrice[]"]')?.value);
                parts += q*p;
            });
            const labor = toNum(document.getElementById('laborCost')?.value);
            document.getElementById('partsPreview').textContent = parts.toFixed(2);
            document.getElementById('laborPreview').textContent = labor.toFixed(2);
            document.getElementById('totalPreview').textContent = (parts+labor).toFixed(2);
        }

        document.addEventListener('input', e=>{
            if (e.target.matches('input[name="quantity[]"], input[name="unitPrice[]"], #laborCost')) recalc();
        });

        // first paint
        document.querySelectorAll('.parts-container .part-row').forEach(row=>{
            const sel = row.querySelector('select.part-select');
            if (sel) applyFromOption(row, sel.selectedOptions && sel.selectedOptions[0]);
        });
        recalc();
    })();
</script>
