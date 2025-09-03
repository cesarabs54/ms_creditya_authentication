package co.com.bancolombia.api.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthEntryPointJwt implements ServerAuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        log.error("Usuario no autorizado: {}", ex.getMessage());

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("path", exchange.getRequest().getPath().value());

        try {
            byte[] bytes = mapper.writeValueAsBytes(body);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            byte[] fallback = ("{\"error\":\"Unauthorized\"}")
                    .getBytes(StandardCharsets.UTF_8);
            var buffer = response.bufferFactory().wrap(fallback);
            return response.writeWith(Mono.just(buffer));
        }
    }
}
