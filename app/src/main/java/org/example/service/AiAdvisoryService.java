package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.AiAdvisoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiAdvisoryService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAdvisoryResponse suggest(String category) throws Exception {

        String prompt = buildPrompt(category);
        String requestBody = buildRequestBody(prompt);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error: " + response.statusCode()
                    + " — " + response.body());
        }

        return parseResponse(response.body());
    }

    // ── Prompt ──────────────────────────────────────────────────
    private String buildPrompt(String category) {
        return "You are a decision-making advisor. The user is deciding about: " + category + ".\n\n"
             + "Return a JSON object with exactly this structure:\n"
             + "{\n"
             + "  \"about\": \"one sentence describing this decision domain\",\n"
             + "  \"metrics\": [\n"
             + "    { \"name\": \"metric name\", \"hint\": \"one sentence about why this metric may carry more or less weight depending on the user's priorities\" }\n"
             + "  ],\n"
             + "  \"examples\": [\"example option 1\", \"example option 2\", \"example option 3\"]\n"
             + "}\n\n"
             + "Return only valid JSON. No markdown, no code fences, no explanation, no extra text.";
    }

    // ── Build Gemini request body ────────────────────────────────
    private String buildRequestBody(String prompt) throws Exception {
        String escapedPrompt = objectMapper.writeValueAsString(prompt);
        return "{"
             + "\"contents\": [{"
             + "  \"parts\": [{"
             + "    \"text\": " + escapedPrompt
             + "  }]"
             + "}]"
             + "}";
    }

    // ── Parse Gemini response ────────────────────────────────────
    private AiAdvisoryResponse parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        // Extract text from: candidates[0].content.parts[0].text
        String text = root
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text")
                .asText();

        // Strip any accidental markdown fences
        text = text.strip();
        if (text.startsWith("```")) {
            text = text.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
        }

        // Parse the JSON Gemini returned
        JsonNode json = objectMapper.readTree(text);

        String about = json.path("about").asText();

        List<AiAdvisoryResponse.Metric> metrics = new ArrayList<>();
        for (JsonNode m : json.path("metrics")) {
            metrics.add(new AiAdvisoryResponse.Metric(
                    m.path("name").asText(),
                    m.path("hint").asText()
            ));
        }

        List<String> examples = new ArrayList<>();
        for (JsonNode e : json.path("examples")) {
            examples.add(e.asText());
        }

        return new AiAdvisoryResponse(about, metrics, examples);
    }
}