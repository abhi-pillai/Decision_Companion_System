package org.example.service;

import org.example.domain.Criterion;
import org.example.domain.CombinedDecisionResponse;
import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import org.example.engine.TopsisEngine;
import org.example.engine.WeightedSumEngine;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionService {

    private final WeightedSumEngine wsmEngine = new WeightedSumEngine();
    private final TopsisEngine topsisEngine = new TopsisEngine();

    public CombinedDecisionResponse evaluate(DecisionRequest request) {

        // Step 1: Validate
        validate(request);

        // Step 2: Normalize weights (convert % to decimal)
        normalizeWeights(request.getCriteria());

        // Step 3: Run both engines
        List<DecisionResult> wsmResults = wsmEngine.evaluate(request);
        List<DecisionResult> topsisResults = topsisEngine.evaluate(request);

        // Step 4: Build verdicts
        String topsisVerdict = "Your ideal " + request.getCategory() +
                " would be " + topsisResults.get(0).getOptionName();

        String wsmVerdict = wsmResults.get(0).getOptionName() +
                " shows the most value";

        return new CombinedDecisionResponse(
                request.getCategory(),
                wsmResults,
                topsisResults,
                topsisVerdict,
                wsmVerdict
        );
    }

    private void validate(DecisionRequest request) {

        // Check category
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category must not be empty.");
        }

        // Check options exist
        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new IllegalArgumentException("At least 2 options are required.");
        }

        // Check criteria exist
        if (request.getCriteria() == null || request.getCriteria().isEmpty()) {
            throw new IllegalArgumentException("At least 1 criterion is required.");
        }

        // Check weights sum to 100
        double totalWeight = request.getCriteria().stream()
                .mapToDouble(Criterion::getWeight)
                .sum();
        if (Math.abs(totalWeight - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                "Weights must sum to exactly 100. Current sum: " + totalWeight);
        }

        // Check all options have same metrics as criteria
        List<String> criteriaNames = request.getCriteria().stream()
                .map(Criterion::getName)
                .toList();

        request.getOptions().forEach(option -> {
            criteriaNames.forEach(criterionName -> {
                if (!option.getScores().containsKey(criterionName)) {
                    throw new IllegalArgumentException(
                        "Option '" + option.getName() +
                        "' is missing score for criterion: " + criterionName);
                }
            });
        });

        // Check all scores are between 1 and 10
        request.getOptions().forEach(option ->
            option.getScores().forEach((metric, score) -> {
                if (score < 1 || score > 10) {
                    throw new IllegalArgumentException(
                        "Score for '" + metric + "' in option '" +
                        option.getName() + "' must be between 1 and 10.");
                }
            })
        );
    }

    private void normalizeWeights(List<Criterion> criteria) {
        criteria.forEach(c -> c.setWeight(c.getWeight() / 100.0));
    }
}