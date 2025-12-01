package bbu.solution.logwatchai.domain.appconfig;

import lombok.Data;

import java.util.List;

/**
 * Represents a user definition from the configuration. I use this object to
 * construct initial in-memory users or to seed the persistence layer.
 */
@Data
public class UserEntry {

    /**
     * The username of the configured user.
     */
    private String username;

    /**
     * The raw password for the configured user. I assume the caller will hash
     * or secure this value before persistence or authentication use.
     */
    private String password;

    /**
     * The list of roles assigned to the user. I treat unspecified roles as empty.
     */
    private List<String> roles;
}