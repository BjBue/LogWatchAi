package bbu.solution.logwatchai.infrastructure.config.appconfig;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigLoader {

    private final ConfigFileService fileService;
    private final String configPath;

    private AppConfig config;

    public ConfigLoader(ConfigFileService fileService,
                        @Value("${app.config-file}") String configPath) {

        this.fileService = fileService;
        this.configPath = configPath;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("Loading config from: " + configPath);
        this.config = fileService.loadConfig(configPath);
    }

    public AppConfig getConfig() {
        return config;
    }
}
