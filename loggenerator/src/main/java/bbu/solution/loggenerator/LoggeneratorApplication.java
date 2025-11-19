package bbu.solution.loggenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@SpringBootApplication
@RestController
public class LoggeneratorApplication {

	private static final String LOG_FILE = "/shared-logs/test.log";
	private static final Random RANDOM = new Random();
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		SpringApplication.run(LoggeneratorApplication.class, args);
	}

	@PostMapping("/write")
	public String writeMessage(@RequestParam String message) throws IOException {
		try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
			fw.write(message + "\n");
		}
		return "Message written";
	}

	//realistischere Logzeilen
	@PostMapping("/generate")
	public String generateLogs(@RequestParam(defaultValue = "1") int count) throws IOException {
		try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
			for (int i = 0; i < count; i++) {
				String log = generateRandomLog();
				fw.write(log + "\n");
			}
		}
		return count + " log lines generated";
	}

	@GetMapping("/health")
	public String health() {
		return "OK";
	}


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

	private String format(String template) {
		return template.replace("{}", String.valueOf(RANDOM.nextInt(999)));
	}
}
