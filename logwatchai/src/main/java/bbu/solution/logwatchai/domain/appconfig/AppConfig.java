package bbu.solution.logwatchai.domain.appconfig;

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

    public List<String> getWatchPaths() {
        return watchPaths;
    }

    public void setWatchPaths(List<String> watchPaths) {
        this.watchPaths = watchPaths;
    }

    public Map<String, String> getAlertRules() {
        return alertRules;
    }

    public void setAlertRules(Map<String, String> alertRules) {
        this.alertRules = alertRules;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getReportEmail() {
        return reportEmail;
    }

    public void setReportEmail(String reportEmail) {
        this.reportEmail = reportEmail;
    }

    public static class ApiUser {
        private String username;
        private String password;
        private List<String> roles;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
