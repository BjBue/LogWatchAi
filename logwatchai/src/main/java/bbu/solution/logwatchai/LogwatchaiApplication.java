package bbu.solution.logwatchai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the LogWatchAI Spring Boot application.
 * <p>
 * This class bootstraps the entire application, enabling auto-configuration,
 * component scanning, and asynchronous method execution.
 * <p>
 * Key features:
 * <ul>
 *     <li><b>@SpringBootApplication</b> — Activates Spring Boot’s core features such as
 *     component scanning, auto-configuration, and configuration property support.</li>
 *     <li><b>@EnableAsync</b> — Allows methods annotated with {@code @Async} to run in
 *     separate threads, improving responsiveness for tasks such as email sending
 *     or AI processing.</li>
 *     <li><b>main()</b> — Launches the embedded server (e.g., Tomcat) and initializes
 *     the application context.</li>
 * </ul>
 */
@SpringBootApplication
@EnableAsync
public class LogwatchaiApplication {

	/**
	 * Starts the LogWatchAI application.
	 *
	 * @param args command-line arguments passed during startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(LogwatchaiApplication.class, args);
	}
}
