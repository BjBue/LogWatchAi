package bbu.solution.logwatchai.domain.user;

/**
 * Represents the available user roles within the system.
 * <p>
 * Roles define access permissions and control which operations
 * a user is authorized to perform.
 */
public enum Role {

    /** Standard user with limited permissions. */
    USER,

    /** Administrator with full system access. */
    ADMIN,

    /** Moderator with elevated but not full administrative permissions. */
    MODERATOR
}
