package co.com.bancolombia.usecase.api.security;

import co.com.bancolombia.model.gateways.JwtGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtUseCase {

    private final JwtGateway jwtGateway;

    public Mono<String> generateToken(String username) {
        return jwtGateway.generateToken(username);
    }

    public Mono<String> extractUsername(String token) {
        return jwtGateway.getUsernameFromToken(token);
    }

    public Mono<Boolean> validateToken(String token) {
        return jwtGateway.validateToken(token);
    }
}
