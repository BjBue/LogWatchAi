package bbu.solution.logwatchai.infrastructure.config.securityconfig;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI (Swagger) documentation for the application,
 * specifically enabling JWT Bearer Authentication to be used inside
 * the Swagger UI.
 *
 * <h2>Purpose</h2>
 * This configuration ensures that:
 * <ul>
 *     <li>Swagger UI shows the “Authorize” button</li>
 *     <li>JWT tokens can be entered in Swagger for authenticated requests</li>
 *     <li>All API endpoints automatically reference the configured security scheme</li>
 * </ul>
 *
 * <h2>How It Works</h2>
 * <ul>
 *     <li>A security scheme named <code>BearerAuth</code> is registered.</li>
 *     <li>The security type is explicitly set to HTTP Bearer.</li>
 *     <li>The <code>bearerFormat</code> indicates the token type is JWT.</li>
 *     <li>The security requirement is added globally so controllers inherit it automatically.</li>
 * </ul>
 *
 * <h2>Swagger usage</h2>
 * After startup, when opening Swagger UI:
 * <ol>
 *     <li>Click <b>Authorize</b></li>
 *     <li>Enter your JWT as: <code>Bearer &lt;token&gt;</code> or only <code>&lt;token&gt;</code>
 *         (Swagger handles the prefix automatically depending on configuration)</li>
 *     <li>Execute secured endpoints</li>
 * </ol>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates a custom {@link OpenAPI} configuration bean
     * that registers the Bearer Auth scheme and attaches it
     * as a global security requirement.
     *
     * @return a fully configured {@link OpenAPI} instance for Swagger.
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                // Add a global security requirement: endpoints expect Bearer Authentication
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                // Register the security scheme definition
                .components(new Components().addSecuritySchemes(
                        "BearerAuth",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
