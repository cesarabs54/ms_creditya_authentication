package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;

public interface JwtGateway {
    Mono<String> generateToken(String username);

    Mono<String> getUsernameFromToken(String token);

    Mono<String> generateAccessToken(User user);

    Mono<String> generateRefreshToken(User user);

    Mono<Boolean> validateToken(String token);

    Mono<Void> invalidateRefreshTokensForUser(String userId);
}
