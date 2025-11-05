package controller.employee.accountant;

import model.customer.Customer;
import model.dto.InvoiceItemDTO;
import model.invoice.Invoice;
import model.payment.Payment;
import model.workorder.WorkOrder;
import service.payment.PaymentService;
import dao.invoice.InvoiceDAO;
import dao.workorder.WorkOrderDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "InvoiceServlet", urlPatterns = {"/accountant/invoice"})
public class InvoiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PaymentService paymentService;
    private InvoiceDAO invoiceDAO;
    private WorkOrderDAO workOrderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        paymentService = new PaymentService();
        invoiceDAO = new InvoiceDAO();
        workOrderDAO = new WorkOrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.trim().isEmpty()) {
                action = "list";
            }
            switch (action) {
                case "list":
                    listInvoices(request, response);
                    break;

                case "view":
                case "detail":
                    viewInvoice(request, response);
                    break;

                case "create":
                    showCreateForm(request, response);
                    break;

                case "delete":
                    deleteErrorInvoice(request, response);
                    break;

                case "search":
                    searchInvoices(request, response);
                    break;

                case "overdue":
                    listOverdueInvoices(request, response);
                    break;

                default:
                    listInvoices(request, response);
                    break;
            }

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid ID format: " + e.getMessage());
            request.getRequestDispatcher("/view/accountant/invoice-list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            if (action == null || action.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/accountant/invoice");
                return;
            }

            switch (action) {
                case "create":
                    createInvoice(request, response);
                    break;

                case "void":
                    voidInvoice(request, response);
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/accountant/invoice");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }


    private void listInvoices(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");

        int page = 1;
        int pageSize = 10;

        // Parse page number
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Invoice> invoices;
        int totalCount;

        // Filter by status if provided
        if (status != null && !status.isEmpty() && !status.equals("ALL")) {
            invoices = paymentService.getInvoicesByStatus(status);
            totalCount = invoices.size();

            // Apply pagination manually for filtered results
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, invoices.size());

            if (fromIndex < invoices.size()) {
                invoices = invoices.subList(fromIndex, toIndex);
            } else {
                invoices = List.of();
            }

        } else {
            invoices = paymentService.getInvoicesWithPagination(page, pageSize);
            totalCount = paymentService.getTotalInvoiceCount();
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // Set attributes for JSP
        request.setAttribute("invoices", invoices);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("totalCount", totalCount);

        request.getRequestDispatcher("/view/accountant/invoice-list.jsp").forward(request, response);
    }


    private void viewInvoice(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invoice ID is required");
            response.sendRedirect(request.getContextPath() + "/accountant/invoice");
            return;
        }

        int invoiceID = Integer.parseInt(idStr);

        // Get invoice
        Invoice invoice = paymentService.getInvoiceById(invoiceID);

        if (invoice == null) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invoice not found with ID: " + invoiceID);
            response.sendRedirect(request.getContextPath() + "/accountant/invoice");
            return;
        }

        // Get payment history
        List<Payment> payments = paymentService.getPaymentsByInvoiceID(invoiceID);

        // Get invoice items
        List<InvoiceItemDTO> items = paymentService.getInvoiceItems(invoiceID);

        // Get customer info
        Customer customer = null;
        try {
            customer = workOrderDAO.getCustomerForWorkOrder(invoice.getWorkOrderID());
        } catch (SQLException e) {
        }

        // Set attributes for JSP
        request.setAttribute("invoice", invoice);
        request.setAttribute("payments", payments);
        request.setAttribute("items", items);
        request.setAttribute("customer", customer);

        request.getRequestDispatcher("/view/accountant/invoice-detail.jsp").forward(request, response);
    }


    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // Get list of completed work orders without invoice
        List<WorkOrder> workOrders = paymentService.getCompletedWorkOrdersWithoutInvoice();

        request.setAttribute("workOrders", workOrders);
        request.getRequestDispatcher("/view/accountant/create-invoice.jsp").forward(request, response);
    }

    private void createInvoice(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String workOrderIdStr = request.getParameter("workOrderID");

        if (workOrderIdStr == null || workOrderIdStr.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Work Order ID is required");
            response.sendRedirect(request.getContextPath() + "/accountant/invoice?action=create");
            return;
        }

        int workOrderID = Integer.parseInt(workOrderIdStr);


        try {
            // Create invoice
            Invoice invoice = paymentService.createInvoiceFromWorkOrder(workOrderID);

            // Set success message
            HttpSession session = request.getSession();
            session.setAttribute("successMessage",
                    "Invoice created successfully! Invoice Number: " + invoice.getInvoiceNumber());

            // Redirect to invoice detail page
            response.sendRedirect(request.getContextPath() +
                    "/accountant/invoice?action=view&id=" + invoice.getInvoiceID());

        } catch (Exception e) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage",
                    "Failed to create invoice: " + e.getMessage());

            response.sendRedirect(request.getContextPath() + "/accountant/invoice?action=create");
        }
    }

    private void deleteErrorInvoice(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invoice ID is required");
            response.sendRedirect(request.getContextPath() + "/accountant/invoice");
            return;
        }

        int invoiceID = Integer.parseInt(idStr);

        try {
            Invoice invoice = paymentService.getInvoiceById(invoiceID);

            if (invoice == null) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Invoice not found");
                response.sendRedirect(request.getContextPath() + "/accountant/invoice");
                return;
            }

            // Only allow delete if invoice has errors (NULL values)
            boolean hasErrors = (invoice.getTotalAmount() == null ||
                    invoice.getBalanceAmount() == null ||
                    invoice.getPaymentStatus() == null ||
                    invoice.getSubtotal() == null);

            if (hasErrors) {
                // Delete the error invoice
                invoiceDAO.delete(invoiceID);

                HttpSession session = request.getSession();
                session.setAttribute("successMessage",
                        "Error invoice deleted successfully (ID: " + invoiceID + ")");
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage",
                        "Cannot delete valid invoice. Use 'Void' instead for cancellation.");
            }

        } catch (Exception e) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage",
                    "Failed to delete invoice: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/accountant/invoice");
    }

    private void voidInvoice(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String idStr = request.getParameter("invoiceID");
        String reason = request.getParameter("reason");

        if (idStr == null || idStr.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invoice ID is required");
            response.sendRedirect(request.getContextPath() + "/accountant/invoice");
            return;
        }

        int invoiceID = Integer.parseInt(idStr);

        if (reason == null || reason.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Please provide a reason for voiding the invoice");
            response.sendRedirect(request.getContextPath() + "/accountant/invoice?action=view&id=" + invoiceID);
            return;
        }

        try {
            paymentService.voidInvoice(invoiceID, reason);

            HttpSession session = request.getSession();
            session.setAttribute("successMessage",
                    "Invoice #" + invoiceID + " has been voided successfully");

            response.sendRedirect(request.getContextPath() +
                    "/accountant/invoice?action=view&id=" + invoiceID);

        } catch (Exception e) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage",
                    "Failed to void invoice: " + e.getMessage());

            response.sendRedirect(request.getContextPath() +
                    "/accountant/invoice?action=view&id=" + invoiceID);
        }
    }

    private void searchInvoices(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/accountant/invoice");
            return;
        }

        List<Invoice> invoices = paymentService.searchInvoices(keyword);

        request.setAttribute("invoices", invoices);
        request.setAttribute("keyword", keyword);
        request.setAttribute("totalCount", invoices.size());

        request.getRequestDispatcher("/view/accountant/invoice-list.jsp").forward(request, response);
    }


    private void listOverdueInvoices(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<Invoice> invoices = paymentService.getOverdueInvoices();

        request.setAttribute("invoices", invoices);
        request.setAttribute("isOverdueList", true);
        request.setAttribute("totalCount", invoices.size());

        request.getRequestDispatcher("/view/accountant/invoice-list.jsp").forward(request, response);
    }
}