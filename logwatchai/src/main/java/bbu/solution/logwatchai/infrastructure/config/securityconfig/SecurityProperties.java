package bbu.solution.logwatchai.infrastructure.config.securityconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * Loads and holds security-related configuration from the application's properties or YAML file.
 * <p>
 * This class is mapped to the property namespace <code>security.*</code>
 * using Spring Boot’s {@link ConfigurationProperties} mechanism.
 * It allows defining users, passwords, and roles directly in <code>application.yml</code>
 * or an external configuration file.
 *
 * <h2>Example YAML structure</h2>
 * <pre>
 * security:
 *   users:
 *     - username: "admin"
 *       password: "secret"
 *       roles:
 *         - "ADMIN"
 *     - username: "reader"
 *       password: "reader123"
 *       roles:
 *         - "USER"
 * </pre>
 *
 * <h2>Purpose</h2>
 * This configuration class enables:
 * <ul>
 *     <li>Defining static application users through YAML</li>
 *     <li>Loading user credentials before application startup</li>
 *     <li>Injecting user definitions into custom initializers or security components</li>
 * </ul>
 *
 * <h2>Where it is used</h2>
 * Typically consumed by initialization logic such as:
 * <ul>
 *     <li>{@code AppConfigUserInitializer}</li>
 *     <li>Security setup or custom user creation logic</li>
 * </ul>
 */
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * A list of user definitions loaded from security.users[]
     * in the application properties.
     */
    private List<AppUser> users = new ArrayList<>();

    /**
     * Represents a user entry from the YAML file.
     * Contains username, raw password, and assigned roles.
     *
     * This is used to initialize users in the system at startup.
     */
    public static class AppUser {

        /** The username defined in YAML. */
        private String username;

        /** The cleartext password from YAML (usually encoded later). */
        private String password;

        /** A list of role names (e.g., ADMIN, USER). */
        private List<String> roles;

        // --- Getters & Setters ---
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

    // --- Getters & Setters ---
    public List<AppUser> getUsers() {
        return users;
    }

    public void setUsers(List<AppUser> users) {
        this.users = users;
    }
}
