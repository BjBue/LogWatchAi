package bbu.solution.logwatchai.infrastructure.security;

import bbu.solution.logwatchai.domain.user.Role;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable response model representing the result of a successful JWT-based login.
 * <p>
 * This record bundles all relevant authentication information:
 * <ul>
 *     <li>The authenticated user's ID</li>
 *     <li>The username</li>
 *     <li>The user's role</li>
 *     <li>The generated JWT access token</li>
 *     <li>The token type (always {@code Bearer})</li>
 *     <li>The timestamp when the token expires</li>
 * </ul>
 *
 * It is typically returned by authentication endpoints and used by clients
 * to store and attach the token to future requests.
 */
public record JwtResponse(
        UUID userId,
        String username,
        Role role,
        String accessToken,
        String tokenType,
        Instant expiresAt
) {

    /**
     * Compact constructor enforcing that the access token is not empty.
     *
     * @throws IllegalArgumentException if the access token is null or blank
     */
    public JwtResponse {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken must not be empty");
        }
    }

    /**
     * Returns the token type. Although this field exists in the record,
     * the implementation forces it to always return {@code "Bearer"}.
     *
     * @return the token type, fixed to "Bearer"
     */
    @Override
    public String tokenType() {
        return "Bearer";
    }

    /**
     * Returns the full token string formatted as:
     * <pre>
     *     Bearer &lt;accessToken&gt;
     * </pre>
     *
     * @return the formatted Authorization header value
     */
    public String fullToken() {
        return tokenType() + " " + accessToken;
    }
}
