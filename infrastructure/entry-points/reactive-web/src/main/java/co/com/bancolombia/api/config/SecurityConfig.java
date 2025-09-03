package co.com.bancolombia.api.config;

import co.com.bancolombia.api.security.jwt.AuthEntryPointJwt;
import co.com.bancolombia.api.security.jwt.AuthTokenFilter;
import co.com.bancolombia.api.security.service.SecurityUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Log4j2
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final SecurityUserDetailsService userDetailsService;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        var authManager = new UserDetailsRepositoryReactiveAuthenticationManager(
                userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/**", "/test/**", "/configuration/**", "/actuator/info").permitAll()
                        .pathMatchers("/v3/api-docs", "/webjars/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth/signup").hasAnyRole("ADMIN", "ADVISOR")
                        .pathMatchers(HttpMethod.GET, "/users/users/**").hasRole("CLIENT")
                        .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(authTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

}
