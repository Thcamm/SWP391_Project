<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>




<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/edit-diagnostic.css">


<c:set var="vm" value="${requestScope.vm}"/>
<c:set var="diag" value="${vm.diagnostic}"/>
<c:set var="locked" value="${vm.locked}"/>


<jsp:include page="header.jsp"/>
<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main edit-diag">
                <!-- breadcrumb -->
                <%--        <div class="breadcrumb">--%>
                <%--            <a href="${pageContext.request.contextPath}/technician/home">Home</a>--%>
                <%--            <span>/</span>--%>
                <%--            <a href="${pageContext.request.contextPath}/technician/diagnostics">Diagnostics</a>--%>
                <%--            <span>/</span>--%>
                <%--            <span>Edit #<c:out value="${diag.vehicleDiagnosticID}"/></span>--%>
                <%--        </div>--%>

                <!-- page header -->
                <div class="page-header">
                    <h1 class="page-title">✏️ Edit Diagnostic #<c:out value="${diag.vehicleDiagnosticID}"/></h1>
                    <div class="actions">
                        <a class="btn btn-light"
                           href="${pageContext.request.contextPath}/technician/diagnostic/view?diagnosticId=${diag.vehicleDiagnosticID}">
                            ← Back to View
                        </a>
                    </div>
                </div>

                <!-- locked banner -->
                <c:if test="${locked}">
                    <div class="alert alert-warning">
                        💣 This diagnostic is locked because it already has approved parts or an approved work order.
                        You can view details but cannot edit.
                    </div>
                </c:if>

                <!-- flash messages (nếu có) -->
                <jsp:include page="message-display.jsp"/>

                <form action="${pageContext.request.contextPath}/technician/diagnostic/edit" method="post" id="editDiagForm">
                    <input type="hidden" name="diagnosticId" value="${diag.vehicleDiagnosticID}"/>

                    <!-- basic info -->
                    <div class="card">
                        <div class="card-header bg-info">🐧 Diagnostic Info</div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col">
                                    <p class="mb-2"><strong>ID:</strong> #<c:out value="${diag.vehicleDiagnosticID}"/></p>
                                    <p class="mb-2"><strong>Assignment:</strong> #<c:out value="${diag.assignmentID}"/></p>
                                    <p class="mb-2"><strong>Vehicle:</strong>
                                        <span class="badge badge-secondary"><c:out value="${diag.vehicleInfo}"/></span>
                                    </p>
                                </div>
                                <div class="col">
                                    <p class="mb-2"><strong>Technician:</strong> <c:out value="${diag.technicianName}"/></p>
                                    <p class="mb-2"><strong>Created:</strong>
                                        <c:choose>
                                            <c:when test="${not empty diag.createdAt}">
                                                <c:out value="${diag.createdAtFormatted}" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p class="mb-2"><strong>Status:</strong>
                                        <span class="badge
        ${diag.statusString == 'SUBMITTED' ? 'badge-warning' :
          diag.statusString == 'APPROVED'  ? 'badge-success' :
          diag.statusString == 'REJECTED'  ? 'badge-danger'  : 'badge-secondary'}">
                                            ${diag.statusString}
                                        </span>
                                    </p>

                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- details -->
                    <div class="card">
                        <div class="card-header bg-primary">✍️ Details</div>
                        <div class="card-body">
                            <div class="form-group">
                                <label class="form-label">Issue Found <span class="required-star">*</span></label>
                                <textarea name="issueFound" rows="6" class="form-control"
                                          placeholder="Describe the problems found in detail... (minimum 20 characters)"
                                          minlength="20"
                                          <c:if test="${locked}">readonly</c:if>>${diag.issueFound}</textarea>
                                <small class="form-hint">Be specific (symptoms, tests, observations...).</small>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Estimated Labor Cost ($)</label>
                                <input type="number" name="laborCost" id="laborCost" class="form-control"
                                       step="0.01" min="0"
                                       value="<c:out value='${diag.laborCostCalculated != null ? diag.laborCostCalculated : 0}'/>"
                                       <c:if test="${locked}">readonly</c:if>/>
                                <small class="form-hint">Labor/service only (excluding parts).</small>
                            </div>
                        </div>
                    </div>

                    <!-- parts -->
                    <div class="card">
                        <div class="card-header bg-success">🔧 Parts</div>
                        <div class="card-body">
                            <p class="text-muted mb-3">Add/adjust parts for this diagnostic.</p>

                            <!-- search + pagination controls -->
                            <div class="row align-items-end mb-3">
                                <div class="col-6">
                                    <label class="form-label">Search parts (name / SKU)</label>
                                    <input type="text" name="partQuery" class="form-control" value="${partQuery}"
                                           placeholder="Search name or SKU" <c:if test="${locked}">disabled</c:if> />
                                </div>
                                <div class="col-2">
                                    <label class="form-label">Items/dropdown</label>
                                    <input type="number" name="size" min="5" max="200" step="5" class="form-control"
                                           value="${partsPageSize != null ? partsPageSize : 20}" <c:if test="${locked}">disabled</c:if> />
                                </div>
                                <div class="col-auto">
                                    <button type="button" class="btn btn-success btn-sm"
                                            onclick="window.location.href='${pageContext.request.contextPath}/technician/diagnostic/edit?diagnosticId=${diag.vehicleDiagnosticID}&partQuery='
                                                    + encodeURIComponent(document.querySelector('input[name=partQuery]').value)
                                                    + '&size=' + document.querySelector('input[name=size]').value;"
                                            <c:if test="${locked}">disabled</c:if>>
                                        Search
                                    </button>
                                </div>
                            </div>

                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <div class="text-muted">
                                    <c:if test="${not empty partQuery}">
                                        Filtering by: "<strong>${partQuery}</strong>"
                                    </c:if>
                                    <c:if test="${empty availableParts}">
                                        <span class="ms-2">No parts found.</span>
                                    </c:if>
                                </div>

                                <div class="btn-group">
                                    <a class="btn btn-sm btn-outline-primary ${partsPage <= 1 ? 'disabled' : ''}"
                                       href="${pageContext.request.contextPath}/technician/diagnostic/edit?diagnosticId=${diag.vehicleDiagnosticID}&partQuery=${fn:escapeXml(partQuery)}&page=${partsPage-1}&size=${partsPageSize}">
                                        Prev
                                    </a>
                                    <span class="btn btn-sm btn-light disabled">
                Page ${partsPage != null ? partsPage : 1} /
                ${partsTotalPages != null ? partsTotalPages : 1}
              </span>
                                    <a class="btn btn-sm btn-outline-primary ${partsTotalPages != null && partsPage >= partsTotalPages ? 'disabled' : ''}"
                                       href="${pageContext.request.contextPath}/technician/diagnostic/edit?diagnosticId=${diag.vehicleDiagnosticID}&partQuery=${fn:escapeXml(partQuery)}&page=${partsPage+1}&size=${partsPageSize}">
                                        Next
                                    </a>
                                </div>
                            </div>

                            <!-- editable rows -->
                            <div class="parts-container">
                                <%-- Render parts rows based on vm.parts --%>
                                <c:choose>
                                    <c:when test="${not empty vm.parts}">
                                        <%-- Nếu đã có parts trong VM => render ra để sửa --%>
                                        <c:forEach var="p" items="${vm.parts}" varStatus="st">
                                            <div class="part-row">
                                                <div class="row">
                                                    <div class="col-4">
                                                        <label class="form-label">Part <span class="required-star">*</span></label>
                                                        <select name="partDetailId[]" class="form-control part-select" <c:if test="${locked}">disabled</c:if> required>
                                                            <option value="">-- Select Part --</option>
                                                            <!-- option đang chọn (trường hợp không nằm trong trang hiện tại) -->
                                                            <c:if test="${not empty p.partDetailID}">
                                                                <option value="${p.partDetailID}" selected
                                                                        data-price="${p.unitPrice}" data-stock="999999">
                                                                    (Selected) ${p.partName} (${p.sku})
                                                                </option>
                                                            </c:if>
                                                            <c:forEach items="${availableParts}" var="opt">
                                                                <option value="${opt.partDetailId}"
                                                                        data-price="${opt.unitPrice}"
                                                                        data-stock="${opt.quantity}"
                                                                    ${opt.partDetailId == p.partDetailID ? 'selected' : ''}>
                                                                        ${opt.partName} (${opt.sku}) - Stock: ${opt.quantity}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>

                                                    <div class="col-2">
                                                        <label class="form-label">Qty <span class="required-star">*</span></label>
                                                        <input type="number" name="quantity[]" class="form-control" min="1"
                                                               value="${p.quantityNeeded}" <c:if test="${locked}">readonly</c:if> required/>
                                                    </div>

                                                    <div class="col-2">
                                                        <label class="form-label">Unit Price</label>
                                                        <input type="number" name="unitPrice[]" class="form-control" step="0.01"
                                                               value="${p.unitPrice}" readonly required/>
                                                    </div>

                                                    <div class="col-3">
                                                        <label class="form-label">Condition <span class="required-star">*</span></label>
                                                        <select name="condition[]" class="form-control" <c:if test="${locked}">disabled</c:if> required>
                                                            <option value="REQUIRED"    ${p.partCondition == 'REQUIRED'    ? 'selected' : ''}>Required</option>
                                                            <option value="RECOMMENDED" ${p.partCondition == 'RECOMMENDED' ? 'selected' : ''}>Recommended</option>
                                                            <option value="OPTIONAL"    ${p.partCondition == 'OPTIONAL'    ? 'selected' : ''}>Optional</option>
                                                        </select>
                                                    </div>

                                                    <div class="col-1">
                                                        <label class="form-label">&nbsp;</label>
                                                        <c:choose>
                                                            <c:when test="${locked}">
                                                                <div class="text-muted text-center">—</div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="button" class="btn btn-outline-danger btn-sm btn-remove-row" title="Remove part">
                                                                    ×
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>

                                                <div class="row mt-2">
                                                    <div class="col">
                                                        <label class="form-label">Reason for Replacement</label>
                                                        <input type="text" name="reason[]" class="form-control"
                                                               value="${p.reasonForReplacement}" <c:if test="${locked}">readonly</c:if>
                                                               placeholder="e.g., Brake pad worn 90%..."/>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- Không có parts: render 1 dòng trống --%>
                                        <div class="part-row">
                                            <div class="row">
                                                <div class="col-4">
                                                    <label class="form-label">Part <span class="required-star">*</span></label>
                                                    <select name="partDetailId[]" class="form-control part-select" <c:if test="${locked}">disabled</c:if> required>
                                                        <option value="">-- Select Part --</option>
                                                        <c:forEach items="${availableParts}" var="opt">
                                                            <option value="${opt.partDetailId}"
                                                                    data-price="${opt.unitPrice}"
                                                                    data-stock="${opt.quantity}">
                                                                    ${opt.partName} (${opt.sku}) - Stock: ${opt.quantity}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="col-2">
                                                    <label class="form-label">Qty <span class="required-star">*</span></label>
                                                    <input type="number" name="quantity[]" class="form-control" min="1" value="1"
                                                           <c:if test="${locked}">readonly</c:if> required/>
                                                </div>
                                                <div class="col-2">
                                                    <label class="form-label">Unit Price</label>
                                                    <input type="number" name="unitPrice[]" class="form-control" step="0.01" value="0.00" readonly required/>
                                                </div>
                                                <div class="col-3">
                                                    <label class="form-label">Condition <span class="required-star">*</span></label>
                                                    <select name="condition[]" class="form-control" <c:if test="${locked}">disabled</c:if> required>
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
                                                           <c:if test="${locked}">readonly</c:if>
                                                           placeholder="e.g., Brake pad worn 90%..."/>
                                                </div>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- add row -->
                            <div class="mt-3">
                                <button type="button" id="btnAddRow" class="btn btn-success btn-sm" <c:if test="${locked}">disabled</c:if>>
                                    🤞 Add Another Part
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- summary -->
                    <div class="alert alert-info">
                        <h5 class="alert-heading">💰 Estimate Summary</h5>
                        <hr/>
                        <div class="row">
                            <div class="col-4"><strong>Labor Cost:</strong> $<span id="laborPreview">0.00</span></div>
                            <div class="col-4"><strong>Parts Cost:</strong> $<span id="partsPreview">0.00</span></div>
                            <div class="col-4"><strong>Total Estimate:</strong>
                                <span class="text-primary" style="font-size:18px;font-weight:600;">$<span id="totalPreview">0.00</span></span>
                            </div>
                        </div>
                    </div>

                    <!-- actions -->
                    <div class="text-center mt-4">
                        <c:choose>
                            <c:when test="${locked}">
                                <a class="btn btn-secondary btn-lg"
                                   href="${pageContext.request.contextPath}/technician/diagnostic/view?diagnosticId=${diag.vehicleDiagnosticID}">
                                    Close
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button type="submit" class="btn btn-primary btn-lg">😫 Save Changes</button>
                                <a class="btn btn-secondary btn-lg ms-2"
                                   href="${pageContext.request.contextPath}/technician/diagnostic/view?diagnosticId=${diag.vehicleDiagnosticID}">
                                    Cancel
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<!-- client helpers -->
<script>
    (function () {
        // helper: safe number conversion
        function toNum(v){
            var n = Number(v);
            return isFinite(n) ? n : 0;
        }

        function applyFromOption(row, opt){
            if(!row) return;
            var priceInput = row.querySelector('input[name="unitPrice[]"]');
            var qtyInput   = row.querySelector('input[name="quantity[]"]');
            if (!opt) {
                if (priceInput) priceInput.value = (0).toFixed(2);
                return;
            }
            var p = opt.getAttribute('data-price');
            var s = opt.getAttribute('data-stock');
            if (priceInput) {
                var price = toNum(p);
                priceInput.value = price.toFixed(2);
            }
            if (qtyInput) {
                var stock = parseInt(s, 10);
                if (!isFinite(stock)) stock = null;
                if (stock !== null) {
                    qtyInput.max = stock;
                    if (!qtyInput.value || Number(qtyInput.value) < 1) qtyInput.value = 1;
                    if (Number(qtyInput.value) > stock) qtyInput.value = stock;
                } else {
                    qtyInput.removeAttribute('max');
                }
            }
        }

        function wireRow(row){
            if(!row) return;
            var select = row.querySelector('select.part-select');
            var qtyInput = row.querySelector('input[name="quantity[]"]');
            var removeBtn = row.querySelector('.btn-remove-row');

            // initialize from currently selected option
            var selectedOpt = select ? select.options[select.selectedIndex] : null;
            applyFromOption(row, selectedOpt);

            if (select) {
                select.addEventListener('change', function(){
                    var opt = select.options[select.selectedIndex];
                    applyFromOption(row, opt);
                    recalc();
                });
            }

            if (qtyInput) {
                qtyInput.addEventListener('change', function(){
                    var max = qtyInput.max ? parseInt(qtyInput.max, 10) : null;
                    var v = toNum(qtyInput.value);
                    if (v < 1) v = 1;
                    if (max !== null && isFinite(max) && v > max) v = max;
                    qtyInput.value = v;
                    recalc();
                });
            }

            if (removeBtn) {
                removeBtn.addEventListener('click', function(){
                    var container = row.parentNode;
                    var allRows = container.querySelectorAll('.part-row');

                    // Nếu chỉ còn 1 row, không cho xóa (hoặc thay bằng row trống)
                    if (allRows.length <= 1) {
                        // Clear giá trị thay vì xóa row
                        var select = row.querySelector('select.part-select');
                        var qtyInput = row.querySelector('input[name="quantity[]"]');
                        var priceInput = row.querySelector('input[name="unitPrice[]"]');
                        var conditionSelect = row.querySelector('select[name="condition[]"]');
                        var reasonInput = row.querySelector('input[name="reason[]"]');

                        if (select) select.selectedIndex = 0;
                        if (qtyInput) qtyInput.value = 1;
                        if (priceInput) priceInput.value = '0.00';
                        if (conditionSelect) conditionSelect.value = 'RECOMMENDED';
                        if (reasonInput) reasonInput.value = '';

                        recalc();
                        return;
                    }

                    //Nếu còn > 1 row, cho phép xóa
                    row.parentNode.removeChild(row);
                    recalc();
                });
            }
        }

        // initial wiring for existing rows
        document.querySelectorAll('.parts-container .part-row').forEach(function(row){
            wireRow(row);
        });

        // add row logic: use the first select.part-select as canonical source of options
        var addBtn = document.getElementById('btnAddRow');
        if (addBtn) {
            addBtn.addEventListener('click', function(){
                var container = document.querySelector('.parts-container');
                var sourceSelect = document.querySelector('select.part-select');
                // build options HTML from source select (if any)
                var optionsHtml = '<option value="">-- Select Part --</option>';
                if (sourceSelect) {
                    Array.prototype.forEach.call(sourceSelect.options, function(o){
                        var val = o.value || '';
                        var price = o.getAttribute('data-price') || '';
                        var stock = o.getAttribute('data-stock') || '';
                        // escape option text by using the raw textContent (safe enough here)
                        var text = o.textContent || '';
                        optionsHtml += '<option value="' + val + '" data-price="' + price + '" data-stock="' + stock + '">' + text + '</option>';
                    });
                }

                var tmpl =
                    '<div class="part-row">' +
                    '<div class="row">' +
                    '<div class="col-4">' +
                    '<label class="form-label">Part <span class="required-star">*</span></label>' +
                    '<select name="partDetailId[]" class="form-control part-select" required>' +
                    optionsHtml +
                    '</select>' +
                    '</div>' +
                    '<div class="col-2">' +
                    '<label class="form-label">Qty <span class="required-star">*</span></label>' +
                    '<input type="number" name="quantity[]" class="form-control" min="1" value="1" required/>' +
                    '</div>' +
                    '<div class="col-2">' +
                    '<label class="form-label">Unit Price</label>' +
                    '<input type="number" name="unitPrice[]" class="form-control" step="0.01" value="0.00" readonly required/>' +
                    '</div>' +
                    '<div class="col-3">' +
                    '<label class="form-label">Condition <span class="required-star">*</span></label>' +
                    '<select name="condition[]" class="form-control" required>' +
                    '<option value="REQUIRED">Required</option>' +
                    '<option value="RECOMMENDED" selected>Recommended</option>' +
                    '<option value="OPTIONAL">Optional</option>' +
                    '</select>' +
                    '</div>' +
                    '<div class="col-1">' +
                    '<label class="form-label">&nbsp;</label>' +
                    '<div class="text-muted text-center">—</div>' +
                    '</div>' +
                    '</div>' +
                    '<div class="row mt-2">' +
                    '<div class="col">' +
                    '<label class="form-label">Reason for Replacement</label>' +
                    '<input type="text" name="reason[]" class="form-control" placeholder="e.g., Brake pad worn 90%..."/>' +
                    '</div>' +
                    '<div class="col-auto">' +
                    '<button type="button" class="btn btn-outline-danger btn-remove-row" title="Remove row">×</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>';

                container.insertAdjacentHTML('beforeend', tmpl);
                var last = container.lastElementChild;
                wireRow(last);
                recalc();
            });
        }

        function recalc(){
            var partsTotal = 0;
            var rows = document.querySelectorAll('.part-row');
            Array.prototype.forEach.call(rows, function(row){
                var qEl = row.querySelector('input[name="quantity[]"]');
                var pEl = row.querySelector('input[name="unitPrice[]"]');
                var q = qEl ? toNum(qEl.value) : 0;
                var p = pEl ? toNum(pEl.value) : 0;
                partsTotal += q * p;
            });
            var labor = toNum(document.getElementById('laborCost') ? document.getElementById('laborCost').value : 0);
            var partsPreview = document.getElementById('partsPreview');
            var laborPreview = document.getElementById('laborPreview');
            var totalPreview = document.getElementById('totalPreview');
            if (partsPreview) partsPreview.textContent = partsTotal.toFixed(2);
            if (laborPreview) laborPreview.textContent = labor.toFixed(2);
            if (totalPreview) totalPreview.textContent = (partsTotal + labor).toFixed(2);
        }

        // listen for changes that affect totals
        document.addEventListener('input', function(e){
            var target = e.target;
            if (!target) return;
            var name = target.getAttribute('name') || target.id || '';
            if (name === 'quantity[]' || name === 'unitPrice[]' || target.id === 'laborCost') {
                recalc();
            }
        });

        // initial apply + calc
        document.querySelectorAll('.parts-container .part-row').forEach(function(row){
            var sel = row.querySelector('select.part-select');
            var opt = sel ? sel.options[sel.selectedIndex] : null;
            applyFromOption(row, opt);
        });
        recalc();
    })();
</script>