package bbu.solution.logwatchai.infrastructure.email;

import bbu.solution.logwatchai.domain.alert.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendAlertEmail(Alert alert, String recipient) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(recipient);
            msg.setSubject("LogWatchAI ALERT: " + alert.getSeverity());
            msg.setText("""
                    Alert Triggered
                    ----------------------
                    Severity: %s
                    Message: %s
                    Rules: %s
                    Source-ID: %s
                    LogEntry-ID: %s
                    """.formatted(
                    alert.getSeverity(),
                    alert.getMessage(),
                    String.join(", ", alert.getRuleNames()),
                    alert.getSourceId(),
                    alert.getLogEntryId()
            ));

            mailSender.send(msg);
            log.info("Alert email sent to {}", recipient);
        } catch (Exception e) {
            log.error("Failed to send alert email", e);
        }
    }
}
