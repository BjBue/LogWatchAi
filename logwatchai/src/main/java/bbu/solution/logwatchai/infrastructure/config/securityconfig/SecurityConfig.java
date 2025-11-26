package bbu.solution.logwatchai.infrastructure.config.securityconfig;

import bbu.solution.logwatchai.infrastructure.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configures Spring Security for the application, including:
 * <ul>
 *     <li>JWT authentication processing</li>
 *     <li>CORS integration</li>
 *     <li>Request authorization rules</li>
 *     <li>Disabling CSRF for stateless JWT usage</li>
 *     <li>Password encoding configuration</li>
 *     <li>Exposing the AuthenticationManager</li>
 * </ul>
 *
 * <h2>General Purpose</h2>
 * This class builds the security filter chain that defines how incoming HTTP requests are secured.
 * It integrates the custom {@link JwtAuthFilter}, applies CORS rules, configures public vs. secured
 * endpoints, and sets the authentication components used by Spring Security.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Builds and configures the Spring Security filter chain.
     *
     * <h2>What is configured here?</h2>
     * <ul>
     *     <li><b>CSRF disabled:</b> Not needed for stateless JWT-based APIs.</li>
     *     <li><b>CORS enabled:</b> Uses the application-wide CORS rules provided by {@code appCorsConfigurationSource}.</li>
     *     <li><b>Public endpoints:</b> Swagger, health checks, and /auth/login are accessible without authentication.</li>
     *     <li><b>Protected endpoints:</b> Every other request requires a valid JWT.</li>
     *     <li><b>UserDetailsService:</b> Required for authentication and JWT validation.</li>
     *     <li><b>JWT Filter:</b> Inserted before {@link UsernamePasswordAuthenticationFilter}
     *     so tokens are validated before Spring's default authentication kicks in.</li>
     * </ul>
     *
     * <h2>Process Flow</h2>
     * <ol>
     *     <li>Incoming request arrives</li>
     *     <li>JWT filter checks Authorization header for a bearer token</li>
     *     <li>If token is valid → attach authentication to SecurityContext</li>
     *     <li>Spring checks endpoint authorization (public or protected)</li>
     *     <li>Request proceeds or is rejected</li>
     * </ol>
     *
     * @param http  Spring Security's main configuration object
     * @param cors  CORS configuration provided by {@link bbu.solution.logwatchai.infrastructure.config.securityconfig.CorsConfig}
     * @return the fully configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           @Qualifier("appCorsConfigurationSource") CorsConfigurationSource cors) throws Exception {

        http
                // Disable CSRF because JWT does not use sessions
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS and inject custom configuration
                .cors(c -> c.configurationSource(cors))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible endpoints
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/health",
                                "/auth/login"
                        ).permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Required when using JWT and custom authentication logic
                .userDetailsService(userDetailsService)

                // Insert custom JWT filter before Spring's username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes the {@link AuthenticationManager} to the application context.
     * <p>
     * This allows authentication requests (e.g., login) to be processed using
     * Spring Security's internal authentication mechanisms.
     *
     * @param config authentication configuration provided by Spring Boot
     * @return an initialized AuthenticationManager
     * @throws Exception if initialization fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides a strong password encoder using the BCrypt hashing algorithm.
     * <p>
     * BCrypt automatically applies salting and multiple rounds of hashing,
     * making stored passwords significantly more secure.
     *
     * @return a BCrypt-based {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
