package co.com.bancolombia.model.gateways;

import reactor.core.publisher.Mono;

public interface PasswordEncoderService {

    Mono<Boolean> matches(String rawPassword, String encodedPassword);

    Mono<String> encode(String rawPassword);
}

