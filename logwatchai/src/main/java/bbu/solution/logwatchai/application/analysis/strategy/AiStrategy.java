package bbu.solution.logwatchai.application.analysis.strategy;

/**
 * Strategy contract for AI provider implementations.
 * <p>
 * Each strategy must provide a unique name, an enabled flag, and an analyze method
 * that accepts a prompt and returns the raw textual response from the provider (usually JSON).
 */
public interface AiStrategy {
    /**
     * Unique name of this strategy (should match ai.models[].name from config).
     *
     * @return strategy name
     */
    String getName();
    /**
     * Whether this strategy is currently enabled/usable.
     *
     * @return true if enabled
     */
    boolean isEnabled();
    /**
     * Perform the analysis call against the underlying provider and return raw text result.
     * Implementations should throw exceptions on unrecoverable errors; transient errors
     * (like rate limits) can be retried inside the implementation.
     *
     * @param prompt the prompt text to send to the provider
     * @return raw provider response (string)
     * @throws Exception on unrecoverable failure
     */
    String analyze(String prompt) throws Exception;


}
