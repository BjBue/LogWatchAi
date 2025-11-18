package bbu.solution.logwatchai.domain.logwatcher;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogEvent {
    private String filePath;
    private String line;
}
