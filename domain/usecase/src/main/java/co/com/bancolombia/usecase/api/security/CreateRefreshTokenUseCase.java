package co.com.bancolombia.usecase.api.security;


import co.com.bancolombia.model.entities.RefreshToken;
import co.com.bancolombia.model.gateways.RefreshTokenRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RequiredArgsConstructor
public class CreateRefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Mono<RefreshToken> execute(String username, String token, Instant expiry) {
        return userRepository.findByDocumentIdentification(username)
                .switchIfEmpty(Mono.error(
                        new RuntimeException(
                                "Error: Identificacion not found - " + username))
                ).flatMap(
                        userFound -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(userFound)
                                .token(token)
                                .expiryDate(expiry)
                                .build())

                );
    }
}
