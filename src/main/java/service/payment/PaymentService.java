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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final InvoiceDAO invoiceDAO;
    private final PaymentDAO paymentDAO;
    private final WorkOrderDAO workOrderDAO;

    // Tax rate configuration
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% VAT
    private static final int DEFAULT_PAYMENT_TERMS_DAYS = 30;

    public PaymentService() {
        this.invoiceDAO = new InvoiceDAO();
        this.paymentDAO = new PaymentDAO();
        this.workOrderDAO = new WorkOrderDAO();
    }

    public Invoice createInvoiceFromWorkOrder(int workOrderID) throws Exception {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            // Validate WorkOrder
            WorkOrder workOrder = workOrderDAO.getWorkOrderById(workOrderID);
            validateWorkOrderForInvoicing(workOrder, workOrderID);

            // Check if invoice already exists
            if (invoiceDAO.existsByWorkOrderID(workOrderID)) {
                throw new Exception("Invoice already exists for WorkOrder #" + workOrderID);
            }

            // Calculate amounts
            BigDecimal subtotal = calculateSubtotalFromWorkOrder(workOrderID);

            // VALIDATE SUBTOTAL
            if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("Cannot create invoice: Subtotal is zero or invalid. " +
                        "WorkOrder #" + workOrderID + " may not have any details.");
            }

            BigDecimal taxAmount = subtotal.multiply(TAX_RATE);

            // Generate Invoice Number
            String invoiceNumber = generateInvoiceNumber();

            // VALIDATE INVOICE NUMBER
            if (invoiceNumber == null || invoiceNumber.isEmpty()) {
                throw new Exception("Failed to generate invoice number");
            }

            // Calculate dates
            LocalDate invoiceDate = LocalDate.now();
            LocalDate dueDate = invoiceDate.plusDays(DEFAULT_PAYMENT_TERMS_DAYS);

            // Get current timestamp
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Create invoice with ALL required fields
            Invoice invoice = new Invoice();
            invoice.setWorkOrderID(workOrderID);
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setInvoiceDate(Date.valueOf(invoiceDate));
            invoice.setDueDate(Date.valueOf(dueDate));
            invoice.setSubtotal(subtotal);                    // NOT NULL
            invoice.setTaxAmount(taxAmount);                  // NOT NULL
            invoice.setPaidAmount(BigDecimal.ZERO);           // NOT NULL
            invoice.setPaymentStatus("UNPAID");               // NOT NULL
            invoice.setNotes("Generated from WorkOrder #" + workOrderID);
            invoice.setCreatedAt(now);
            invoice.setUpdatedAt(now);

            // VALIDATE before insert
            validateInvoiceBeforeInsert(invoice);

            // Save to database
            int invoiceId = invoiceDAO.insert(invoice);
            invoice.setInvoiceID(invoiceId);

            // COMMIT TRANSACTION
            conn.commit();

            // Reload to get GENERATED columns (TotalAmount, BalanceAmount)
            Invoice savedInvoice = invoiceDAO.getById(invoiceId);

            // FINAL VALIDATION
            if (savedInvoice.getTotalAmount() == null || savedInvoice.getBalanceAmount() == null) {
                conn.rollback();
                throw new Exception("Invoice created but generated columns are NULL. Database trigger may have failed.");
            }

            return savedInvoice;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new Exception("Lỗi database khi tạo invoice: " + e.getMessage(), e);
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
        BigDecimal total = BigDecimal.ZERO;

        List<WorkOrderDetail> workOrderDetails = workOrderDAO.getWorkOrderDetailsForInvoice(workOrderID);

        for (WorkOrderDetail detail : workOrderDetails) {
            if (detail.getEstimateAmount() != null) {
                total = total.add(detail.getEstimateAmount());
            }
        }

        return total;
    }


    public Payment processPayment(int invoiceID, BigDecimal amount, String method,
                                  String referenceNo, int accountantID, String note) throws Exception {
        try {
            // Validate
            Invoice invoice = invoiceDAO.getById(invoiceID);
            validateInvoiceForPayment(invoice, invoiceID, amount);

            // Auto-generate reference if empty
            if (referenceNo == null || referenceNo.trim().isEmpty()) {
                referenceNo = "REF-" + System.currentTimeMillis();
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Create payment
            Payment payment = new Payment();
            payment.setInvoiceID(invoiceID);
            payment.setWorkOrderID(invoice.getWorkOrderID());
            payment.setPaymentDate(now);
            payment.setAmount(amount);
            payment.setMethod(method);
            payment.setReferenceNo(referenceNo);
            payment.setAccountantID(accountantID);
            payment.setNote(note);

            int paymentId = paymentDAO.insert(payment);
            payment.setPaymentID(paymentId);

            // Reload invoice to get updated status
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
                        System.out.println(" Payment confirmation email sent successfully!");
                    } else {
                        System.err.println("Failed to send email, but payment was successful");
                    }
                } else {
                    System.out.println("Customer email not found, skipping email notification");
                }

            } catch (Exception emailEx) {
                // Log but don't fail the payment
                System.err.println(" Email sending failed: " + emailEx.getMessage());
                emailEx.printStackTrace();
                // Payment is still successful even if email fails
            }

            return payment;

        } catch (SQLException e) {
            throw new Exception("Lỗi database khi xử lý thanh toán: " + e.getMessage(), e);
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
            throw new Exception("Lỗi khi lấy danh sách thanh toán: " + e.getMessage(), e);
        }
    }

    public Invoice getInvoiceById(int invoiceID) throws Exception {
        try {
            Invoice invoice = invoiceDAO.getById(invoiceID);
            if (invoice == null) {
                throw new Exception("Không tìm thấy hóa đơn #" + invoiceID);
            }
            return invoice;
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy thông tin hóa đơn: " + e.getMessage(), e);
        }
    }

    public List<Invoice> getInvoicesByStatus(String status) throws Exception {
        try {
            return invoiceDAO.getByStatus(status);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách hóa đơn theo trạng thái: " + e.getMessage(), e);
        }
    }

