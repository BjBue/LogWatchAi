package bbu.solution.logwatchai.infrastructure.config.appconfig;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Loads the application configuration from a YAML file during application startup.
 * Acts as a central holder for the parsed {@link AppConfig}.
 */
@Component
public class ConfigLoader {

    private final ConfigFileService fileService;
    private final String configPath;

    private AppConfig config;

    /**
     * Constructs a new {@code ConfigLoader}.
     *
     * @param fileService the service responsible for reading YAML configuration files
     * @param configPath the path to the configuration file defined via application properties
     */
    public ConfigLoader(ConfigFileService fileService,
                        @Value("${app.config-file}") String configPath) {

        this.fileService = fileService;
        this.configPath = configPath;
    }

    /**
     * Invoked once during application startup.
     * Loads the configuration file and stores its parsed result.
     */
    @PostConstruct
    public void initialize() {
        System.out.println("Loading config from: " + configPath);
        this.config = fileService.loadConfig(configPath);
    }

    /**
     * Returns the loaded application configuration.
     *
     * @return the parsed {@link AppConfig} instance
     */
    public AppConfig getConfig() {
        return config;
    }
}
