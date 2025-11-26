package bbu.solution.logwatchai.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service responsible for generating and validating JWT tokens.
 * <p>
 * This service provides:
 * <ul>
 *     <li>Creation of signed JWT access tokens (HS256)</li>
 *     <li>Extraction of username (subject) from valid tokens</li>
 *     <li>Basic claim handling (e.g., user role)</li>
 * </ul>
 * <p>
 * The token is signed using an HMAC key derived from the configured secret.
 */
@Service
public class JwtService {

    private final Key key;

    /**
     * Initializes the signing key using the configured secret value.
     *
     * @param secret the secret used to generate the HMAC key
     */
    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT access token for the specified user.
     * <p>
     * The token contains:
     * <ul>
     *     <li>{@code sub} (username)</li>
     *     <li>{@code role} claim</li>
     *     <li>{@code iat} / {@code exp}</li>
     * </ul>
     *
     * @param username the username to embed as the subject
     * @param role     the user's role to include as a claim
     * @return a signed and encoded JWT string
     */
    public String generateToken(String username, String role) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600_000)) // 1 hour expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from a valid JWT.
     * <p>
     * A 60-second clock skew tolerance is applied to avoid issues caused
     * by minor time synchronization differences between systems.
     *
     * @param token the raw JWT string
     * @return the username contained in the {@code sub} field
     * @throws ExpiredJwtException      if the token is expired
     * @throws JwtException             if the token is invalid or malformed
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
