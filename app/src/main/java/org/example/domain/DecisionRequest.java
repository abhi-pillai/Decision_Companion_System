package org.example.domain;

import java.util.List;

public class DecisionRequest {
    private String category;
    private List<Option> options;
    private List<Criterion> criteria;

    public DecisionRequest() {}

    public DecisionRequest(String category, List<Option> options, List<Criterion> criteria) {
        this.category = category;
        this.options = options;
        this.criteria = criteria;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }
}
