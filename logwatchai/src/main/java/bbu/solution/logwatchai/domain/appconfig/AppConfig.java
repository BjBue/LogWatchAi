package bbu.solution.logwatchai.domain.appconfig;

import bbu.solution.logwatchai.application.rules.AlertingConfig;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AppConfig {

    private List<String> watchPaths;
    private AlertingConfig alerting;
    private boolean aiEnabled;
    private String aiModel;
    private String reportEmail;

    private Security security;

    @Data
    public static class Security {
        private List<UserEntry> users;
    }

    @Data
    public static class UserEntry {
        private String username;
        private String password;
        private List<String> roles;
    }
}
