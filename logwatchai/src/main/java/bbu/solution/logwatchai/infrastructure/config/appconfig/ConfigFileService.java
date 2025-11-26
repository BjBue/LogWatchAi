package bbu.solution.logwatchai.infrastructure.config.appconfig;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Service responsible for loading the application configuration
 * from an external YAML file into an {@link AppConfig} object.
 */
@Component
public class ConfigFileService {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * Loads and parses the YAML configuration file located at the given path.
     *
     * @param path the filesystem path to the YAML configuration file
     * @return the parsed {@link AppConfig} instance
     * @throws RuntimeException if the configuration file cannot be read or parsed
     */
    public AppConfig loadConfig(String path) {
        try {
            return mapper.readValue(new File(path), AppConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config from " + path, e);
        }
    }
}
