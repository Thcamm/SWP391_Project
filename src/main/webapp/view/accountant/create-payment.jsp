<jsp:useBean id="invoice" scope="request" type="model.invoice.Invoice"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);">

                    <!-- Page Header -->
                    <div class="mb-4">
                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${invoice.invoiceID}"
                           class="btn btn-outline-secondary mb-3"
                           style="border-radius: 8px;">
                            <i class="fas fa-arrow-left me-2"></i>Back to Invoice
                        </a>
                        <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                            <i class="fas fa-money-bill-wave me-2" style="color: #059669;"></i>
                            Pay Invoice
                        </h2>
                        <p class="text-muted mb-0">Make payment for invoice ${invoice.invoiceNumber}</p>
                    </div>

                    <div class="row">
                        <!-- Left Column - Payment Form -->
                        <div class="col-lg-7">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header"
                                     style="background: linear-gradient(135deg, #059669 0%, #047857 100%);
                                            color: white;
                                            border-radius: 12px 12px 0 0;
                                            padding: 1.25rem;">
                                    <h5 class="mb-0">
                                        <i class="fas fa-file-invoice-dollar me-2"></i>
                                        Payment Information
                                    </h5>
                                </div>
                                <div class="card-body" style="padding: 2rem;">
                                    <form action="${pageContext.request.contextPath}/accountant/payment"
                                          method="post"
                                          id="paymentForm"
                                          onsubmit="return validatePaymentForm();">
                                        <input type="hidden" name="action" value="create">
                                        <input type="hidden" name="invoiceID" value="${invoice.invoiceID}">
                                        <input type="hidden" id="balanceAmount" value="${invoice.balanceAmount}" />

                                        <!-- Amount -->
                                        <div class="mb-4">
                                            <label for="amount" class="form-label fw-semibold" style="color: #374151;">
                                                <i class="fas fa-coins me-1"></i>
                                                Payment Amount <span class="text-danger">*</span>
                                            </label>
                                            <div class="input-group" style="border-radius: 8px;">
                                                <input type="number"
                                                       class="form-control form-control-lg"
                                                       id="amount"
                                                       name="amount"
                                                       min="1"
                                                       max="${invoice.balanceAmount}"
                                                       step="1000"
                                                       required
                                                       placeholder="Enter amount..."
                                                       style="border-radius: 8px 0 0 8px; font-size: 1.25rem; font-weight: 600;">
                                                <span class="input-group-text"
                                                      style="background-color: #f9fafb;
                                                             border-radius: 0 8px 8px 0;
                                                             font-weight: 600;">
                                                    VND
                                                </span>
                                            </div>
                                            <small class="text-muted">
                                                <i class="fas fa-info-circle me-1"></i>
                                                Maximum amount: <strong><fmt:formatNumber value="${invoice.balanceAmount}" pattern="#,###"/> VND</strong>
                                            </small>

                                            <!-- Quick Amount Buttons -->
                                            <div class="mt-3">
                                                <small class="text-muted d-block mb-2">Quick select:</small>
                                                <div class="btn-group" role="group">
                                                    <button type="button"
                                                            class="btn btn-outline-primary btn-sm"
                                                            onclick="setAmount(${invoice.balanceAmount})"
                                                            style="border-radius: 6px 0 0 6px;">
                                                        <i class="fas fa-check-double me-1"></i>Full
                                                    </button>
                                                    <button type="button"
                                                            class="btn btn-outline-primary btn-sm"
                                                            onclick="setAmount(${invoice.balanceAmount / 2})"
                                                            style="border-radius: 0;">
                                                        <i class="fas fa-divide me-1"></i>Half
                                                    </button>
                                                    <button type="button"
                                                            class="btn btn-outline-primary btn-sm"
                                                            onclick="document.getElementById('amount').value = ''"
                                                            style="border-radius: 0 6px 6px 0;">
                                                        <i class="fas fa-eraser me-1"></i>Clear
                                                    </button>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Payment Method -->
                                        <div class="mb-4">
                                            <label class="form-label fw-semibold" style="color: #374151;">
                                                <i class="fas fa-credit-card me-1"></i>
                                                Payment Method <span class="text-danger">*</span>
                                            </label>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <div class="form-check form-check-custom">
                                                        <input class="form-check-input"
                                                               type="radio"
                                                               name="method"
                                                               id="methodOnline"
                                                               value="ONLINE"
                                                               checked
                                                               style="width: 1.25rem; height: 1.25rem; cursor: pointer;">
                                                        <label class="form-check-label ms-2"
                                                               for="methodOnline"
                                                               style="cursor: pointer; padding: 1rem;
                                                                      border: 2px solid #e5e7eb;
                                                                      border-radius: 8px;
                                                                      width: 100%;
                                                                      display: block;
                                                                      transition: all 0.3s;">
                                                            <i class="fas fa-globe fa-2x mb-2" style="color: #3b82f6;"></i>
                                                            <div class="fw-bold" style="color: #111827;">Online</div>
                                                            <small class="text-muted">Bank transfer, QR Code</small>
                                                        </label>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="form-check form-check-custom">
                                                        <input class="form-check-input"
                                                               type="radio"
                                                               name="method"
                                                               id="methodOffline"
                                                               value="OFFLINE"
                                                               style="width: 1.25rem; height: 1.25rem; cursor: pointer;">
                                                        <label class="form-check-label ms-2"
                                                               for="methodOffline"
                                                               style="cursor: pointer; padding: 1rem;
                                                                      border: 2px solid #e5e7eb;
                                                                      border-radius: 8px;
                                                                      width: 100%;
                                                                      display: block;
                                                                      transition: all 0.3s;">
                                                            <i class="fas fa-money-bill-wave fa-2x mb-2" style="color: #059669;"></i>
                                                            <div class="fw-bold" style="color: #111827;">Offline</div>
                                                            <small class="text-muted">Cash at counter</small>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Reference Number -->
                                        <div class="mb-4">
                                            <label for="referenceNo" class="form-label fw-semibold" style="color: #374151;">
                                                <i class="fas fa-hashtag me-1"></i>
                                                Reference Number
                                            </label>
                                            <input type="text"
                                                   class="form-control"
                                                   id="referenceNo"
                                                   name="referenceNo"
                                                   placeholder="Enter transaction reference (optional)"
                                                   style="border-radius: 8px;">
                                            <small class="text-muted">
                                                <i class="fas fa-info-circle me-1"></i>
                                                Leave blank to auto-generate
                                            </small>
                                        </div>

                                        <!-- Note -->
                                        <div class="mb-4">
                                            <label for="note" class="form-label fw-semibold" style="color: #374151;">
                                                <i class="fas fa-sticky-note me-1"></i>
                                                Note
                                            </label>
                                            <textarea class="form-control"
                                                      id="note"
                                                      name="note"
                                                      rows="3"
                                                      placeholder="Enter a note for this payment (optional)..."
                                                      style="border-radius: 8px;"></textarea>
                                        </div>

                                        <!-- Submit Buttons -->
                                        <div class="d-grid gap-2">
                                            <button type="submit"
                                                    class="btn btn-success btn-lg"
                                                    style="border-radius: 8px; padding: 0.875rem;">
                                                <i class="fas fa-check-circle me-2"></i>
                                                Confirm Payment
                                            </button>
                                            <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${invoice.invoiceID}"
                                               class="btn btn-outline-secondary"
                                               style="border-radius: 8px; padding: 0.875rem;">
                                                <i class="fas fa-times me-2"></i>
                                                Cancel
                                            </a>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Right Column - Invoice Summary (keep as is) -->
                        <!-- ... -->
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/accountant/payment.js"></script>

<jsp:include page="footer.jsp"/>
