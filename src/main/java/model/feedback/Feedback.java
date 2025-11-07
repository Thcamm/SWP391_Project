package model.feedback;

import java.time.LocalDateTime;
import java.util.Objects;

public class Feedback {
    private Integer feedbackID;
    private Integer customerID;
    private Integer workOrderID;
    private Integer rating;
    private String feedbackText;
    private LocalDateTime feedbackDate;
    private boolean anonymous;
    private String replyText;
    private LocalDateTime replyDate;
    private Integer replyBy;
    private String Status;

    public Feedback() {
    }

    public Feedback(Integer feedbackID, Integer customerID, Integer workOrderID,
                    Integer rating, String feedbackText, LocalDateTime feedbackDate,
                    boolean anonymous, String replyText, LocalDateTime replyDate, Integer replyBy,String Status) {
        this.feedbackID = feedbackID;
        this.customerID = customerID;
        this.workOrderID = workOrderID;
        this.rating = rating;
        this.feedbackText = feedbackText;
        this.feedbackDate = feedbackDate;
        this.anonymous = anonymous;
        this.replyText = replyText;
        this.replyDate = replyDate;
        this.replyBy = replyBy;
        this.Status = Status;

    }

    public Integer getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(Integer feedbackID) {
        this.feedbackID = feedbackID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Integer getWorkOrderID() {
        return workOrderID;
    }

    public void setWorkOrderID(Integer workOrderID) {
        this.workOrderID = workOrderID;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public LocalDateTime getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(LocalDateTime replyDate) {
        this.replyDate = replyDate;
    }

    public Integer getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(Integer replyBy) {
        this.replyBy = replyBy;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback feedback)) return false;
        return Objects.equals(feedbackID, feedback.feedbackID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackID);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackID=" + feedbackID +
                ", customerID=" + customerID +
                ", workOrderID=" + workOrderID +
                ", rating=" + rating +
                ", isAnonymous=" + anonymous +
                ", feedbackText='" + feedbackText + '\'' +
                ", replyText='" + replyText + '\'' +
                '}';
    }
}
