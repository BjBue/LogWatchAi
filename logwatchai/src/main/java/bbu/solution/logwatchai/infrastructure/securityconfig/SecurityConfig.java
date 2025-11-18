package bbu.solution.logwatchai.infrastructure.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health","/config-test").permitAll()   // public
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)  // no csrf
                .formLogin(Customizer.withDefaults()); //standard login-form
        return http.build();
    }

}
