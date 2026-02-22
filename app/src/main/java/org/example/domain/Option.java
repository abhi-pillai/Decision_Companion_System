package org.example.domain;

import java.util.Map;

public class Option {
    private String name;
    private Map<String, Double> scores;

    public Option(String name, Map<String, Double> scores) {
        this.name = name;
        this.scores = scores;
    }

    public String getName() {
        return name;
    }

    public Map<String, Double> getScores() {
        return scores;
    }
}