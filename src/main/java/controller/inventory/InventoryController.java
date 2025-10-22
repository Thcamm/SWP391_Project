package controller.inventory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.inventory.PartDetail;
import dao.inventory.PartDAO;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "InventoryController", urlPatterns = {"/inventory"})
public class InventoryController extends HttpServlet {
    private PartDAO partDetailDAO = new PartDAO();

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
            } else if (action.equals("detail")) {
                viewDetail(request, response);
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
            if (action.equals("add")) {
                addPartDetail(request, response);
            } else if (action.equals("update")) {
                updatePartDetail(request, response);
            } else if (action.equals("delete")) {
                deletePartDetail(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    // Hiển thị danh sách tồn kho
    private void listInventory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<PartDetail> list = partDetailDAO.getAllWithCharacteristics();

        request.setAttribute("inventoryList", list);
        request.setAttribute("totalItems", list.size());
        request.setAttribute("totalValue", calculateTotalValue(list));
        request.setAttribute("lowStockCount", countLowStock(list));

        request.getRequestDispatcher("/view/storekepper/inventory-list.jsp").forward(request, response);
    }

    // Tìm kiếm
    private void searchInventory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String keyword = request.getParameter("keyword");
        List<PartDetail> list = partDetailDAO.search(keyword);

        request.setAttribute("inventoryList", list);
        request.setAttribute("keyword", keyword);
        request.setAttribute("searchResults", list.size());

        request.getRequestDispatcher("/view/storekepper/inventory-list.jsp").forward(request, response);
    }

    // Cảnh báo tồn kho thấp
    private void lowStockInventory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<PartDetail> list = partDetailDAO.getLowStockItems();

        request.setAttribute("inventoryList", list);
        request.setAttribute("isLowStockView", true);

        request.getRequestDispatcher("/view/storekepper/inventory-list.jsp").forward(request, response);
    }

    // Xem chi tiết
    private void viewDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        PartDetail partDetail = partDetailDAO.getById(id);

        if (partDetail != null) {
            request.setAttribute("partDetail", partDetail);
            request.getRequestDispatcher("/view/storekepper/inventory-list.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list");
        }
    }

    // Thêm mới
    private void addPartDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        PartDetail partDetail = new PartDetail();
        partDetail.setPartId(Integer.parseInt(request.getParameter("partId")));
        partDetail.setSku(request.getParameter("sku"));
        partDetail.setQuantity(Integer.parseInt(request.getParameter("quantity")));
        partDetail.setMinStock(Integer.parseInt(request.getParameter("minStock")));
        partDetail.setUnitPrice(new java.math.BigDecimal(request.getParameter("unitPrice")));
        partDetail.setLocation(request.getParameter("location"));

        boolean success = partDetailDAO.insert(partDetail);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&message=added");
        } else {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&error=add_failed");
        }
    }

    // Cập nhật
    private void updatePartDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        PartDetail partDetail = new PartDetail();
        partDetail.setPartDetailId(Integer.parseInt(request.getParameter("partDetailId")));
        partDetail.setPartId(Integer.parseInt(request.getParameter("partId")));
        partDetail.setSku(request.getParameter("sku"));
        partDetail.setQuantity(Integer.parseInt(request.getParameter("quantity")));
        partDetail.setMinStock(Integer.parseInt(request.getParameter("minStock")));
        partDetail.setUnitPrice(new java.math.BigDecimal(request.getParameter("unitPrice")));
        partDetail.setLocation(request.getParameter("location"));

        boolean success = partDetailDAO.update(partDetail);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&message=updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&error=update_failed");
        }
    }

    // Xóa
    private void deletePartDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = partDetailDAO.delete(id);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&message=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/inventory?action=list&error=delete_failed");
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