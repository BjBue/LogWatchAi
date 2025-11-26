package bbu.solution.logwatchai.application.user;

import bbu.solution.logwatchai.infrastructure.persistence.user.UserRepository;
import bbu.solution.logwatchai.infrastructure.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's {@link UserDetailsService} that loads
 * user information from the application's persistence layer.
 *
 * <p>I retrieve users from the {@link UserRepository}, convert them into
 * {@link UserPrincipal} objects, and provide them to Spring Security during
 * authentication. If the user cannot be found, I throw the appropriate
 * {@link UsernameNotFoundException}.</p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Creates a new instance of the user details service.
     *
     * <p>I receive a {@link UserRepository} so I can look up users by their
     * username whenever Spring Security requests authentication details.</p>
     *
     * @param repo the repository used to load user entities
     */
    public UserDetailsServiceImpl(UserRepository repo) {
        this.userRepository = repo;
    }

    /**
     * Loads a user by its username and returns a Spring Security {@link UserDetails}
     * object representing the authenticated user.
     *
     * <p>I access the {@link UserRepository} to fetch the user data. If a user
     * with the given name exists, I wrap it in a {@link UserPrincipal}. If not,
     * I throw a {@link UsernameNotFoundException}, as expected by Spring Security.</p>
     *
     * @param username the username to look up
     * @return the authenticated user's details
     * @throws UsernameNotFoundException if no matching user exists
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return userRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
