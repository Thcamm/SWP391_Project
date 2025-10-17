package model.support;

public class SupportFAQ {
    private int FAQId ;
    private String question;
    private String answer ;
    private boolean isActive ;

    public SupportFAQ() {
    }

    public SupportFAQ(int FAQId, String question, String answer) {
        this.FAQId = FAQId;
        this.question = question;
        this.answer = answer;
    }

    public SupportFAQ(int FAQId, String question, String answer, boolean isActive) {
        this.FAQId = FAQId;
        this.question = question;
        this.answer = answer;
        this.isActive = isActive;
    }

    public int getFAQId() {
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
    public String toString() {
        return "SupportFAQ{" +
                "FAQID=" + FAQId +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
