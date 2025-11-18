package bbu.solution.logwatchai.domain.user;

import jakarta.persistence.*;
import org.hibernate.annotations.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        },
        indexes = {
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_created_at", columnList = "created_at")
        }
)
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue
    @Column(nullable = false, columnDefinition = "BINARY(16)", updatable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(nullable = false)
    private boolean enabled = true;

    @Version
    private Long version;

    // ====================== Enums ======================
    public enum Role {
        USER,
        ADMIN,
        ANALYST,
        AUDITOR
    }

    // ====================== Konstruktoren ======================
    public User() {}

    public User(String username, String plainPassword, Role role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.role = role;
        this.passwordHash = new BCryptPasswordEncoder().encode(plainPassword);
    }

    // ====================== Business Methoden ======================
    public boolean verifyPassword(String plainPassword) {
        return new BCryptPasswordEncoder().matches(plainPassword, this.passwordHash);
    }

    public boolean changePassword(String newPlainPassword) {
        if (newPlainPassword == null || newPlainPassword.trim().length() < 8) {
            return false;
        }
        this.passwordHash = new BCryptPasswordEncoder().encode(newPlainPassword.trim());
        return true;
    }

    public void updateLastLogin() {
        this.lastLogin = Instant.now();
    }

    // ====================== Getter & Setter ======================
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastLogin() { return lastLogin; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}