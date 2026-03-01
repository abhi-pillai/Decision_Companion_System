package org.example;

import org.example.domain.Criterion;
import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import org.example.domain.Option;
import org.example.engine.TopsisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TopsisEngineTest {

    // ── The engine under test ──────────────────────────────────────
    private TopsisEngine engine;

    // ── Shared test data ───────────────────────────────────────────
    private List<Criterion> criteria;
    private List<Option>    options;

    @BeforeEach
    void setUp() {
        engine = new TopsisEngine();

        // Same scenario as WeightedSumEngineTest for consistency
        criteria = List.of(
            new Criterion("Performance", 0.40),
            new Criterion("Battery",     0.30),
            new Criterion("Price",       0.30)
        );

        options = List.of(
            new Option("Dell XPS",    Map.of("Performance", 9.0, "Battery", 6.0, "Price", 7.0)),
            new Option("MacBook Air", Map.of("Performance", 8.0, "Battery", 9.0, "Price", 5.0)),
            new Option("Lenovo",      Map.of("Performance", 6.0, "Battery", 7.0, "Price", 9.0))
        );
    }

    // ── Test 1 ─────────────────────────────────────────────────────
    // The engine must return one result per option — no options dropped
    @Test
    void shouldReturnOneResultPerOption() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        assertEquals(3, results.size(),
            "Expected 3 results — one per option");
    }

    // ── Test 2 ─────────────────────────────────────────────────────
    // All TOPSIS closeness ratios must be between 0 and 1 (inclusive)
    // C = d⁻ / (d⁺ + d⁻) — mathematically bounded to [0, 1]
    @Test
    void shouldProduceClosenessRatiosBetweenZeroAndOne() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        for (DecisionResult result : results) {
            assertTrue(result.getScore() >= 0.0 && result.getScore() <= 1.0,
                "Closeness ratio for " + result.getOptionName()
                + " should be between 0 and 1, but was " + result.getScore());
        }
    }

    // ── Test 3 ─────────────────────────────────────────────────────
    // Results must be sorted descending by closeness ratio
    @Test
    void shouldReturnResultsSortedByClosenessDescending() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        assertTrue(results.get(0).getScore() >= results.get(1).getScore(),
            "Rank 1 closeness ratio should be >= rank 2");
        assertTrue(results.get(1).getScore() >= results.get(2).getScore(),
            "Rank 2 closeness ratio should be >= rank 3");
    }

    // ── Test 4 ─────────────────────────────────────────────────────
    // The ideal option (scores all 10) should have closeness ratio = 1.0
    // because d⁺ = 0, so C = d⁻ / (0 + d⁻) = 1.0
    @Test
    void shouldGiveClosenessOfOneToIdealOption() {
        List<Option> testOptions = List.of(
            new Option("Perfect",  Map.of("Performance", 10.0, "Battery", 10.0, "Price", 10.0)),
            new Option("Mediocre", Map.of("Performance", 5.0,  "Battery", 5.0,  "Price", 5.0))
        );

        DecisionRequest request = new DecisionRequest("Test", testOptions, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        assertEquals(1.0, results.get(0).getScore(), 0.0001,
            "The option with all-max scores should have closeness ratio of 1.0");
    }

    // ── Test 5 ─────────────────────────────────────────────────────
    // The worst option (scores all 1) should have closeness ratio = 0.0
    // because d⁻ = 0, so C = 0 / (d⁺ + 0) = 0.0
    @Test
    void shouldGiveClosenessOfZeroToWorstOption() {
        List<Option> testOptions = List.of(
            new Option("Perfect", Map.of("Performance", 10.0, "Battery", 10.0, "Price", 10.0)),
            new Option("Worst",   Map.of("Performance", 1.0,  "Battery", 1.0,  "Price", 1.0))
        );

        DecisionRequest request = new DecisionRequest("Test", testOptions, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        // Worst option will be ranked last
        DecisionResult worst = results.get(results.size() - 1);
        assertEquals(0.0, worst.getScore(), 0.0001,
            "The option with all-min scores should have closeness ratio of 0.0");
    }

    // ── Test 6 ─────────────────────────────────────────────────────
    // Every result must have a non-empty explanation string
    @Test
    void shouldAttachExplanationToEachResult() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        for (DecisionResult result : results) {
            assertNotNull(result.getExplanation(),
                "Explanation should not be null for " + result.getOptionName());
            assertFalse(result.getExplanation().isBlank(),
                "Explanation should not be blank for " + result.getOptionName());
        }
    }
}