//    public String checkPaymentStatus(int invoiceID) throws Exception {
//        Invoice invoice = getInvoiceById(invoiceID);
//        return invoice.getPaymentStatus();
//    }

    public List<Invoice> getOverdueInvoices() throws Exception {
        try {
            return invoiceDAO.getOverdueInvoices();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách hóa đơn quá hạn: " + e.getMessage(), e);
        }
    }

    public void voidInvoice(int invoiceID, String reason) throws Exception {
        try {
            Invoice invoice = getInvoiceById(invoiceID);

            if ("VOID".equals(invoice.getPaymentStatus())) {
                throw new Exception("Hóa đơn đã bị hủy trước đó!");
            }

            if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                throw new Exception("Không thể hủy hóa đơn đã có thanh toán!");
            }

            invoice.setPaymentStatus("VOID");
            invoice.setNotes((invoice.getNotes() != null ? invoice.getNotes() + "\n" : "") +
                    "HỦY: " + reason);

            invoiceDAO.update(invoice);

        } catch (SQLException e) {
            throw new Exception("Lỗi khi hủy hóa đơn: " + e.getMessage(), e);
        }
    }

    public List<Invoice> searchInvoices(String keyword) throws Exception {
        try {
            return invoiceDAO.search(keyword);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage(), e);
        }
    }

    public List<Invoice> getInvoicesWithPagination(int page, int pageSize) throws Exception {
        try {
            return invoiceDAO.getInvoicesWithPagination(page, pageSize);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage(), e);
        }
    }

    public int getTotalInvoiceCount() throws Exception {
        try {
            return invoiceDAO.getTotalCount();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi đếm số lượng hóa đơn: " + e.getMessage(), e);
        }
    }

    public Payment getPaymentById(int paymentID) throws Exception {
        try {
            Payment payment = paymentDAO.getById(paymentID);
            if (payment == null) {
                throw new Exception("Không tìm thấy payment #" + paymentID);
            }
            return payment;
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy thông tin thanh toán: " + e.getMessage(), e);
        }
    }

    public List<Payment> getAllPayments() throws Exception {
        try {
            return paymentDAO.getAll();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách thanh toán: " + e.getMessage(), e);
        }
    }

    public BigDecimal getTotalRevenue(Date startDate, Date endDate) throws Exception {
        try {
            return paymentDAO.getTotalRevenueByDateRange(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi tính tổng doanh thu: " + e.getMessage(), e);
        }
    }

    public List<InvoiceItemDTO> getInvoiceItems(int invoiceID) throws Exception {
        try {
            // Get invoice
            Invoice invoice = invoiceDAO.getById(invoiceID);
            if (invoice == null) {
                throw new Exception("Invoice not found");
            }

            // Get work order details
            List<WorkOrderDetail> details = workOrderDAO.getWorkOrderDetailsForInvoice(invoice.getWorkOrderID());

            // CONVERT to InvoiceItemDTO
            List<InvoiceItemDTO> items = new ArrayList<>();
            for (WorkOrderDetail detail : details) {
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

            return items;

        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage(), e);
        }
    }


    public List<WorkOrder> getCompletedWorkOrdersWithoutInvoice() throws Exception {
        try {
            List<WorkOrder> allWorkOrders = workOrderDAO.getAllWorkOrders();
            List<WorkOrder> result = new ArrayList<>();

            for (WorkOrder wo : allWorkOrders) {
                // Chỉ lấy WorkOrder COMPLETE và chưa có invoice
                if (wo.getStatus() == WorkOrder.Status.COMPLETE) {
                    if (!invoiceDAO.existsByWorkOrderID(wo.getWorkOrderId())) {
                        result.add(wo);
                    }
                }
            }

            return result;

        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách Work Order: " + e.getMessage(), e);
        }
    }

    private void validateWorkOrderForInvoicing(WorkOrder workOrder, int workOrderID) throws Exception {
        if (workOrder == null) {
            throw new Exception("Không tìm thấy Work Order #" + workOrderID);
        }

        if (workOrder.getStatus() != WorkOrder.Status.COMPLETE) {
            throw new Exception("Work Order #" + workOrderID + " chưa hoàn thành! " +
                    "Status hiện tại: " + workOrder.getStatus());
        }
    }

    private void validateInvoiceForPayment(Invoice invoice, int invoiceID, BigDecimal amount)
            throws Exception {

        if (invoice == null) {
            throw new Exception("Không tìm thấy hóa đơn #" + invoiceID);
        }

        if ("VOID".equals(invoice.getPaymentStatus())) {
            throw new Exception("Hóa đơn #" + invoiceID + " đã bị hủy!");
        }

        if ("PAID".equals(invoice.getPaymentStatus())) {
            throw new Exception("Hóa đơn #" + invoiceID + " đã thanh toán đầy đủ!");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Số tiền thanh toán phải lớn hơn 0!");
        }

        BigDecimal remaining = invoice.getBalanceAmount();
        if (amount.compareTo(remaining) > 0) {
            throw new Exception(String.format(
                    "Số tiền thanh toán (%s) vượt quá số tiền còn lại (%s)!",
                    amount, remaining
            ));
        }
    }

    private String generateInvoiceNumber() throws Exception {
        try {
            LocalDate now = LocalDate.now();
            String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Get next sequence number for today
            int sequence = invoiceDAO.getNextSequenceForDate(now);

            return String.format("INV-%s-%04d", dateStr, sequence);

        } catch (SQLException e) {
            throw new Exception("Lỗi khi tạo mã hóa đơn: " + e.getMessage(), e);
        }
    }

    private String generateReferenceNo() {
        return "REF-" + System.currentTimeMillis();
    }
}