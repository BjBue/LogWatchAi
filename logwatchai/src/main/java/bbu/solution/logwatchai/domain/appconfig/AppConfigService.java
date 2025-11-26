package bbu.solution.logwatchai.domain.appconfig;

import bbu.solution.logwatchai.infrastructure.config.appconfig.ConfigLoader;
import org.springframework.stereotype.Service;

/**
 * Provides application-wide configuration access.
 * I load the configuration once during construction and expose it to the
 * rest of the application. The configuration is treated as immutable at runtime.
 */
@Service
public class AppConfigService {

    /**
     * Holds the fully loaded application configuration.
     * I treat this as a final, read-only snapshot.
     */
    private final AppConfig config;

    /**
     * Constructs the service by receiving a {@link ConfigLoader},
     * from which I immediately load and cache the full configuration.
     *
     * @param loader the component responsible for parsing the configuration file
     */
    public AppConfigService(ConfigLoader loader) {
        this.config = loader.getConfig();
    }

    /**
     * Returns the application configuration that I loaded during initialization.
     * I never reload or mutate the configuration; this method always returns the same instance.
     *
     * @return the immutable {@link AppConfig}
     */
    public AppConfig getConfig() {
        return config;
    }
}
