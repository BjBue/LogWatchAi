package bbu.solution.logwatchai.domain.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a system user stored in the database.
 * <p>
 * This entity holds authentication and authorization information,
 * including credentials, role, status, and timestamps. It is used by
 * the security layer to perform login checks and permission evaluations.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "username"),
        indexes = {
                @Index(name = "idx_users_created_at", columnList = "created_at"),
                @Index(name = "idx_users_role", columnList = "role")
        }
)
public class User {

    /**
     * Primary key for the user, stored as a 16-byte binary UUID.
     */
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    /**
     * Unique username used for authentication.
     */
    @Column(nullable = false, length = 100, unique = true)
    private String username;

    /**
     * Secure hash of the user's password.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Role defining user permissions within the system.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /**
     * Automatically tracked timestamp of when the user was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp of the last successful login event.
     */
    @Column(name = "last_login")
    private Instant lastLogin;

    /**
     * Whether the account is enabled and allowed to log in.
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Version field for optimistic locking.
     */
    @Version
    private Long version;

    /**
     * Default JPA constructor.
     */
    public User() {}

    /**
     * Creates a new user with the given username, password hash, and role.
     *
     * @param username     the username
     * @param passwordHash the hashed password
     * @param role         the assigned role
     */
    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // --- Business Methods ---

    /**
     * Updates the lastLogin timestamp to the current time.
     * <p>
     * Should be called after a successful authentication.
     */
    public void updateLastLogin() {
        this.lastLogin = Instant.now();
    }

    // --- Getters & Setters ---

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastLogin() { return lastLogin; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
