package org.example.domain;

public class DecisionResult {
    private String optionName;
    private double score;
    private String explanation;

    public DecisionResult() {}

    public DecisionResult(String optionName, double score, String explanation) {
        this.optionName = optionName;
        this.score = score;
        this.explanation = explanation;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
