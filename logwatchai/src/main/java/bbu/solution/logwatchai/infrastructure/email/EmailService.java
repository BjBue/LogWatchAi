package bbu.solution.logwatchai.infrastructure.email;

import bbu.solution.logwatchai.domain.alert.Alert;

public interface EmailService {
    void sendAlertEmail(Alert alert, String recipient);
}