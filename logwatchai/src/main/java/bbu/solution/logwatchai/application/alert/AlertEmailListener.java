package bbu.solution.logwatchai.application.alert;

import bbu.solution.logwatchai.domain.alert.events.AlertCreatedEvent;
import bbu.solution.logwatchai.infrastructure.config.appconfig.ConfigLoader;
import bbu.solution.logwatchai.infrastructure.email.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * Application-layer listener that reacts to domain events after the Alert
 * has been successfully persisted. Only AFTER the commit is completed an
 * email is sent, ensuring "email but no alert in DB" can never occur again.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEmailListener {

    private final EmailService emailService;
    private final ConfigLoader configLoader;

    /**
     * Sends an alert e-mail only after the transaction was committed.
     */
    //@EventListener
    //@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAlertCreated(AlertCreatedEvent event) {

        var alert = event.alert();
        String recipient = configLoader.getConfig().getReportEmail();

        if (recipient == null || recipient.isBlank()) {
            log.debug("Skipping alert email: no recipient configured");
            return;
        }

        try {
            emailService.sendAlertEmail(alert, recipient);
            log.info("Sent alert e-mail for alert {} to {}", alert.getId(), recipient);
        } catch (Exception ex) {
            log.error("Failed to send alert email for alert {}: {}", alert.getId(), ex.getMessage(), ex);
        }
    }
}
