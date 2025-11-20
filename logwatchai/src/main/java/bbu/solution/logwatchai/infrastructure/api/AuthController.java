package bbu.solution.logwatchai.infrastructure.api;

import bbu.solution.logwatchai.domain.user.User;
import bbu.solution.logwatchai.domain.user.UserService;
import bbu.solution.logwatchai.infrastructure.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService,  UserService userService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userService.getUserByUsername(request.username())
                .orElseThrow(
                        () -> new RuntimeException("username not found")
                );

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new LoginResponse(
                user.getUsername(),
                user.getRole().name(),
                token,
                "Bearer"
        );
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String username, String role, String token, String type) {}

}

