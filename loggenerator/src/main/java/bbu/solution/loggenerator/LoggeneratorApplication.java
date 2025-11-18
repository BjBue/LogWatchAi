package bbu.solution.loggenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
@RestController
public class LoggeneratorApplication {

	private static final String LOG_FILE = "/shared-logs/test.log";

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

	@GetMapping("/health")
	public String health() {
		return "OK";
	}
}
