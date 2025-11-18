package bbu.solution.logwatchai.infrastructure.security;

import bbu.solution.logwatchai.domain.user.Role;
import bbu.solution.logwatchai.domain.user.User;

import java.time.Instant;
import java.util.UUID;

public record JwtResponse(
        UUID userId,
        String username,
        Role role,
        String accessToken,
        String tokenType,
        Instant expiresAt
) {
    public JwtResponse{
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken must not be empty");
        }
    }

    public String tokenType() {
        return "Bearer";
    }

    public String fullToken() {
        return tokenType() + " " + accessToken;
    }
}