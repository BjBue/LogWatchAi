package bbu.solution.logwatchai.infrastructure.email;

import bbu.solution.logwatchai.domain.alert.Alert;

/**
 * Defines the contract for sending alert-related emails.
 * Implementations of this interface provide mechanisms to deliver
 * alert notifications to specified recipients.
 */
public interface EmailService {

    /**
     * Sends an alert notification email to the given recipient.
     *
     * @param alert     the alert information that should be included in the email
     * @param recipient the target email address that will receive the alert
     */
    void sendAlertEmail(Alert alert, String recipient);
}
