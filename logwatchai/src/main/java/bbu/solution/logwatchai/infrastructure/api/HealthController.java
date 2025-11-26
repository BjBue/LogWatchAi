package bbu.solution.logwatchai.infrastructure.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController provides a simple health check endpoint
 * to verify that the application is running and reachable.
 *
 * <p>The /health endpoint can be used by monitoring tools
 * or load balancers to ensure service availability.</p>
 */
@RestController
public class HealthController {

    /**
     * Returns a basic "OK" response indicating that the service is healthy.
     *
     * @return HTTP 200 with body "OK"
     */
    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("OK");
    }
}
