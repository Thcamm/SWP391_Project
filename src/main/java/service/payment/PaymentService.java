package service.payment;

import common.DbContext;
import dao.invoice.InvoiceDAO;
import dao.payment.PaymentDAO;
import dao.workorder.WorkOrderDAO;
import model.customer.Customer;
import model.dto.InvoiceItemDTO;
import model.invoice.Invoice;
import model.payment.Payment;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import util.MailService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentService {

    private final InvoiceDAO invoiceDAO;
    private final PaymentDAO paymentDAO;
    private final WorkOrderDAO workOrderDAO;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final int DEFAULT_PAYMENT_TERMS_DAYS = 30;

    public PaymentService() {
        this.invoiceDAO = new InvoiceDAO();
        this.paymentDAO = new PaymentDAO();
        this.workOrderDAO = new WorkOrderDAO();
    }

    public PaymentService(InvoiceDAO invoiceDAO, PaymentDAO paymentDAO, WorkOrderDAO workOrderDAO) {
        this.invoiceDAO = invoiceDAO;
        this.paymentDAO = paymentDAO;
        this.workOrderDAO = workOrderDAO;
    }

    public Invoice createInvoiceFromWorkOrder(int workOrderID) throws Exception {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            WorkOrder workOrder = workOrderDAO.getWorkOrderById(workOrderID);
            validateWorkOrderForInvoicing(workOrder, workOrderID);

            if (invoiceDAO.existsByWorkOrderID(workOrderID)) {
                throw new Exception("Invoice already exists for WorkOrder #" + workOrderID);
            }

            BigDecimal subtotal = calculateSubtotalFromWorkOrder(workOrderID);

            if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("Cannot create invoice: Subtotal is zero or invalid. " +
                        "WorkOrder #" + workOrderID + " may not have any details.");
            }

            BigDecimal taxAmount = subtotal.multiply(TAX_RATE);

            String invoiceNumber = invoiceDAO.generateInvoiceNumber(conn);

            if (invoiceNumber == null || invoiceNumber.isEmpty()) {
                throw new Exception("Failed to generate invoice number");
            }

            LocalDate invoiceDate = LocalDate.now();
            LocalDate dueDate = invoiceDate.plusDays(DEFAULT_PAYMENT_TERMS_DAYS);

            Invoice invoice = new Invoice();
            invoice.setWorkOrderID(workOrderID);
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setInvoiceDate(Date.valueOf(invoiceDate));
            invoice.setDueDate(Date.valueOf(dueDate));
            invoice.setSubtotal(subtotal);
            invoice.setTaxAmount(taxAmount);
            invoice.setPaidAmount(BigDecimal.ZERO);
            invoice.setPaymentStatus("UNPAID");
            invoice.setNotes("Generated from WorkOrder #" + workOrderID);

            validateInvoiceBeforeInsert(invoice);

            int invoiceId = invoiceDAO.insert(conn, invoice);
            invoice.setInvoiceID(invoiceId);

            conn.commit();

            Invoice savedInvoice = invoiceDAO.getById(invoiceId);

            if (savedInvoice.getTotalAmount() == null || savedInvoice.getBalanceAmount() == null) {
                throw new Exception("Invoice created but generated columns are NULL. Database trigger may have failed.");
            }

            return savedInvoice;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Error creating invoice: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Payment processPayment(int invoiceID, BigDecimal amount, String method,
                                  String referenceNo, int accountantID, String note) throws Exception {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            Invoice invoice = invoiceDAO.getById(invoiceID);
            validateInvoiceForPayment(invoice, invoiceID, amount);

            if (referenceNo == null || referenceNo.trim().isEmpty()) {
                referenceNo = "REF-" + System.currentTimeMillis();
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());

            Payment payment = new Payment();
            payment.setInvoiceID(invoiceID);
            payment.setWorkOrderID(invoice.getWorkOrderID());
            payment.setPaymentDate(now);
            payment.setAmount(amount);
            payment.setMethod(method);
            payment.setReferenceNo(referenceNo);
            payment.setAccountantID(accountantID);
            payment.setNote(note);

            int paymentId = paymentDAO.insert(conn, payment);
            payment.setPaymentID(paymentId);

            BigDecimal newPaidAmount = invoice.getPaidAmount().add(amount);
            BigDecimal totalAmount = invoice.getTotalAmount();

            String newStatus;
            if (newPaidAmount.compareTo(totalAmount) >= 0) {
                newStatus = "PAID";
                newPaidAmount = totalAmount;
            } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
                newStatus = "PARTIALLY_PAID";
            } else {
                newStatus = "UNPAID";
            }

            invoiceDAO.updatePaymentInfo(conn, invoiceID, newPaidAmount, newStatus);

            conn.commit();

            BigDecimal dbPaidTotal = paymentDAO.getTotalPaidForInvoice(invoiceID);
            if (dbPaidTotal.compareTo(newPaidAmount) != 0) {
                System.err.println("WARNING: Payment sum mismatch! " +
                        "Expected: " + newPaidAmount + ", DB Sum: " + dbPaidTotal);
            }

            invoice = invoiceDAO.getById(invoiceID);

            try {
                String customerEmail = getCustomerEmailFromInvoice(invoice);

                if (customerEmail != null && !customerEmail.isEmpty()) {
                    System.out.println("Sending payment confirmation email to: " + customerEmail);

                    boolean emailSent = MailService.sendPaymentConfirmationEmail(
                            invoice,
                            payment,
                            customerEmail
                    );

                    if (emailSent) {
                        System.out.println("Payment confirmation email sent successfully!");
                    } else {
                        System.err.println("Failed to send email, but payment was successful");
                    }
                } else {
                    System.out.println("Customer email not found, skipping email notification");
                }

            } catch (Exception emailEx) {
                System.err.println("Email sending failed: " + emailEx.getMessage());
                emailEx.printStackTrace();
            }

            return payment;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Payment rolled back: " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Payment processing failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void cancelPayment(int paymentID, String reason, int requestedByUserID) throws Exception {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            Payment payment = paymentDAO.getById(paymentID);
            if (payment == null) {
                throw new Exception("Payment not found: " + paymentID);
            }

            Invoice invoice = invoiceDAO.getById(payment.getInvoiceID());
            if (invoice == null) {
                throw new Exception("Invoice not found for payment");
            }

            if ("VOID".equals(invoice.getPaymentStatus())) {
                throw new Exception("Cannot cancel payment for voided invoice");
            }

            BigDecimal newPaidAmount = invoice.getPaidAmount().subtract(payment.getAmount());
            if (newPaidAmount.compareTo(BigDecimal.ZERO) < 0) {
                newPaidAmount = BigDecimal.ZERO;
            }

            String newStatus;
            if (newPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
                newStatus = "UNPAID";
            } else if (newPaidAmount.compareTo(invoice.getTotalAmount()) < 0) {
                newStatus = "PARTIALLY_PAID";
            } else {
                newStatus = "PAID";
            }

            paymentDAO.delete(conn, paymentID);

            invoiceDAO.updatePaymentInfo(conn, invoice.getInvoiceID(), newPaidAmount, newStatus);

            String logNote = String.format("Payment #%d cancelled by User #%d. Reason: %s",
                    paymentID, requestedByUserID, reason);
            System.out.println(logNote);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Failed to cancel payment: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Object> getPaymentSummary(int invoiceID) throws Exception {
        try {
            Invoice invoice = invoiceDAO.getById(invoiceID);
            if (invoice == null) {
                throw new Exception("Invoice not found: " + invoiceID);
            }

            List<Payment> payments = paymentDAO.getByInvoiceID(invoiceID);

            BigDecimal calculatedTotal = payments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> summary = new HashMap<>();
            summary.put("invoiceID", invoiceID);
            summary.put("invoiceNumber", invoice.getInvoiceNumber());
            summary.put("totalAmount", invoice.getTotalAmount());
            summary.put("paidAmount", invoice.getPaidAmount());
            summary.put("balanceAmount", invoice.getBalanceAmount());
            summary.put("paymentStatus", invoice.getPaymentStatus());
            summary.put("payments", payments);
            summary.put("paymentCount", payments.size());
            summary.put("calculatedTotal", calculatedTotal);

            boolean isConsistent = calculatedTotal.compareTo(invoice.getPaidAmount()) == 0;
            summary.put("isConsistent", isConsistent);

            if (!isConsistent) {
                summary.put("warning", String.format(
                        "Data mismatch: Invoice shows %s but payments sum to %s",
                        invoice.getPaidAmount(), calculatedTotal
                ));
            }

            return summary;

        } catch (SQLException e) {
            throw new Exception("Error getting payment summary: " + e.getMessage(), e);
        }
    }

    private String getCustomerEmailFromInvoice(Invoice invoice) throws SQLException {
        if (invoice == null) return null;

        Customer customer = workOrderDAO.getCustomerForWorkOrder(invoice.getWorkOrderID());

        if (customer != null && customer.getEmail() != null) {
            return customer.getEmail();
        }

        return null;
    }

    public List<Payment> getPaymentsByInvoiceID(int invoiceID) throws Exception {
        try {
            return paymentDAO.getByInvoiceID(invoiceID);
        } catch (SQLException e) {
            throw new Exception("Error getting payment list: " + e.getMessage(), e);
        }
    }

    public Invoice getInvoiceById(int invoiceID) throws Exception {
        try {
            Invoice invoice = invoiceDAO.getById(invoiceID);
            if (invoice == null) {
                throw new Exception("Invoice not found: #" + invoiceID);
            }
            return invoice;
        } catch (SQLException e) {
            throw new Exception("Error getting invoice: " + e.getMessage(), e);
        }
    }

    public List<Invoice> getInvoicesByStatus(String status) throws Exception {
        try {
            return invoiceDAO.getByStatus(status);
        } catch (SQLException e) {
            throw new Exception("Error getting invoices by status: " + e.getMessage(), e);
        }
    }

    public List<Invoice> getOverdueInvoices() throws Exception {
        try {
            return invoiceDAO.getOverdueInvoices();
        } catch (SQLException e) {
            throw new Exception("Error getting overdue invoices: " + e.getMessage(), e);
        }
    }

    public void voidInvoice(int invoiceID, String reason) throws Exception {
        try {
            Invoice invoice = getInvoiceById(invoiceID);

            if ("VOID".equals(invoice.getPaymentStatus())) {
                throw new Exception("Invoice already voided!");
            }

            if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                throw new Exception("Cannot void invoice with payments!");
            }

            invoice.setPaymentStatus("VOID");
            invoice.setNotes((invoice.getNotes() != null ? invoice.getNotes() + "\n" : "") +
                    "VOID: " + reason);

            invoiceDAO.update(invoice);

        } catch (SQLException e) {
            throw new Exception("Error voiding invoice: " + e.getMessage(), e);
        }
    }

    public List<Invoice> searchInvoices(String keyword) throws Exception {
        try {
            return invoiceDAO.search(keyword);
        } catch (SQLException e) {
            throw new Exception("Error searching invoices: " + e.getMessage(), e);
        }
    }

    public List<Invoice> getInvoicesWithPagination(int page, int pageSize) throws Exception {
        try {
            return invoiceDAO.getInvoicesWithPagination(page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Error getting invoice list: " + e.getMessage(), e);
        }
    }

    public int getTotalInvoiceCount() throws Exception {
        try {
            return invoiceDAO.getTotalCount();
        } catch (SQLException e) {
            throw new Exception("Error counting invoices: " + e.getMessage(), e);
        }
    }

    public Payment getPaymentById(int paymentID) throws Exception {
        try {
            Payment payment = paymentDAO.getById(paymentID);
            if (payment == null) {
                throw new Exception("Payment not found: #" + paymentID);
            }
            return payment;
        } catch (SQLException e) {
            throw new Exception("Error getting payment: " + e.getMessage(), e);
        }
    }

    public List<Payment> getAllPayments() throws Exception {
        try {
            return paymentDAO.getAll();
        } catch (SQLException e) {
            throw new Exception("Error getting payment list: " + e.getMessage(), e);
        }
    }

    public BigDecimal getTotalRevenue(Date startDate, Date endDate) throws Exception {
        try {
            return paymentDAO.getTotalRevenueByDateRange(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Error calculating total revenue: " + e.getMessage(), e);
        }
    }

    public List<InvoiceItemDTO> getInvoiceItems(int invoiceID) throws Exception {
        try {
            Invoice invoice = invoiceDAO.getById(invoiceID);
            if (invoice == null) {
                throw new Exception("Invoice not found");
            }

            List<InvoiceItemDTO> items = new ArrayList<>();
            List<WorkOrderDetail> serviceDetails = workOrderDAO.getWorkOrderDetailsForInvoice(invoice.getWorkOrderID());

            for (WorkOrderDetail detail : serviceDetails) {
                InvoiceItemDTO item = new InvoiceItemDTO();
                item.setDescription(detail.getTaskDescription() != null
                        ? detail.getTaskDescription()
                        : "Service item");
                item.setQuantity(detail.getEstimateHours() != null
                        ? detail.getEstimateHours()
                        : BigDecimal.ONE);
                item.setUnitPrice(detail.getEstimateAmount() != null
                        ? detail.getEstimateAmount()
                        : BigDecimal.ZERO);
                item.setAmount(detail.getEstimateAmount() != null
                        ? detail.getEstimateAmount()
                        : BigDecimal.ZERO);

                items.add(item);
            }
            List<InvoiceItemDTO> partsItems = workOrderDAO.getWorkOrderPartsForInvoice(invoice.getWorkOrderID());
            if (partsItems != null) {
                items.addAll(partsItems);
            }
            return items;

        } catch (SQLException e) {
            throw new Exception("Error getting invoice items: " + e.getMessage(), e);
        }
    }

    public List<WorkOrder> getCompletedWorkOrdersWithoutInvoice() throws Exception {
        try {
            List<WorkOrder> allWorkOrders = workOrderDAO.getAllWorkOrders();
            List<WorkOrder> result = new ArrayList<>();

            for (WorkOrder wo : allWorkOrders) {
                if (wo.getStatus() == WorkOrder.Status.COMPLETE) {
                    if (!invoiceDAO.existsByWorkOrderID(wo.getWorkOrderId())) {
                        result.add(wo);
                    }
                }
            }

            return result;

        } catch (SQLException e) {
            throw new Exception("Error getting work order list: " + e.getMessage(), e);
        }
    }

    private void validateWorkOrderForInvoicing(WorkOrder workOrder, int workOrderID) throws Exception {
        if (workOrder == null) {
            throw new Exception("WorkOrder not found: #" + workOrderID);
        }

        if (workOrder.getStatus() != WorkOrder.Status.COMPLETE) {
            throw new Exception("WorkOrder #" + workOrderID + " is not completed! " +
                    "Current status: " + workOrder.getStatus());
        }
    }

    private void validateInvoiceForPayment(Invoice invoice, int invoiceID, BigDecimal amount)
            throws Exception {

        if (invoice == null) {
            throw new Exception("Invoice not found: #" + invoiceID);
        }

        if ("VOID".equals(invoice.getPaymentStatus())) {
            throw new Exception("Invoice #" + invoiceID + " is voided!");
        }

        if ("PAID".equals(invoice.getPaymentStatus())) {
            throw new Exception("Invoice #" + invoiceID + " is already fully paid!");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Payment amount must be greater than zero!");
        }

        BigDecimal remaining = invoice.getBalanceAmount();
        if (amount.compareTo(remaining) > 0) {
            throw new Exception(String.format(
                    "Payment amount (%s) exceeds remaining balance (%s)!",
                    amount, remaining
            ));
        }
    }

    private void validateInvoiceBeforeInsert(Invoice invoice) throws Exception {
        List<String> errors = new ArrayList<>();

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            errors.add("Invoice Number is required");
        }

        if (invoice.getSubtotal() == null || invoice.getSubtotal().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Subtotal must be greater than zero");
        }

        if (invoice.getTaxAmount() == null) {
            errors.add("Tax Amount is required");
        }

        if (invoice.getPaidAmount() == null) {
            errors.add("Paid Amount is required (can be zero)");
        }

        if (invoice.getPaymentStatus() == null || invoice.getPaymentStatus().isEmpty()) {
            errors.add("Payment Status is required");
        }

        if (invoice.getInvoiceDate() == null) {
            errors.add("Invoice Date is required");
        }

        if (invoice.getDueDate() == null) {
            errors.add("Due Date is required");
        }

        if (!errors.isEmpty()) {
            throw new Exception("Invoice validation failed: " + String.join(", ", errors));
        }
    }

    private BigDecimal calculateSubtotalFromWorkOrder(int workOrderID) throws SQLException {
        BigDecimal serviceCost = BigDecimal.ZERO;

        List<WorkOrderDetail> workOrderDetails = workOrderDAO.getWorkOrderDetailsForInvoice(workOrderID);

        for (WorkOrderDetail detail : workOrderDetails) {
            if (detail.getEstimateAmount() != null) {
                serviceCost = serviceCost.add(detail.getEstimateAmount());
            }
        }

        BigDecimal partsCost = BigDecimal.ZERO;
        List<InvoiceItemDTO> partsItems = workOrderDAO.getWorkOrderPartsForInvoice(workOrderID);

        for (InvoiceItemDTO item : partsItems) {
            if (item.getAmount() != null) {
                partsCost = partsCost.add(item.getAmount());
            }
        }
        BigDecimal subtotal = serviceCost.add(partsCost);

        System.out.println("Total Subtotal: " + subtotal +
                " (Service: " + serviceCost +
                " + Parts: " + partsCost + ")");

        return subtotal;
    }
}