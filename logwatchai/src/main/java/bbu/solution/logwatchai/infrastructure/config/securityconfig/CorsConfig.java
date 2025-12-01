package bbu.solution.logwatchai.infrastructure.config.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the application.
 * <p>
 * This configuration defines which external origins (e.g., frontend applications)
 * are allowed to make HTTP requests to this backend. It also specifies which
 * HTTP methods and headers are permitted, and whether credentials such as cookies
 * or authorization headers can be included in cross-origin requests.
 * </p>
 *
 * <h2>What this class configures</h2>
 * <ul>
 *   <li><b>Allowed Origins:</b> Specific development URLs (Blazor, Vue, etc.) that may call this API.</li>
 *   <li><b>Allowed Methods:</b> HTTP methods that clients may use (GET, POST, PUT, etc.).</li>
 *   <li><b>Allowed Headers:</b> Specifies which HTTP headers incoming requests may contain. Here: all.</li>
 *   <li><b>Allow Credentials:</b> Enables sending cookies, authorization headers, and authentication tokens.</li>
 *   <li><b>URL Pattern:</b> Applies CORS rules to all endpoints (/**).</li>
 * </ul>
 *
 * <p>This configuration is required especially when a frontend SPA (Angular, React, Vue, Blazor...) runs
 * on a different port or domain than the backend API.</p>
 */
@Configuration
public class CorsConfig {

    /**
     * Creates and configures a {@link CorsConfigurationSource} bean that defines CORS rules
     * for the entire application.
     *
     * @return a fully configured {@link CorsConfigurationSource} instance
     */
    @Bean(name = "appCorsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        /**
         * Allowed Origins:
         * These URLs are permitted to access this API.
         * This is essential for local development, where frontend and backend
         * run on different ports.
         */
        config.setAllowedOrigins(List.of(
                "http://localhost:5000",
                "http://localhost:5278",
                "http://localhost:5173",
                "http://localhost:8080",   // optional dev port
                "http://localhost:8081",   // optional dev port
                "http://127.0.0.1:5000"
        ));

        /**
         * Allowed HTTP Methods:
         * Defines which types of HTTP requests the browser may send.
         */
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        /**
         * Allowed Headers:
         * "*" allows all headers (e.g. Authorization, Content-Type, X-Custom-Header).
         */
        config.setAllowedHeaders(List.of("*"));

        /**
         * Allow Credentials:
         * Enables cookies, Authorization headers, and similar to be included in cross-origin requests.
         */
        config.setAllowCredentials(true);

        /**
         * Apply this configuration to all API endpoints.
         */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
