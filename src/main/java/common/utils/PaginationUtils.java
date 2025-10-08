package common.utils;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {

    public static class PaginationResult<T> {
        private final List<T> paginatedData;
        private final int totalItems;
        private final int totalPages;
        private final int currentPage;
        private final int itemsPerPage;

        public PaginationResult(List<T> paginatedData, int totalItems, int totalPages, int currentPage, int itemsPerPage) {
            this.paginatedData = paginatedData;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.itemsPerPage = itemsPerPage;
        }

        public List<T> getPaginatedData() {
            return paginatedData;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getItemsPerPage() {
            return itemsPerPage;
        }


    }

    public static <T> PaginationResult<T> paginate(
            List<T> data,
        int currentPage,
        int itemsPerPage){

        int toralItems = data.size();
        int totalPages = (int)Math.ceil((double) toralItems/itemsPerPage);

        int page = Math.max(1, currentPage);
        page = Math.min(page, totalPages > 0 ? totalPages : 1);

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, toralItems);

        List<T> paginatedData;

        if(startIndex >= toralItems){
            paginatedData = new ArrayList<>();

        }else {
            paginatedData = data.subList(startIndex, endIndex);
        }

        return new PaginationResult<>(
                paginatedData,
                toralItems,
                totalPages,
                page,
                itemsPerPage
        );


    }

    public static class PaginationCalculation {
        public final int totalPages;
        public final int offset;
        public final int safePage;

        public PaginationCalculation(int totalPages, int offset, int safePage) {
            this.totalPages = totalPages;
            this.offset = offset;
            this.safePage = safePage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getOffset() {
            return offset;
        }

        public int getSafePage() {
            return safePage;
        }
    }

    public static PaginationCalculation calculateParams(int totalItems, int currentPage, int intemsPerPage){
        int size = Math.min(1, intemsPerPage);
        int page = Math.max(1, currentPage);

        int totalPages = (int)Math.ceil((double)totalItems / size);
        int safePage = Math.min(page, totalPages > 0 ? totalPages : 1);
        int offset = (safePage - 1) * size;
        if(totalItems == 0) offset =0;
        return new PaginationCalculation(totalPages, offset, safePage);
    }


}
