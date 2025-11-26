package bbu.solution.logwatchai.infrastructure.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class providing the {@link OpenAiService} bean used for
 * communication with the OpenAI API.
 * <p>
 * This bean is created once at application startup and reused throughout
 * the application whenever AI analysis is required.
 */
@Configuration
public class OpenAiClientConfig {

    /**
     * Creates and configures the {@link OpenAiService} instance.
     *
     * @param apiKey the API key used to authenticate requests to the OpenAI API,
     *               injected from the property {@code ai.api-key}
     * @return a configured {@link OpenAiService} client
     */
    @Bean
    public OpenAiService openAiService(@Value("${ai.api-key}") String apiKey) {
        // A 60-second timeout is used to accommodate longer AI requests.
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
}
