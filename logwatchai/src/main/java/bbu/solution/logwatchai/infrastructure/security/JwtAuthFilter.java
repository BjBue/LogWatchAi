package bbu.solution.logwatchai.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A security filter responsible for processing JWT-based authentication.
 * <p>
 * This filter is executed once per request and attempts to:
 * <ul>
 *     <li>Read the {@code Authorization} header</li>
 *     <li>Validate and parse the JWT token</li>
 *     <li>Load the associated user details</li>
 *     <li>Populate the Spring Security context with an authenticated user</li>
 * </ul>
 *
 * If the request does not contain a proper Bearer token, the filter simply
 * passes the request along the chain without modifying the security context.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Creates a new instance of the JWT authentication filter.
     *
     * @param jwtService         service used to extract and validate JWT tokens
     * @param userDetailsService service used to load user details by username
     */
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Inspects each incoming HTTP request for a Bearer token. If a valid token is found,
     * the associated user is authenticated and placed into the {@link SecurityContextHolder}.
     *
     * @param request  the incoming HTTP request
     * @param response the HTTP response associated with the request
     * @param chain    the filter chain that continues request execution
     * @throws ServletException if an internal servlet error occurs
     * @throws IOException      if a read/write error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No token found → continue without authentication
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        UserDetails user = userDetailsService.loadUserByUsername(username);

        // Create an authentication token and set it into the security context
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        // Continue request execution
        chain.doFilter(request, response);
    }
}
