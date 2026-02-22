package org.example.domain;

public class DecisionResult {
    private String optionName;
    private double score;
    private String explanation;

    public DecisionResult(String optionName, double score, String explanation) {
        this.optionName = optionName;
        this.score = score;
        this.explanation = explanation;
    }

    public String getOptionName() {
        return optionName;
    }

    public double getScore() {
        return score;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return String.format("Option: %-15s | Score: %.4f | %s", optionName, score, explanation);
    }
}