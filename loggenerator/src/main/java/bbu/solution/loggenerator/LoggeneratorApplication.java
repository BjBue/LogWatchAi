package bbu.solution.loggenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Main application class for the Log Generator service.
 * <p>
 * This service simulates log-producing systems by writing plain text log entries
 * to a shared log file. It can write custom messages or generate realistic,
 * randomized log lines used for testing LogWatchAI ingestion and alerting.
 * <p>
 * The application also exposes simple REST endpoints for generating logs,
 * writing custom messages, and performing health checks.
 */
@SpringBootApplication
@RestController
public class LoggeneratorApplication {

	/** Path to the shared log file where generated logs will be appended. */
	private static final String LOG_FILE = "/shared-logs/test.log";

	/** Random generator used for producing pseudo-random log messages. */
	private static final Random RANDOM = new Random();

	/**
	 * Starts the Log Generator application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(LoggeneratorApplication.class, args);
	}

	/**
	 * Writes a custom log message directly into the shared log file.
	 *
	 * @param message the text to append as a log entry
	 * @return confirmation message
	 * @throws IOException if writing to the log file fails
	 */
	@PostMapping("/write")
	public String writeMessage(@RequestParam String message) throws IOException {
		try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
			fw.write(message + "\n");
		}
		return "Message written";
	}

	/**
	 * Generates a specified number of synthetic log entries using realistic formats.
	 *
	 * @param count how many log lines to generate (default: 1)
	 * @return confirmation with number of generated lines
	 * @throws IOException if writing to the log file fails
	 */
	@PostMapping("/generate")
	public String generateLogs(@RequestParam(defaultValue = "1") int count) throws IOException {
		try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
			for (int i = 0; i < count; i++) {
				fw.write(generateRandomLog() + "\n");
			}
		}
		return count + " log lines generated";
	}

	/**
	 * Simple health endpoint to verify service availability.
	 *
	 * @return always returns "OK"
	 */
	@GetMapping("/health")
	public String health() {
		return "OK";
	}

	// ---------------------
	// Log generation internals
	// ---------------------

	private static final String[] SERVICES = {
			"AuthService", "Database", "ApiGateway", "Security",
			"UserService", "JobScheduler", "PaymentClient",
			"Network", "JvmMonitor"
	};

	private static final String[] INFO_MESSAGES = {
			"User '{}' authenticated successfully (session={})",
			"GET /api/users/{} 200 OK in {}ms trace={}",
			"DailyCleanup completed in {}ms (deleted={})",
			"Service heartbeat OK"
	};

	private static final String[] WARN_MESSAGES = {
			"Disk usage at {}% on /dev/sda1 (threshold=90%)",
			"High latency detected between node A and B ({}ms)",
			"Failed login attempt for user '{}' from IP {}"
	};

	private static final String[] ERROR_MESSAGES = {
			"Connection pool exhausted (active={}, max={})",
			"External API /charge returned 503 Service Unavailable retry={}",
			"NullPointerException while processing user={}",
			"OutOfMemoryWarning: Heap usage {}% ({}GB / {}GB)"
	};

	/**
	 * Generates a single randomized log entry including timestamp, severity,
	 * service name, and a formatted message.
	 *
	 * @return generated log entry string
	 */
	private String generateRandomLog() {
		String timestamp = java.time.Instant.now().toString();
		String service = SERVICES[RANDOM.nextInt(SERVICES.length)];

		String level;
		String message;

		int pick = RANDOM.nextInt(10);

		if (pick < 4) {
			level = "INFO";
			message = format(INFO_MESSAGES[RANDOM.nextInt(INFO_MESSAGES.length)]);
		} else if (pick < 7) {
			level = "WARN";
			message = format(WARN_MESSAGES[RANDOM.nextInt(WARN_MESSAGES.length)]);
		} else {
			level = "ERROR";
			message = format(ERROR_MESSAGES[RANDOM.nextInt(ERROR_MESSAGES.length)]);
		}

		return String.format("%s %s [%s] %s", timestamp, level, service, message);
	}

	/**
	 * Formats a log message template by replacing all occurrences of `{}` with
	 * random numeric values. Useful for simulating dynamic log content.
	 *
	 * @param template message template containing placeholders
	 * @return formatted message
	 */
	private String format(String template) {
		return template.replace("{}", String.valueOf(RANDOM.nextInt(999)));
	}
}
