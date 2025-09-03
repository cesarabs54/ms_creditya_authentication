package co.com.bancolombia.api.security.jwt;

import co.com.bancolombia.api.security.service.SecurityUserDetailsService;
import co.com.bancolombia.usecase.api.security.JwtUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter implements WebFilter {

    private final JwtUseCase jwtUseCase;
    private final SecurityUserDetailsService securityUserDetailsService;

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String jwt = parseJwt(exchange);
        if (jwt == null) {
            return chain.filter(exchange);
        }

        return jwtUseCase.validateToken(jwt)
                .flatMap(valid -> {
                    if (!valid) {
                        return chain.filter(exchange);
                    }
                    return jwtUseCase.extractUsername(jwt)
                            .flatMap(securityUserDetailsService::findByUsername)
                            .flatMap(userDetails -> {
                                var authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                                return chain.filter(exchange)
                                        .contextWrite(
                                                ReactiveSecurityContextHolder.withAuthentication(
                                                        authentication));
                            });
                })
                .onErrorResume(e -> {
                    log.error("Error in JWT filter: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private String parseJwt(ServerWebExchange exchange) {
        String headerAuth = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
