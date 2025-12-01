package bbu.solution.logwatchai.domain.appconfig;

import lombok.Data;
import java.util.List;

/**
 * Represents the AI configuration block.
 * I use this to load all configured AI models from the configuration file.
 */
@Data
public class AiConfig {

    /**
     * Contains all defined AI model entries.
     * If empty or missing, no AI-based analysis is available.
     */
    private List<AiModelEntry> models;
}
