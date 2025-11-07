package service.payment;

import common.DbContext;
import dao.invoice.InvoiceDAO;
import dao.payment.PaymentDAO;
import dao.workorder.WorkOrderDAO;
import model.customer.Customer;
import model.invoice.Invoice;
import model.payment.Payment;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.MailService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private InvoiceDAO invoiceDAO;
    @Mock
    private PaymentDAO paymentDAO;
    @Mock
    private WorkOrderDAO workOrderDAO;
    @Mock
    private Connection mockConnection;

    private PaymentService paymentService;
    private WorkOrder sampleWorkOrder;
    private Invoice sampleInvoice;

    @BeforeEach
    void setUp() {
        // Use the constructor we added for dependency injection
        paymentService = new PaymentService(invoiceDAO, paymentDAO, workOrderDAO);

        sampleWorkOrder = new WorkOrder();
        sampleWorkOrder.setWorkOrderId(1);
        sampleWorkOrder.setStatus(WorkOrder.Status.COMPLETE);

        sampleInvoice = new Invoice();
        sampleInvoice.setInvoiceID(10);
        sampleInvoice.setWorkOrderID(1);
        sampleInvoice.setPaymentStatus("UNPAID");
        sampleInvoice.setSubtotal(new BigDecimal("1000"));
        sampleInvoice.setTaxAmount(new BigDecimal("100"));
        sampleInvoice.setTotalAmount(new BigDecimal("1100"));
        sampleInvoice.setBalanceAmount(new BigDecimal("1100"));
        sampleInvoice.setPaidAmount(BigDecimal.ZERO);
    }

    // --- Test Cases for createInvoiceFromWorkOrder ---

    @Test
    @DisplayName("TC01 (Normal): Successfully create an invoice")
    void testCreateInvoice_Success() {
        // We must mock the static DbContext.getConnection()
        try (MockedStatic<DbContext> mockedDbContext = Mockito.mockStatic(DbContext.class)) {
            // Arrange
            mockedDbContext.when(DbContext::getConnection).thenReturn(mockConnection);
            when(workOrderDAO.getWorkOrderById(1)).thenReturn(sampleWorkOrder);
            when(invoiceDAO.existsByWorkOrderID(1)).thenReturn(false);

            WorkOrderDetail detail = new WorkOrderDetail();
            detail.setEstimateAmount(new BigDecimal("1000"));
            when(workOrderDAO.getWorkOrderDetailsForInvoice(1)).thenReturn(List.of(detail));

            when(invoiceDAO.getNextSequenceForDate(any(LocalDate.class))).thenReturn(1);
            when(invoiceDAO.insert(any(Invoice.class))).thenReturn(99); // New Invoice ID

            // Mock the final reload
            sampleInvoice.setInvoiceID(99);
            when(invoiceDAO.getById(99)).thenReturn(sampleInvoice);

            // Act
            Invoice createdInvoice = assertDoesNotThrow(
                    () -> paymentService.createInvoiceFromWorkOrder(1)
            );

            // Assert
            assertNotNull(createdInvoice);
            assertEquals(99, createdInvoice.getInvoiceID());
            assertEquals("UNPAID", createdInvoice.getPaymentStatus());
            assertEquals(0, new BigDecimal("1100").compareTo(createdInvoice.getTotalAmount()));
            verify(mockConnection, times(1)).commit(); // Verify transaction was committed

        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("TC02 (Abnormal): WorkOrder not found")
    void testCreateInvoice_WorkOrderNotFound() {
        try (MockedStatic<DbContext> mockedDbContext = Mockito.mockStatic(DbContext.class)) {
            // Arrange
            mockedDbContext.when(DbContext::getConnection).thenReturn(mockConnection);
            when(workOrderDAO.getWorkOrderById(anyInt())).thenReturn(null);

            // Act & Assert
            Exception exception = assertThrows(Exception.class,
                    () -> paymentService.createInvoiceFromWorkOrder(999)
            );
            assertTrue(exception.getMessage().contains("Không tìm thấy Work Order #999"));
            verify(mockConnection, times(1)).rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TC03 (Abnormal): WorkOrder is not COMPLETE")
    void testCreateInvoice_WorkOrderNotComplete() {
        try (MockedStatic<DbContext> mockedDbContext = Mockito.mockStatic(DbContext.class)) {
            // Arrange
            mockedDbContext.when(DbContext::getConnection).thenReturn(mockConnection);
            sampleWorkOrder.setStatus(WorkOrder.Status.IN_PROGRESS);
            when(workOrderDAO.getWorkOrderById(1)).thenReturn(sampleWorkOrder);

            // Act & Assert
            Exception exception = assertThrows(Exception.class,
                    () -> paymentService.createInvoiceFromWorkOrder(1)
            );
            assertTrue(exception.getMessage().contains("chưa hoàn thành!"));
            verify(mockConnection, times(1)).rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TC04 (Abnormal): Invoice already exists")
    void testCreateInvoice_InvoiceAlreadyExists() {
        try (MockedStatic<DbContext> mockedDbContext = Mockito.mockStatic(DbContext.class)) {
            // Arrange
            mockedDbContext.when(DbContext::getConnection).thenReturn(mockConnection);
            when(workOrderDAO.getWorkOrderById(1)).thenReturn(sampleWorkOrder);
            when(invoiceDAO.existsByWorkOrderID(1)).thenReturn(true);

            // Act & Assert
            Exception exception = assertThrows(Exception.class,
                    () -> paymentService.createInvoiceFromWorkOrder(1)
            );
            assertTrue(exception.getMessage().contains("Invoice already exists"));
            verify(mockConnection, times(1)).rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TC05 (Abnormal): Subtotal is zero")
    void testCreateInvoice_SubtotalIsZero() {
        try (MockedStatic<DbContext> mockedDbContext = Mockito.mockStatic(DbContext.class)) {
            // Arrange
            mockedDbContext.when(DbContext::getConnection).thenReturn(mockConnection);
            when(workOrderDAO.getWorkOrderById(1)).thenReturn(sampleWorkOrder);
            when(invoiceDAO.existsByWorkOrderID(1)).thenReturn(false);
            when(workOrderDAO.getWorkOrderDetailsForInvoice(1)).thenReturn(Collections.emptyList());

            // Act & Assert
            Exception exception = assertThrows(Exception.class,
                    () -> paymentService.createInvoiceFromWorkOrder(1)
            );
            assertTrue(exception.getMessage().contains("Subtotal is zero or invalid"));
            verify(mockConnection, times(1)).rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Test Cases for processPayment ---

    @Test
    @DisplayName("TC06 (Normal): Successfully process a partial payment and send email")
    void testProcessPayment_Success() {
        // Mock static MailService
        try (MockedStatic<MailService> mockedMail = Mockito.mockStatic(MailService.class)) {
            // Arrange
            BigDecimal paymentAmount = new BigDecimal("500");
            when(invoiceDAO.getById(10)).thenReturn(sampleInvoice);
            when(paymentDAO.insert(any(Payment.class))).thenReturn(201); // New Payment ID

            // Mock email sending part
            Customer customer = new Customer();
            customer.setEmail("test@example.com");
            when(workOrderDAO.getCustomerForWorkOrder(1)).thenReturn(customer);
            mockedMail.when(() -> MailService.sendPaymentConfirmationEmail(
                    any(Invoice.class), any(Payment.class), anyString()
            )).thenReturn(true);

            // Act
            Payment payment = assertDoesNotThrow(() -> paymentService.processPayment(
                    10, paymentAmount, "CREDIT_CARD", "REF123", 1, "Partial payment"
            ));

            // Assert
            assertNotNull(payment);
            assertEquals(201, payment.getPaymentID());
            assertEquals(0, paymentAmount.compareTo(payment.getAmount()));

            // Verify email was sent
            mockedMail.verify(() -> MailService.sendPaymentConfirmationEmail(
                    any(Invoice.class), any(Payment.class), eq("test@example.com")
            ), times(1));

        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("TC07 (Abnormal): Invoice not found for payment")
    void testProcessPayment_InvoiceNotFound() throws Exception {
        // Arrange
        when(invoiceDAO.getById(999)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> paymentService.processPayment(
                        999, new BigDecimal("100"), "CASH", null, 1, null
                )
        );
        assertTrue(exception.getMessage().contains("Không tìm thấy hóa đơn #999"));
    }

    @Test
    @DisplayName("TC08 (Abnormal): Invoice is already PAID")
    void testProcessPayment_InvoiceAlreadyPaid() throws Exception {
        // Arrange
        sampleInvoice.setPaymentStatus("PAID");
        when(invoiceDAO.getById(10)).thenReturn(sampleInvoice);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> paymentService.processPayment(
                        10, new BigDecimal("100"), "CASH", null, 1, null
                )
        );
        assertTrue(exception.getMessage().contains("đã thanh toán đầy đủ!"));
    }

    @Test
    @DisplayName("TC09 (Boundary): Payment amount is zero")
    void testProcessPayment_AmountIsZero() throws Exception {
        // Arrange
        when(invoiceDAO.getById(10)).thenReturn(sampleInvoice);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> paymentService.processPayment(
                        10, BigDecimal.ZERO, "CASH", null, 1, null
                )
        );
        assertTrue(exception.getMessage().contains("Số tiền thanh toán phải lớn hơn 0!"));
    }

    @Test
    @DisplayName("TC10 (Abnormal): Overpayment amount")
    void testProcessPayment_Overpayment() throws Exception {
        // Arrange
        when(invoiceDAO.getById(10)).thenReturn(sampleInvoice); // Balance is 1100
        BigDecimal overpayment = new BigDecimal("1200");

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> paymentService.processPayment(
                        10, overpayment, "CASH", null, 1, null
                )
        );
        assertTrue(exception.getMessage().contains("vượt quá số tiền còn lại"));
    }


    // --- Test Cases for voidInvoice ---
    @Test
    @DisplayName("TC12 (Abnormal): Cannot void an invoice that has payments")
    void testVoidInvoice_HasPayments() throws Exception {
        // Arrange
        sampleInvoice.setPaidAmount(new BigDecimal("100")); // Has a payment
        when(invoiceDAO.getById(10)).thenReturn(sampleInvoice);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> paymentService.voidInvoice(10, "Test void")
        );
        assertTrue(exception.getMessage().contains("Không thể hủy hóa đơn đã có thanh toán!"));
    }
}