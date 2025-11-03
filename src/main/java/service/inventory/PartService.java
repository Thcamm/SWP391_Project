package service.inventory;

import common.constant.MessageConstants;
import common.message.ServiceResult;
import common.utils.PaginationUtils;
import dao.inventory.PartInventoryDAO;
import model.inventory.PartDetail;
import model.pagination.PaginationResponse;

import java.util.List;

public class PartService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 200;

    private final PartInventoryDAO partDAO = new PartInventoryDAO();

    public ServiceResult getAvailableParts() {
        try {
            List<PartDetail> parts = partDAO.getAllAvailableParts();

            if (parts.isEmpty()) {
                return ServiceResult.success(MessageConstants.MSG001, parts);
            }

            return ServiceResult.success(MessageConstants.MSG001, parts);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        }
    }

    public ServiceResult searchParts(String keyword) {
        try {
            List<PartDetail> parts = partDAO.searchAvailableParts(keyword);

            return ServiceResult.success(MessageConstants.MSG001, parts);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        }
    }

    public ServiceResult searchAvailableParts(String keyword, Integer currentPage, Integer itemsPerPage) {
        try {

            int page = (currentPage == null || currentPage < 1) ? DEFAULT_PAGE : currentPage;
            int size = (itemsPerPage == null || itemsPerPage < 1) ? DEFAULT_SIZE : Math.min(itemsPerPage, MAX_SIZE);

            String kw = keyword ==  null ? "" : keyword.trim();

            List<PartDetail> raw = kw.isEmpty() ? partDAO.getAllAvailableParts()
                                                : partDAO.searchAvailableParts(kw);


            if (raw.isEmpty()) {
                return ServiceResult.success(MessageConstants.MSG001,
                        new PaginationResponse<PartDetail>(List.of(), page, size, 0, 0));
            }
            // Phân trang bằng PaginationUtils
            PaginationUtils.PaginationResult<PartDetail> p =
                    PaginationUtils.paginate(raw, page, size);

            // Đóng gói PaginationResponse để trả về UI
            PaginationResponse<PartDetail> response = new PaginationResponse<>(
                    p.getPaginatedData(),
                    p.getCurrentPage(),
                    p.getItemsPerPage(),
                    p.getTotalItems(),
                    p.getTotalPages()
            );

            return ServiceResult.success(MessageConstants.MSG001, response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        }
    }

    public ServiceResult getUnitPriceAndStock(int partDetailId) {
        try {
            PartDetail p = partDAO.getPartDetailById(partDetailId);
            if (p == null) return ServiceResult.error(MessageConstants.ERR002);
            return ServiceResult.success(MessageConstants.MSG001,
                    new java.util.HashMap<>() {{
                        put("unitPrice", p.getUnitPrice());
                        put("quantity", p.getQuantity());
                    }});
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        }
    }

    public ServiceResult getPartDetailById(int partDetailId) {
        try {
            PartDetail part = partDAO.getPartDetailById(partDetailId);

            if (part == null) {
                return ServiceResult.error(MessageConstants.ERR002);
            }

            return ServiceResult.success(MessageConstants.MSG001, part);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        }
    }

    public ServiceResult checkPartAvailability(int partDetailId, int requestedQty) {
        try {
            boolean hasEnough = partDAO.hasEnoughQuantity(partDetailId, requestedQty);

            if (!hasEnough) {
                Integer currentStock = partDAO.getPartDetailQuantity(partDetailId);
                String message = String.format(
                        "Insufficient stock. Available: %d, Requested: %d",
                        currentStock != null ? currentStock : 0,
                        requestedQty
                );
                return ServiceResult.error(MessageConstants.ERR003, message);
            }

            return ServiceResult.success(MessageConstants.MSG001, true);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        }
    }
}
