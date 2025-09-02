package co.com.bancolombia.usecase.api.security;


import co.com.bancolombia.model.entities.RefreshToken;
import co.com.bancolombia.model.gateways.RefreshTokenRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshTokenDurationMs;

    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Mono<RefreshToken> createRefreshToken(String identification) {
        return userRepository.findByDocumentIdentification(identification)
                .switchIfEmpty(Mono.error(
                        new RuntimeException(
                                "Error: Identification not found - " + identification)))
                .flatMap(user1 -> refreshTokenRepository.save(RefreshToken.builder()
                        .user(user1)
                        .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                        .token(UUID.randomUUID().toString())
                        .build()));
    }

    public Mono<RefreshToken> verifyExpiration(RefreshToken token) {

        return Mono.defer(() -> {
            if (token.getExpiryDate().isBefore(Instant.now())) {
                return refreshTokenRepository.delete(token).then(
                        Mono.error(new IllegalArgumentException("Refresh token expired")));
            }
            return Mono.just(token);
        });
    }

    public Mono<Void> deleteByUsername(String identification) {
        return userRepository.findByDocumentIdentification(identification)
                .switchIfEmpty(Mono.error(
                        new RuntimeException(
                                "Error: Identification not found - " + identification)))
                .flatMap(user -> refreshTokenRepository.deleteByUser(user).then());
    }
}