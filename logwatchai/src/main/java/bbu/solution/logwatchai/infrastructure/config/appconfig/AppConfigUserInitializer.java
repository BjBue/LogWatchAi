package bbu.solution.logwatchai.infrastructure.config.appconfig;

import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import bbu.solution.logwatchai.domain.user.Role;
import bbu.solution.logwatchai.domain.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Initializes application users defined in the YAML configuration file.
 * <p>
 * When the application starts, this component loads the configured users
 * from AppConfig and ensures they exist in the system. Missing users are
 * automatically created with the defined username, password, and role.
 */
@Component
@RequiredArgsConstructor
public class AppConfigUserInitializer {

    private final AppConfigService configService;
    private final UserService userService;

    /**
     * Loads users from the YAML configuration and creates any that do not
     * already exist in the system. Existing users are skipped.
     * <p>
     * This method runs automatically at startup due to {@link PostConstruct}.
     */
    @PostConstruct
    public void initUser() {

        AppConfig cfg = configService.getConfig();

        if (cfg.getSecurity() == null || cfg.getSecurity().getUsers() == null) {
            System.out.println("No YAML users found");
            return;
        }

        for (AppConfig.UserEntry entry : cfg.getSecurity().getUsers()) {

            if (!userService.existsByUsername(entry.getUsername())) {

                System.out.println("Creating YAML user: " + entry.getUsername());

                Role role = Role.valueOf(entry.getRoles().get(0));

                userService.createUser(
                        entry.getUsername(),
                        entry.getPassword(),
                        role
                );

            } else {
                System.out.println("User already exists (YAML): " + entry.getUsername());
            }
        }
    }
}
