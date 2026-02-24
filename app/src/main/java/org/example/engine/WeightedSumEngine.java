package org.example.engine;

import org.example.domain.Criterion;
import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import org.example.domain.Option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeightedSumEngine implements DecisionEngine {

    @Override
    public List<DecisionResult> evaluate(DecisionRequest request) {
        List<Option> options = request.getOptions();
        List<Criterion> criteria = request.getCriteria();

        List<DecisionResult> results = new ArrayList<>();

        for (Option option : options) {
            double totalScore = 0.0;
            StringBuilder explanation = new StringBuilder("Score breakdown: ");

            for (Criterion criterion : criteria) {
                double score = option.getScores().getOrDefault(criterion.getName(), 0.0);
                double weighted = score * criterion.getWeight();
                totalScore += weighted;

                explanation.append(String.format("[%s: %.2f x %.2f = %.4f] ",
                        criterion.getName(), score, criterion.getWeight(), weighted));
            }
            double roundedScore = Math.round(totalScore * 10000.0) / 10000.0;
            results.add(new DecisionResult(option.getName(), roundedScore, explanation.toString()));
        }

        results.sort(Comparator.comparingDouble(DecisionResult::getScore).reversed());

        return results;
    }
}