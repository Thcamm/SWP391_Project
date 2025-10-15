package model.support;

import java.util.Objects;

public class SupportCategory {

    private int categoryId;
    private String categoryName;
    private String description;

    public SupportCategory() {
    }

    public SupportCategory(int categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SupportCategory that)) return false;
        return categoryId == that.categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(categoryId);
    }

    @Override
    public String toString() {
        return "SupportCategory{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
