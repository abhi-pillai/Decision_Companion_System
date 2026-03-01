package org.example;

import org.example.domain.Criterion;
import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import org.example.domain.Option;
import org.example.engine.WeightedSumEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeightedSumEngineTest {

    // ── The engine under test ──────────────────────────────────────
    private WeightedSumEngine engine;

    // ── Shared test data ───────────────────────────────────────────
    // Weights are in decimal (not percentage) because DecisionService
    // normalizes from % to decimal before passing to the engine.
    private List<Criterion> criteria;
    private List<Option>    options;

    @BeforeEach
    void setUp() {
        engine = new WeightedSumEngine();

        // Criteria: Performance 40%, Battery 30%, Price 30%
        criteria = List.of(
            new Criterion("Performance", 0.40),
            new Criterion("Battery",     0.30),
            new Criterion("Price",       0.30)
        );

        // Options: same laptop scenario used in Postman verification (Day 11)
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
    // Dell XPS should rank first based on known hand-calculated scores:
    //   Dell XPS    = 0.40×9 + 0.30×6 + 0.30×7 = 3.6 + 1.8 + 2.1 = 7.5
    //   MacBook Air = 0.40×8 + 0.30×9 + 0.30×5 = 3.2 + 2.7 + 1.5 = 7.4
    //   Lenovo      = 0.40×6 + 0.30×7 + 0.30×9 = 2.4 + 2.1 + 2.7 = 7.2
    @Test
    void shouldRankDellXpsFirst() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        assertEquals("Dell XPS", results.get(0).getOptionName(),
            "Dell XPS should rank first with the highest weighted sum");
    }

    // ── Test 3 ─────────────────────────────────────────────────────
    // Scores must be sorted descending — rank 1 score > rank 2 score > rank 3 score
    @Test
    void shouldReturnResultsSortedByScoreDescending() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        assertTrue(results.get(0).getScore() >= results.get(1).getScore(),
            "Rank 1 score should be >= rank 2 score");
        assertTrue(results.get(1).getScore() >= results.get(2).getScore(),
            "Rank 2 score should be >= rank 3 score");
    }

    // ── Test 4 ─────────────────────────────────────────────────────
    // Dell XPS score should be exactly 7.5 (hand-calculated above)
    @Test
    void shouldCalculateCorrectScoreForDellXps() {
        DecisionRequest request = new DecisionRequest("Laptop Selection", options, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        double dellScore = results.get(0).getScore();
        assertEquals(7.5, dellScore, 0.0001,
            "Dell XPS WSM score should be exactly 7.5");
    }

    // ── Test 5 ─────────────────────────────────────────────────────
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

    // ── Test 6 ─────────────────────────────────────────────────────
    // With equal scores across all criteria, all options should tie
    @Test
    void shouldProduceTiedScoresWhenAllOptionsAreIdentical() {
        List<Option> tiedOptions = List.of(
            new Option("Option A", Map.of("Performance", 7.0, "Battery", 7.0, "Price", 7.0)),
            new Option("Option B", Map.of("Performance", 7.0, "Battery", 7.0, "Price", 7.0))
        );

        DecisionRequest request = new DecisionRequest("Test", tiedOptions, criteria);
        List<DecisionResult> results = engine.evaluate(request);

        assertEquals(results.get(0).getScore(), results.get(1).getScore(), 0.0001,
            "Identical options should produce identical scores");
    }
}