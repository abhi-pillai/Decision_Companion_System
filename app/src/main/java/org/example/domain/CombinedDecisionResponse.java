package org.example.domain;

import java.util.List;

public class CombinedDecisionResponse {
    private String category;
    private List<DecisionResult> wsmRanking;
    private List<DecisionResult> topsisRanking;
    private String topsisVerdict;
    private String wsmVerdict;

    public CombinedDecisionResponse() {}

    public CombinedDecisionResponse(
            String category,
            List<DecisionResult> wsmRanking,
            List<DecisionResult> topsisRanking,
            String topsisVerdict,
            String wsmVerdict) {
        this.category = category;
        this.wsmRanking = wsmRanking;
        this.topsisRanking = topsisRanking;
        this.topsisVerdict = topsisVerdict;
        this.wsmVerdict = wsmVerdict;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<DecisionResult> getWsmRanking() {
        return wsmRanking;
    }

    public void setWsmRanking(List<DecisionResult> wsmRanking) {
        this.wsmRanking = wsmRanking;
    }

    public List<DecisionResult> getTopsisRanking() {
        return topsisRanking;
    }

    public void setTopsisRanking(List<DecisionResult> topsisRanking) {
        this.topsisRanking = topsisRanking;
    }

    public String getTopsisVerdict() {
        return topsisVerdict;
    }

    public void setTopsisVerdict(String topsisVerdict) {
        this.topsisVerdict = topsisVerdict;
    }

    public String getWsmVerdict() {
        return wsmVerdict;
    }

    public void setWsmVerdict(String wsmVerdict) {
        this.wsmVerdict = wsmVerdict;
    }
}

//Added this to store combined results from both WSM and Topsis