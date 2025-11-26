package bbu.solution.logwatchai.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up the application's mail sender.
 * This configuration is tailored for a local MailHog instance.
 */
@Configuration
public class MailConfig {

    /**
     * Creates and configures a {@link JavaMailSender} instance using MailHog.
     * MailHog acts as a local SMTP server for development and testing,
     * capturing outgoing emails without sending them externally.
     *
     * @return a fully configured {@link JavaMailSender} instance
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost("mailhog");
        sender.setPort(1025); // MailHog SMTP port

        sender.setUsername("");
        sender.setPassword("");

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "false");

        return sender;
    }
}
