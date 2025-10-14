package model.support;

import java.util.Objects;

public class SupportChannel {

    private int        channelId;
    private String      channelName;
    private String iconPath;
    private String ContactLink;
    private String description;
    private Boolean isActive;

    public SupportChannel() {
    }

    public SupportChannel(int channelId, String channelName, String contactLink, String iconPath, String description, Boolean isActive) {
        this.channelId = channelId;
        this.channelName = channelName;
        ContactLink = contactLink;
        this.iconPath = iconPath;
        this.description = description;
        this.isActive = isActive;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getContactLink() {
        return ContactLink;
    }

    public void setContactLink(String contactLink) {
        ContactLink = contactLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SupportChannel that)) return false;
        return channelId == that.channelId && Objects.equals(channelName, that.channelName) && Objects.equals(iconPath, that.iconPath) && Objects.equals(ContactLink, that.ContactLink) && Objects.equals(description, that.description) && Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, channelName, ContactLink, isActive);
    }

    @Override
    public String toString() {
        return "SupportChannel{" +
                "channelId=" + channelId +
                ", channelName='" + channelName + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", ContactLink='" + ContactLink + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
