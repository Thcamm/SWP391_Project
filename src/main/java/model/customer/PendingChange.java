package model.customer;

import java.time.LocalDateTime;
import java.util.Map;

public class PendingChange {
    private int changeId;
    private int customerId;
    private Map<String, String> fieldsChanged;
    private String token;
    private LocalDateTime tokenExpiry;
    private String status;

    // getters & setters

    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Map<String, String> getFieldsChanged() { return fieldsChanged; }
    public void setFieldsChanged(Map<String, String> fieldsChanged) { this.fieldsChanged = fieldsChanged; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
