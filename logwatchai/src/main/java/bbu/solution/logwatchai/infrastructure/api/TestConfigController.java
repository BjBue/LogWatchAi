package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller used to verify that application configuration values
 * can be retrieved correctly through the AppConfigService.
 */
@RestController
public class TestConfigController {

    private final AppConfigService configService;

    /**
     * Creates a new TestConfigController.
     *
     * @param configService the service responsible for providing application configuration
     */
    public TestConfigController(AppConfigService configService) {
        this.configService = configService;
    }

    /**
     * Returns the currently loaded application configuration.
     *
     * @return the configuration object provided by AppConfigService
     */
    @GetMapping("/config-test")
    public Object test() {
        return configService.getConfig();
    }
}
