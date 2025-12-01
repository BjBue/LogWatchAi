package bbu.solution.logwatchai.application.analysis.strategy;

/**
 * Stub strategy for Perplexity (or another provider).
 * This is only to demonstrate multi-provider factory support.
 */
public class PerplexityStrategy implements AiStrategy {

    private final String name;
    private final String model;
    private final String key;

    public PerplexityStrategy(String name, String model, String key) {
        this.name = name;
        this.model = model;
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return key != null && !key.isBlank();
    }

    @Override
    public String analyze(String prompt) throws Exception {
        // Later: call real Perplexity API
        return """
        {
            "severity": "INFO",
            "category": "stub",
            "summarizedIssue": "not implemented",
            "likelyCause": "stub",
            "recommendation": "implement provider logic",
            "anomalyScore": 0.0
        }
        """;
    }
}
