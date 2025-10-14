package model.employee.admin.rbac;

public class UrlPermissionMapping {
    private int id;
    private String urlPattern;
    private String httpMethod;
    private String permCode;
    private String description;
    private boolean isEnabled;

    // Constructors
    public UrlPermissionMapping() {}

    public UrlPermissionMapping(String urlPattern, String httpMethod, String permCode) {
        this.urlPattern = urlPattern;
        this.httpMethod = httpMethod;
        this.permCode = permCode;
        this.isEnabled = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUrlPattern() { return urlPattern; }
    public void setUrlPattern(String urlPattern) { this.urlPattern = urlPattern; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getPermCode() { return permCode; }
    public void setPermCode(String permCode) { this.permCode = permCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    @Override
    public String toString() {
        return String.format("%s:%s -> %s", httpMethod, urlPattern, permCode);
    }
}
