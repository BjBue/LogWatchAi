package bbu.solution.logwatchai.infrastructure.config.appconfig;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ConfigFileService {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public AppConfig loadConfig(String path) {
        try {
            return mapper.readValue(new File(path), AppConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config from " + path, e);
        }
    }
}
