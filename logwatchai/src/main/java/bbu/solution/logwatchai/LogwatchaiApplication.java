package bbu.solution.logwatchai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LogwatchaiApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogwatchaiApplication.class, args);
	}
}
