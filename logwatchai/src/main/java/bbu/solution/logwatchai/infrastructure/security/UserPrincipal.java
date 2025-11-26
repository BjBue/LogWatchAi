package bbu.solution.logwatchai.infrastructure.security;

import bbu.solution.logwatchai.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom Spring Security {@link UserDetails} implementation that wraps the domain {@link User} entity.
 * <p>
 * This class acts as an adapter between the application's user model and Spring Security’s
 * authentication framework. It exposes essential user information such as:
 * <ul>
 *     <li>Username</li>
 *     <li>Password hash</li>
 *     <li>Enabled state</li>
 *     <li>Granted authorities (roles)</li>
 * </ul>
 * <p>
 * Spring Security uses {@code UserDetails} instances to authenticate and authorize users.
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * Creates a new {@code UserPrincipal} wrapping the given domain user.
     *
     * @param user the domain user entity
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * <p>
     * Spring Security requires all roles to be prefixed with {@code ROLE_}, so this method
     * automatically formats the application's domain role.
     *
     * @return a list containing a single {@link SimpleGrantedAuthority} matching the user's role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Returns the hashed password for authentication.
     *
     * @return the stored password hash
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the username used for authentication.
     *
     * @return the stored username
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account is enabled.
     *
     * @return {@code true} if the user is enabled, otherwise {@code false}
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * Indicates whether the account is non-expired.
     * <p>
     * This implementation does not include account expiration logic and always returns {@code true}.
     *
     * @return {@code true}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is non-locked.
     * <p>
     * Since lockout logic is not implemented, this returns {@code true}.
     *
     * @return {@code true}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the credentials are non-expired.
     * <p>
     * Credentials expiration is not implemented, so this always returns {@code true}.
     *
     * @return {@code true}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
