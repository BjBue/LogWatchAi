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

	private String generateRandomLog() {
		String timestamp = LocalDateTime.now().format(FMT);

		String[] levels = {"INFO", "WARN", "ERROR", "DEBUG"};
		String level = levels[RANDOM.nextInt(levels.length)];

		String[] messages = {
				"User authentication successful",
				"Failed login attempt from IP 10.0.0." + RANDOM.nextInt(255),
				"Disk space at " + (80 + RANDOM.nextInt(20)) + "% capacity",
				"Database connection timeout",
				"Service heartbeat OK",
				"File not found: /etc/config.yaml",
				"User session expired",
				"Scheduled job completed",
				"High memory usage detected",
				"External API responded with 500"
		};

		return timestamp + " [" + level + "] " + messages[RANDOM.nextInt(messages.length)];
	}
}
