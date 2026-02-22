package org.example.domain;

public class Criterion {
    private String name;
    private double weight;

    public Criterion(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }
}