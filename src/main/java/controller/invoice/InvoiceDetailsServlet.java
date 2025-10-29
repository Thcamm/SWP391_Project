package controller.invoice;

import dao.invoice.InvoiceDAO;
import dao.workorder.WorkOrderDAO; // Needed to check ownership and get details
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.customer.Customer;
import model.dto.InvoiceItemDTO; // Import the DTO for items
import model.invoice.Invoice;
import model.user.User;
import model.workorder.WorkOrder; // Needed for ownership check

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList; // Import ArrayList
import java.util.List;          // Import List

/**
 * Servlet to display the details of a specific invoice.
 */
@WebServlet(name = "InvoiceDetailsServlet", urlPatterns = {"/invoices/details"})
public class InvoiceDetailsServlet extends HttpServlet {

    private InvoiceDAO invoiceDAO;
    private WorkOrderDAO workOrderDAO; // DAO to get WorkOrder and related details

    @Override
    public void init() throws ServletException {
        this.invoiceDAO = new InvoiceDAO();
        this.workOrderDAO = new WorkOrderDAO(); // Instantiate WorkOrderDAO
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        String roleCode = (session != null) ? (String) session.getAttribute("roleCode") : null;
        Customer customer = (session != null) ? (Customer) session.getAttribute("customer") : null;

        // 1. Authentication Check
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Get Invoice ID
        String invoiceIdParam = request.getParameter("invoiceId");
        if (invoiceIdParam == null || invoiceIdParam.isEmpty()) {
            request.setAttribute("error", "Invoice ID is required.");
            response.sendRedirect(request.getContextPath() + determineRedirectPath(roleCode));
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdParam);

            // 3. Fetch the Invoice
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);

            if (invoice == null) {
                request.setAttribute("error", "Invoice not found.");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }

            // 4. Authorization Check (Your existing logic is fine)
            boolean allowed = isUserAllowedToView(invoice, currentUser, roleCode, customer);
            if (!allowed) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view this invoice.");
                return;
            }

            // === 5. FETCH ADDITIONAL DATA USING NEW DAO METHODS ===
            Customer billedToCustomer = workOrderDAO.getCustomerForWorkOrder(invoice.getWorkOrderID());
            List<InvoiceItemDTO> serviceItems = workOrderDAO.getWorkOrderDetailsForInvoice(invoice.getWorkOrderID());
            List<InvoiceItemDTO> partItems = workOrderDAO.getWorkOrderPartsForInvoice(invoice.getWorkOrderID());

            // Combine service and part items into one list for the JSP
            List<InvoiceItemDTO> allInvoiceItems = new ArrayList<>();
            allInvoiceItems.addAll(serviceItems);
            allInvoiceItems.addAll(partItems);
            // === END FETCH ADDITIONAL DATA ===

            // 6. Set attributes and forward
            request.setAttribute("invoice", invoice);
            request.setAttribute("billedToCustomer", billedToCustomer); // Send customer details
            request.setAttribute("invoiceItems", allInvoiceItems);     // Send combined list of items

            request.getRequestDispatcher("/view/invoice/invoice_details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Invoice ID format.");
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error loading invoice details.");
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    /**
     * Helper method to determine the redirect path based on role.
     */
    private String determineRedirectPath(String roleCode) {
        if (roleCode == null) return "/login";
        switch (roleCode) {
            case "ADMIN": return "/admin/dashboard";
            case "ACCOUNTANT": return "/accountant/dashboard";
            case "CUSTOMER_SERVICE": return "/customerservice/dashboard";
            case "TECH_MANAGER": return "/techmanager/dashboard";
            case "CUSTOMER": return "/Home";
            default: return "/Home";
        }
    }

    /**
     * Checks if the current user is authorized to view the specific invoice.
     * (Using your updated logic that relies on WorkOrderDAO)
     */
    private boolean isUserAllowedToView(Invoice invoice, User currentUser, String roleCode, Customer customer) throws SQLException {
        if (roleCode == null) return false;

        // Roles that can view ANY invoice
        if ("ADMIN".equals(roleCode) || "ACCOUNTANT".equals(roleCode) ||
                "CUSTOMER_SERVICE".equals(roleCode) || "TECH_MANAGER".equals(roleCode)) {
            return true;
        }

        // Customer can only view their OWN invoices
        if ("CUSTOMER".equals(roleCode)) {
            if (customer == null) return false;

            // Fetch WorkOrder to get RequestID
            WorkOrder workOrder = workOrderDAO.getWorkOrderById(invoice.getWorkOrderID()); // Assuming this method exists
            if (workOrder == null) return false; // Or handle error

            // Fetch CustomerID associated with the RequestID
            int workOrderCustomerId = workOrderDAO.getCustomerIdByRequestId(workOrder.getRequestId()); // Using your new method

            return workOrderCustomerId == customer.getCustomerId();
        }
        return false;
    }
}

