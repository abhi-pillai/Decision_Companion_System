package org.example.controller;

import org.example.domain.AiAdvisoryResponse;
import org.example.service.AiAdvisoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/advisory")
@CrossOrigin(origins = "*")
public class AiAdvisoryController {

    private final AiAdvisoryService aiAdvisoryService;

    public AiAdvisoryController(AiAdvisoryService aiAdvisoryService) {
        this.aiAdvisoryService = aiAdvisoryService;
    }

    @PostMapping("/suggest")
    public ResponseEntity<?> suggest(@RequestBody Map<String, String> body) {
        String category = body.get("category");

        if (category == null || category.isBlank()) {
            return ResponseEntity.badRequest().body("Category is required.");
        }

        try {
            AiAdvisoryResponse response = aiAdvisoryService.suggest(category.trim());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get suggestions: " + e.getMessage());
        }
    }
}