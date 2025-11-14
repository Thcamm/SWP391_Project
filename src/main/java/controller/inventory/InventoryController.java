package controller.inventory;

import common.utils.PaginationUtils;
import dao.employee.admin.AdminDAO;
import dao.inventory.InventoryTransactionDAO;
import dao.inventory.PartDAO;
import dao.inventory.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.inventory.CharacteristicValue;
import model.inventory.InventoryTransaction;
import model.inventory.PartDetail;
import model.inventory.Supplier;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


@WebServlet(name = "InventoryController", urlPatterns = {"/inventory"})
public class InventoryController extends HttpServlet {
    private PartDAO partDetailDAO = new PartDAO();
    private InventoryTransactionDAO transactionDAO = new InventoryTransactionDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private static final int ITEMS_PER_PAGE = 5; // Number of items per page

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                listInventory(request, response);
            } else if (action.equals("search")) {
                searchInventory(request, response);
            } else if (action.equals("lowstock")) {
                lowStockInventory(request, response);
            } else if (action.equals("add")) {
                showAddForm(request, response);
            } else if (action.equals("edit")) {
                showEditForm(request, response);
            } else if (action.equals("history")) {
                showStockHistory(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/inventory?action=list&error=unknown_action");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action.equals("update")) {
                updatePartInfo(request, response);
            } else if (action.equals("add")) {
                addNewPart(request, response);
            } else if (action.equals("stockIn")) {
                processStockIn(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    /**
     * Display form to add new part
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        // Load all categories for dropdown
        List<String> allCategories = partDetailDAO.getAllCategories();
        request.setAttribute("allCategoriesList", allCategories);

        // Load all characteristic types for selection
        List<CharacteristicValue> characteristicValues = partDetailDAO.getAllCharacteristicValues();
        request.setAttribute("availableCharacteristics", characteristicValues);

        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/view/storekeeper/inventory-form.jsp").forward(request, response);
    }

    /**
     * Display form to edit part information (NOT quantity)
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        PartDetail partDetail = partDetailDAO.getById(id);
        List<CharacteristicValue> types = partDetailDAO.getAllCharacteristicValues();
        request.setAttribute("availableCharacteristics", types);

        if (partDetail != null) {
            // Load characteristics for this part detail
            List<CharacteristicValue> characteristics =
                    partDetailDAO.getCharacteristicsByPartDetailId(id);

            request.setAttribute("partDetail", partDetail);
            request.setAttribute("characteristics", characteristics);
            request.setAttribute("isEdit", true);

            request.getRequestDispatcher("/view/storekeeper/inventory-form.jsp")
                    .forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=list&error=not_found");
        }
    }


    /**
     * Display inventory list with pagination
     */
    /**
     * Display inventory list with pagination
     */
    private void listInventory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<String> allCategories = partDetailDAO.getAllCategories();
        request.setAttribute("allCategoriesList", allCategories);
        List<PartDetail> allList = partDetailDAO.getAllWithCharacteristics();
        List<PartDetail> listToPaginate; // Danh sách sẽ được dùng để phân trang

        // Load all active suppliers
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();
        request.setAttribute("suppliers", suppliers);

        // 2. Kiểm tra xem có bộ lọc danh mục không
        String categoryFilter = request.getParameter("category");
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            // NẾU CÓ: Lấy danh sách đã lọc để phân trang
            listToPaginate = partDetailDAO.getAllWithCharacteristicsByCategory(categoryFilter);
            request.setAttribute("categoryFilter", categoryFilter);
        } else {
            // NẾU KHÔNG: Dùng danh sách đầy đủ để phân trang
            listToPaginate = allList;
        }

        // Get current page
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // 3. Phân trang trên danh sách chính xác (listToPaginate)
        PaginationUtils.PaginationResult<PartDetail> paginationResult =
                PaginationUtils.paginate(listToPaginate, currentPage, ITEMS_PER_PAGE);

        // Set attributes for JSP
        request.setAttribute("inventoryList", paginationResult.getPaginatedData());
        request.setAttribute("currentPage", paginationResult.getCurrentPage());
        request.setAttribute("totalPages", paginationResult.getTotalPages());
        request.setAttribute("totalItems", paginationResult.getTotalItems());
        request.setAttribute("itemsPerPage", paginationResult.getItemsPerPage());

        // 4. Thống kê (luôn tính trên danh sách đầy đủ 'allList' cho các thẻ tiêu đề)
        request.setAttribute("totalValue", partDetailDAO.countTotalPrice());
        request.setAttribute("lowStockCount", partDetailDAO.countLowStockItems());

        request.getRequestDispatcher("/view/storekeeper/inventory-list.jsp").forward(request, response);
    }

    /**
     * Search inventory with pagination
     */
    private void searchInventory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        // Get search parameters
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");
        String location = request.getParameter("location");
        String stockStatus = request.getParameter("stockStatus");
        String priceFromStr = request.getParameter("priceFrom");
        String priceToStr = request.getParameter("priceTo");
        String manufacturer = request.getParameter("manufacturer");

        // Call DAO to filter
        List<PartDetail> filteredList = partDetailDAO.searchWithFilters(
                keyword, category, location, stockStatus, priceFromStr, priceToStr, manufacturer
        );

        // Pagination
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        PaginationUtils.PaginationResult<PartDetail> paginationResult =
                PaginationUtils.paginate(filteredList, currentPage, ITEMS_PER_PAGE);

        // Set attributes
        request.setAttribute("inventoryList", paginationResult.getPaginatedData());
        request.setAttribute("currentPage", paginationResult.getCurrentPage());
        request.setAttribute("totalPages", paginationResult.getTotalPages());
        request.setAttribute("totalItems", paginationResult.getTotalItems());
        request.setAttribute("searchResults", filteredList.size());

        // Preserve filter values
        request.setAttribute("keyword", keyword);
        request.setAttribute("categoryFilter", category);
        request.setAttribute("locationFilter", location);
        request.setAttribute("stockStatusFilter", stockStatus);
        request.setAttribute("priceFromFilter", priceFromStr);
        request.setAttribute("priceToFilter", priceToStr);
        request.setAttribute("manufacturerFilter", manufacturer);

        request.getRequestDispatcher("/view/storekeeper/inventory-list.jsp").forward(request, response);
    }


    /**
     * Display low stock warning with pagination
     */
    private void lowStockInventory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<PartDetail> allList = partDetailDAO.getLowStockItems();

        // Get current page
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // Pagination
        PaginationUtils.PaginationResult<PartDetail> paginationResult =
                PaginationUtils.paginate(allList, currentPage, ITEMS_PER_PAGE);

        // Set attributes
        request.setAttribute("inventoryList", paginationResult.getPaginatedData());
        request.setAttribute("currentPage", paginationResult.getCurrentPage());
        request.setAttribute("totalPages", paginationResult.getTotalPages());
        request.setAttribute("totalItems", paginationResult.getTotalItems());
        request.setAttribute("isLowStockView", true);

        request.getRequestDispatcher("/view/storekeeper/inventory-list.jsp").forward(request, response);
    }

    private void showStockHistory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int partDetailId = Integer.parseInt(request.getParameter("id"));
        PartDetail partDetail = partDetailDAO.getById(partDetailId);

        // TODO: Load stock movement history from database
        // List<StockMovement> history = stockMovementDAO.getByPartDetailId(partDetailId);

        request.setAttribute("partDetail", partDetail);
        // request.setAttribute("stockHistory", history);

        request.getRequestDispatcher("/view/storekeeper/stock-history.jsp").forward(request, response);
    }

    private void updatePartInfo(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        PartDetail partDetail = new PartDetail();
        String partDetailIdStr = request.getParameter("partDetailId");
        int partDetailId = Integer.parseInt(partDetailIdStr);
        partDetail.setPartDetailId(partDetailId);
        //List<CharacteristicValue> characteristics = partDetailDAO.getCharacteristicsByPartDetailId(partDetailId);

        // Only update these information fields
        partDetail.setSku(request.getParameter("sku"));
        partDetail.setLocation(request.getParameter("location"));
        partDetail.setMinStock(Integer.parseInt(request.getParameter("minStock")));
        partDetail.setUnitPrice(new java.math.BigDecimal(request.getParameter("unitPrice")));
        partDetail.setManufacturer(request.getParameter("manufacturer"));
        partDetail.setDescription(request.getParameter("description"));
        // -------------------------

        boolean success = partDetailDAO.update(partDetail);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&message=updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&error=update_failed");
        }
    }

    /**
     * Add new part to inventory
     */
    private void addNewPart(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        try {
            // Get Part information
            String partCode = request.getParameter("partCode");
            String partName = request.getParameter("partName");
            String category = request.getParameter("category");
            String description = request.getParameter("description");

            // Get PartDetail information
            String sku = request.getParameter("sku");
            String location = request.getParameter("location");
            String manufacturer = request.getParameter("manufacturer");
            int minStock = Integer.parseInt(request.getParameter("minStock"));
            BigDecimal unitPrice = new BigDecimal(request.getParameter("unitPrice"));

            // Get Unit information
            String unitName = request.getParameter("unitName");

            System.out.println("=== DEBUG addNewPart ===");
            System.out.println("partCode: " + partCode);
            System.out.println("partName: " + partName);
            System.out.println("category: " + category);
            System.out.println("sku: " + sku);
            System.out.println("manufacturer: " + manufacturer);
            System.out.println("description: " + description);
            System.out.println("unitName: " + unitName);
            System.out.println("minStock: " + minStock);
            System.out.println("location: " + location);
            System.out.println("unitPrice: " + unitPrice);

            // Validate required fields
            if (partCode == null || partCode.trim().isEmpty() ||
                partName == null || partName.trim().isEmpty() ||
                sku == null || sku.trim().isEmpty() ||
                unitName == null || unitName.trim().isEmpty()) {

                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=add&error=missing_required_fields");
                return;
            }

            // Validate price
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=add&error=invalid_price");
                return;
            }
            System.out.println("Calling DAO with minStock=" + minStock + ", unitPrice=" + unitPrice);
            // Call DAO to insert (quantity is set to 0 by default)
            boolean success = partDetailDAO.addNewPart(
                    partCode, partName, category, description,
                    sku, location, manufacturer, minStock, unitPrice,
                    unitName
            );

            if (success) {
                System.out.println("✅ Add part SUCCESS"); // ✅ LOG
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=list&message=add_success");
            } else {
                System.out.println("❌ Add part FAILED"); // ✅ LOG
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=add&error=add_failed");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace(); // ✅ PRINT STACK TRACE
            System.out.println("NumberFormatException: " + e.getMessage());
            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=add&error=invalid_format");
        } catch (SQLException e) {
            e.printStackTrace(); // ✅ PRINT STACK TRACE
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=add&error=database_error");
        } catch (Exception e) {
            e.printStackTrace(); // ✅ PRINT STACK TRACE
            System.out.println("Exception: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getName());
            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=add&error=system_error");
        }
    }

    /**
     * Process stock in transaction
     */
    private void processStockIn(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        HttpSession session = request.getSession();

        // Get storekeeper from session
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=session_expired");
            return;
        }

        int storeKeeperIdByUserID = (int) session.getAttribute("userId");
        int storeKeeperId = adminDAO.getEmployeeIdByUserId(storeKeeperIdByUserID);
        // Get form parameters
        String partDetailIdStr = request.getParameter("partDetailId");
        String partIdStr = request.getParameter("partId");
        String quantityStr = request.getParameter("quantity");
        String unitPriceStr = request.getParameter("unitPrice");
        String supplierIdStr = request.getParameter("supplierId");
        String note = request.getParameter("note");

        if (partDetailIdStr == null || partDetailIdStr.trim().isEmpty() ||
                partIdStr == null || partIdStr.trim().isEmpty() ||
                quantityStr == null || quantityStr.trim().isEmpty() ||
                unitPriceStr == null || unitPriceStr.trim().isEmpty()) {

            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=list&error=missing_fields");
            return;
        }

        try {
            int partDetailId = Integer.parseInt(partDetailIdStr.trim());
            int partId = Integer.parseInt(partIdStr.trim());
            int quantity = Integer.parseInt(quantityStr.trim());
            BigDecimal unitPrice = new BigDecimal(unitPriceStr.trim());

            // Validate quantity and price
            if (quantity <= 0) {
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=list&error=invalid_quantity");
                return;
            }

            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=list&error=invalid_price");
                return;
            }

            // Create transaction object
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setPartId(partId);
            transaction.setPartDetailId(partDetailId);
            transaction.setTransactionType("IN");
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setStoreKeeperId(storeKeeperId);
            transaction.setQuantity(quantity);
            transaction.setUnitPrice(unitPrice);
            transaction.setNote(note != null && !note.trim().isEmpty() ? note.trim() : "Stock in");

            // Handle supplier
            if (supplierIdStr != null && !supplierIdStr.isEmpty() && !supplierIdStr.equals("0")) {
                try {
                    transaction.setSupplierId(Integer.parseInt(supplierIdStr.trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid supplierId format: " + supplierIdStr);
                }
            }

            // Execute stock in
            boolean success = transactionDAO.stockIn(transaction);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=list&message=stock_in_success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/inventory?action=list&error=stock_in_failed");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("NumberFormatException: " + e.getMessage());
            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=list&error=invalid_format");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getName());
            response.sendRedirect(request.getContextPath() +
                    "/inventory?action=list&error=system_error");
        }
    }

}