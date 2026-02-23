package org.example.engine;

import org.example.domain.Criterion;
import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import org.example.domain.Option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopsisEngine implements DecisionEngine {

    @Override
    public List<DecisionResult> evaluate(DecisionRequest request) {
        List<Option> options = request.getOptions();
        List<Criterion> criteria = request.getCriteria();

        int numOptions = options.size();
        int numCriteria = criteria.size();

        // Step 1: Build raw matrix
        double[][] matrix = new double[numOptions][numCriteria];
        for (int i = 0; i < numOptions; i++) {
            for (int j = 0; j < numCriteria; j++) {
                String criterionName = criteria.get(j).getName();
                matrix[i][j] = options.get(i).getScores().getOrDefault(criterionName, 0.0);
            }
        }

        // Step 2: Normalize matrix (divide each value by column vector length)
        double[][] normalized = new double[numOptions][numCriteria];
        for (int j = 0; j < numCriteria; j++) {
            double sumOfSquares = 0.0;
            for (int i = 0; i < numOptions; i++) {
                sumOfSquares += matrix[i][j] * matrix[i][j];
            }
            double vectorLength = Math.sqrt(sumOfSquares);
            for (int i = 0; i < numOptions; i++) {
                normalized[i][j] = vectorLength == 0 ? 0 : matrix[i][j] / vectorLength;
            }
        }

        // Step 3: Apply weights
        double[][] weighted = new double[numOptions][numCriteria];
        for (int i = 0; i < numOptions; i++) {
            for (int j = 0; j < numCriteria; j++) {
                weighted[i][j] = normalized[i][j] * criteria.get(j).getWeight();
            }
        }

        // Step 4: Compute ideal (max) and anti-ideal (min) per criterion
        double[] ideal     = new double[numCriteria];
        double[] antiIdeal = new double[numCriteria];
        for (int j = 0; j < numCriteria; j++) {
            ideal[j]     = Double.MIN_VALUE;
            antiIdeal[j] = Double.MAX_VALUE;
            for (int i = 0; i < numOptions; i++) {
                if (weighted[i][j] > ideal[j])     ideal[j]     = weighted[i][j];
                if (weighted[i][j] < antiIdeal[j]) antiIdeal[j] = weighted[i][j];
            }
        }

        // Step 5: Compute distances and closeness
        List<DecisionResult> results = new ArrayList<>();
        for (int i = 0; i < numOptions; i++) {
            double dPlus  = 0.0; // distance to ideal
            double dMinus = 0.0; // distance to anti-ideal

            for (int j = 0; j < numCriteria; j++) {
                dPlus  += Math.pow(weighted[i][j] - ideal[j], 2);
                dMinus += Math.pow(weighted[i][j] - antiIdeal[j], 2);
            }

            dPlus  = Math.sqrt(dPlus);
            dMinus = Math.sqrt(dMinus);

            double closeness = (dPlus + dMinus) == 0 ? 0 : dMinus / (dPlus + dMinus);

            String explanation = String.format(
                "TOPSIS: d+ (dist to ideal)=%.4f | d- (dist to worst)=%.4f | closeness=%.4f",
                dPlus, dMinus, closeness
            );

            results.add(new DecisionResult(options.get(i).getName(), closeness, explanation));
        }

        results.sort(Comparator.comparingDouble(DecisionResult::getScore).reversed());

        return results;
    }
}

