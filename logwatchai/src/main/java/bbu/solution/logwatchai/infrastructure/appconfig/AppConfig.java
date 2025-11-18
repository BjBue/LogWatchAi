package bbu.solution.logwatchai.infrastructure.appconfig;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppConfig {

    private List<String> watchPaths;          // Liste von Logfiles
    private Map<String, String> alertRules;   // z.B. error: ALERT
    private boolean aiEnabled;                // AI aktiv?
    private String aiModel;                   // z.B. gpt-4.1
    private String reportEmail;               // Zieladresse
}
