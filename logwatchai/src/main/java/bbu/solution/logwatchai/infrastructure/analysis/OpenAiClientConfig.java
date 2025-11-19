package bbu.solution.logwatchai.infrastructure.analysis;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiClientConfig {

    @Bean(destroyMethod = "shutdown")
    public OpenAiService openAiService(@Value("${ai.api-key}") String apiKey) {
        // Timeout optional
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
}