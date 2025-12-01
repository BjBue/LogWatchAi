package bbu.solution.logwatchai.domain.appconfig;

import lombok.Data;

import java.util.List;

/**
 * Represents the security configuration section. I use it to load static users,
 * predefined roles, or basic authentication rules depending on environment.
 */
@Data
public class ConfigSecurity {

    /**
     * Contains the list of predefined user entries.
     * If empty, no static users are loaded.
     */
    private List<UserEntry> users;
}