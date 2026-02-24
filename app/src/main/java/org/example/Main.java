package org.example;

import org.example.domain.Criterion;
import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import org.example.domain.Option;
import org.example.engine.DecisionEngine;
import org.example.engine.TopsisEngine;
import org.example.engine.WeightedSumEngine;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // --- CRITERIA (name, weight) --- weights must sum to 1.0
        List<Criterion> criteria = List.of(
            new Criterion("Performance", 0.40),
            new Criterion("Battery",     0.30),
            new Criterion("Price",       0.30)
        );

        // --- OPTIONS (name, criterion scores out of 10) ---
        List<Option> options = List.of(
            new Option("Dell XPS", Map.of(
                "Performance", 9.0,
                "Battery",     6.0,
                "Price",       5.0
            )),
            new Option("MacBook Air", Map.of(
                "Performance", 8.0,
                "Battery",     9.0,
                "Price",       4.0
            )),
            new Option("Lenovo IdeaPad", Map.of(
                "Performance", 6.0,
                "Battery",     7.0,
                "Price",       9.0
            ))
        );

        // --- RUN ENGINE ---
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        DecisionEngine engine = new WeightedSumEngine();
        List<DecisionResult> results = engine.evaluate(request);
        DecisionEngine engine2= new TopsisEngine();
        List<DecisionResult> results2 = engine2.evaluate(request);


        // --- PRINT RESULTS ---
        System.out.println("\n========== DECISION COMPANION SYSTEM ==========");
        System.out.println("Decision: Which laptop should I buy?\n");

        System.out.println("--- WEIGHTED SUM RESULTS ---");
        int rank = 1;
        for (DecisionResult result : results) {
            System.out.println("Rank #" + rank + " | Option: " + result.getOptionName() +
            " | Score: " + String.format("%.4f", result.getScore()) +
            " | " + result.getExplanation());
            rank++;
        }

        System.out.println("\n--- TOPSIS RESULTS ---");
        rank = 1;
        for (DecisionResult result : results2) {
            System.out.println("Rank #" + rank + " | Option: " + result.getOptionName() +
            " | Score: " + String.format("%.4f", result.getScore()) +
            " | " + result.getExplanation());
            rank++;
        }

        System.out.println("\n>> Recommendation: " + results.get(0).getOptionName()+
        " (Weighted Sum) and " + results2.get(0).getOptionName() + " (TOPSIS)");
        System.out.println("================================================\n");
    }
}