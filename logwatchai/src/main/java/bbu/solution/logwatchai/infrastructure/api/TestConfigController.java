package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestConfigController {

    private final AppConfigService configService;

    public TestConfigController(AppConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/config-test")
    public Object test() {
        return configService.getConfig();
    }
}