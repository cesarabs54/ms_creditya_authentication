package co.com.bancolombia.usecase.api.security;

import co.com.bancolombia.model.entities.RefreshToken;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.gateways.RefreshTokenRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import co.com.bancolombia.usecase.GetUserByDocumentIdentificationUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class GetUserByDocumentIdentificationUseCaseImpl implements GetUserByDocumentIdentificationUseCase {

    private final UserRepository userRepository;

    @Override
    public Mono<User> execute(String documentIdentification) {
        return userRepository.findByDocumentIdentification(documentIdentification)
                .switchIfEmpty(Mono.error(
                        new RuntimeException(
                                "Error: Document not found - " + documentIdentification)));
    }

}