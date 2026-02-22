package org.example.domain;

import java.util.List;

public class DecisionRequest {
    private List<Option> options;
    private List<Criterion> criteria;

    public DecisionRequest(List<Option> options, List<Criterion> criteria) {
        this.options = options;
        this.criteria = criteria;
    }

    public List<Option> getOptions() {
        return options;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }
}