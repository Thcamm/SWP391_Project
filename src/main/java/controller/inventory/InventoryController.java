package controller.inventory;

import dao.inventory.PartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.inventory.CharacteristicValue;
import model.inventory.PartDetail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import common.utils.PaginationUtils;


@WebServlet(name = "InventoryController", urlPatterns = {"/inventory"})
public class InventoryController extends HttpServlet {
    private PartDAO partDetailDAO = new PartDAO();
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
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
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

        // 1. Lấy danh sách đầy đủ để tính toán thống kê chung
        List<PartDetail> allList = partDetailDAO.getAllWithCharacteristics();

        List<PartDetail> listToPaginate; // Danh sách sẽ được dùng để phân trang

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
        request.setAttribute("totalValue", calculateTotalValue(allList));
        request.setAttribute("lowStockCount", countLowStock(allList));

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
                keyword, category, location, stockStatus, priceFromStr, priceToStr,manufacturer
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

    // Helper methods
    private java.math.BigDecimal calculateTotalValue(List<PartDetail> list) {
        return list.stream()
                .map(PartDetail::getTotalValue)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    private long countLowStock(List<PartDetail> list) {
        return list.stream()
                .filter(PartDetail::isLowStock)
                .count();
    }
}