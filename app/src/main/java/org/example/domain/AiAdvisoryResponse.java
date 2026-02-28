package org.example.domain;

import java.util.List;

public class AiAdvisoryResponse {

    private String about;
    private List<Metric> metrics;
    private List<String> examples;

    public AiAdvisoryResponse() {}

    public AiAdvisoryResponse(String about, List<Metric> metrics, List<String> examples) {
        this.about   = about;
        this.metrics = metrics;
        this.examples = examples;
    }

    public String getAbout()              { return about; }
    public void setAbout(String about)    { this.about = about; }

    public List<Metric> getMetrics()               { return metrics; }
    public void setMetrics(List<Metric> metrics)   { this.metrics = metrics; }

    public List<String> getExamples()              { return examples; }
    public void setExamples(List<String> examples) { this.examples = examples; }

    // ── Nested class representing one suggested metric ──────────
    public static class Metric {
        private String name;
        private String hint;

        public Metric() {}

        public Metric(String name, String hint) {
            this.name = name;
            this.hint = hint;
        }

        public String getName()          { return name; }
        public void setName(String name) { this.name = name; }

        public String getHint()          { return hint; }
        public void setHint(String hint) { this.hint = hint; }
    }
}