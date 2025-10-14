package model.support;

public class SupportFAQ {
    private int FAQID ;
    private String question;
    private String answer ;
    private boolean isActive ;

    public SupportFAQ() {
    }

    public SupportFAQ(int FAQID, String question, String answer, boolean isActive) {
        this.FAQID = FAQID;
        this.question = question;
        this.answer = answer;
        this.isActive = isActive;
    }

    public int getFAQID() {
        return FAQID;
    }

    public void setFAQID(int FAQID) {
        this.FAQID = FAQID;
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
                "FAQID=" + FAQID +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
