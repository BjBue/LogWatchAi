package bbu.solution.logwatchai.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration class enabling asynchronous execution for email-related tasks.
 * Defines a dedicated executor to process email operations without blocking the main application flow.
 */
@Configuration
@EnableAsync
public class EmailAsyncConfig {

    /**
     * Creates a fixed thread pool executor specifically for email handling tasks.
     * A pool size of 2 is sufficient for typical email workloads.
     *
     * @return an {@link Executor} backed by a thread pool of size 2
     */
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        return Executors.newFixedThreadPool(2); // reicht völlig
    }
}
