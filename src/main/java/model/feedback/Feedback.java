package model.feedback;

import java.time.LocalDateTime;

public class Feedback {
    private Integer feedbackID;
    private Integer customerID;
    private Integer workOrderID;
    private Integer rating;
    private String feedbackText;
    private LocalDateTime feedbackDate;

    public Feedback() {
    }

    public Feedback(Integer feedbackID, Integer customerID, Integer workOrderID, Integer rating, String feedbackText, LocalDateTime feedbackDate) {
        this.feedbackID = feedbackID;
        this.customerID = customerID;
        this.workOrderID = workOrderID;
        this.rating = rating;
        this.feedbackText = feedbackText;
        this.feedbackDate = feedbackDate;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getWorkOrderID() {
        return workOrderID;
    }

    public void setWorkOrderID(Integer workOrderID) {
        this.workOrderID = workOrderID;
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

}
