package bbu.solution.logwatchai.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class enabling asynchronous method execution within the application.
 * Provides executor definitions used to run tasks asynchronously.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Creates a fixed thread pool executor used for AI-related asynchronous operations.
     *
     * @return an {@link Executor} instance backed by a thread pool of size 4
     */
    @Bean(name = "aiExecutor")
    public Executor aiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(9);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AI-Executor-");
        executor.initialize();
        return executor;
    }
}
