package org.example.controller;

import org.example.domain.CombinedDecisionResponse;
import org.example.domain.DecisionRequest;
import org.example.service.DecisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decision")
@CrossOrigin(origins = "*")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody DecisionRequest request) {
        try {
            CombinedDecisionResponse response = decisionService.evaluate(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}