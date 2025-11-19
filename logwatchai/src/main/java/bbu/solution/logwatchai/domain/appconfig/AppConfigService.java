package bbu.solution.logwatchai.domain.appconfig;

import bbu.solution.logwatchai.infrastructure.config.appconfig.ConfigLoader;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final AppConfig config;

    public AppConfigService(ConfigLoader loader) {
        this.config = loader.getConfig();
    }

    public AppConfig getConfig() {
        return config;
    }
}
