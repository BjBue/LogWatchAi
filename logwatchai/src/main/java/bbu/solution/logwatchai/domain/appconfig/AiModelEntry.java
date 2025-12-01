package bbu.solution.logwatchai.domain.appconfig;

import lombok.Data;

/**
 * Represents a single AI model definition in the configuration.
 * Each entry describes one provider or model variant that I can use.
 */
@Data
public class AiModelEntry {

    /**
     * The logical name of this model entry.
     * I use this to identify models in logs or selection logic.
     */
    private String name;

    /**
     * Determines whether this model is active.
     * Disabled models are ignored at runtime.
     */
    private boolean enabled;

    /**
     * The model identifier (provider-specific).
     * Example: "gpt-4o-mini", "sonar-small".
     */
    private String model;

    /**
     * The API key used to access this model.
     * I assume the caller stores it securely and never logs it.
     */
    private String key;
}
