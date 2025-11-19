package bbu.solution.logwatchai.infrastructure.config.securityconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private List<AppUser> users = new ArrayList<>();

    public static class AppUser {
        private String username;
        private String password;
        private List<String> roles;

        // getters + setters
    }

    // getters + setters
}