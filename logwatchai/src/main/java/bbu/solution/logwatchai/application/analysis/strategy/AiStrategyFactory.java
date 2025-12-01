package bbu.solution.logwatchai.application.analysis.strategy;

import bbu.solution.logwatchai.domain.appconfig.AiConfig;
import bbu.solution.logwatchai.domain.appconfig.AiModelEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for building AI provider strategies based on the model "name".
 *
 * Example:
 * - name: "openai"     -> OpenAiStrategy
 * - name: "perplexity" -> PerplexityStrategy (stub)
 */
public final class AiStrategyFactory {

    private AiStrategyFactory() {}

    public static Map<String, AiStrategy> buildStrategies(AiConfig config) {

        Map<String, AiStrategy> strategies = new HashMap<>();

        if (config == null || config.getModels() == null) {
            return strategies;
        }

        for (AiModelEntry entry : config.getModels()) {
            if (entry == null || !entry.isEnabled()) {
                continue;
            }

            String name = entry.getName().toLowerCase();
            AiStrategy strategy = switch (name) {

                case "openai", "chatgpt" -> new OpenAiStrategy(
                        entry.getName(),
                        entry.getModel(),
                        entry.getKey()
                );

                case "perplexity" -> new PerplexityStrategy(
                        entry.getName(),
                        entry.getModel(),
                        entry.getKey()
                );

                // Extend here for new providers:
                // case "anthropic" -> new AnthropicStrategy(...);
                // case "local"     -> new LocalAiStrategy(...);

                default -> {
                    System.err.println("Unknown AI strategy name: " + name);
                    yield null;
                }
            };

            if (strategy != null && strategy.isEnabled()) {
                strategies.put(entry.getName(), strategy);
            }
        }

        return strategies;
    }
}
