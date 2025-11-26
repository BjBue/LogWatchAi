package bbu.solution.logwatchai.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for customizing Jackson's ObjectMapper.
 * Registers additional modules to ensure proper serialization and deserialization
 * of Java 8+ date and time API classes.
 */
@Configuration
public class JacksonConfig {

    private final ObjectMapper objectMapper;

    /**
     * Constructs the configuration with the application's primary {@link ObjectMapper}.
     *
     * @param objectMapper the ObjectMapper managed by Spring
     */
    public JacksonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Registers supplemental Jackson modules after the bean has been constructed.
     * Specifically, this adds support for Java Time API classes such as LocalDate and LocalDateTime.
     */
    @PostConstruct
    public void registerModules() {
        objectMapper.registerModule(new JavaTimeModule());
    }
}
