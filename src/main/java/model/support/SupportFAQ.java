package model.support;

import java.util.Objects;

public class SupportFAQ {
    private Integer FAQId ;
    private String question;
    private String answer ;
    private boolean isActive ;

    public SupportFAQ() {
    }

    public SupportFAQ(Integer FAQId, String question, String answer) {
        this.FAQId = FAQId;
        this.question = question;
        this.answer = answer;
    }

    public SupportFAQ(Integer FAQId, String question, String answer, boolean isActive) {
        this.FAQId = FAQId;
        this.question = question;
        this.answer = answer;
        this.isActive = isActive;
    }

    public Integer getFAQId() {
        return FAQId;
    }

    public void setFAQId(int FAQId) {
        this.FAQId = FAQId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SupportFAQ that)) return false;
        return Objects.equals(FAQId, that.FAQId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FAQId);
    }

    @Override
    public String toString() {
        return "SupportFAQ{" +
                "FAQID=" + FAQId +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
