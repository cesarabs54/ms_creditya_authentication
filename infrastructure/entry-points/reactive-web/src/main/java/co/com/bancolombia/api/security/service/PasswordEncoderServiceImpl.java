package co.com.bancolombia.api.security.service;

import co.com.bancolombia.model.gateways.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PasswordEncoderServiceImpl implements PasswordEncoderService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Boolean> matches(String rawPassword, String encodedPassword) {
        return Mono.fromSupplier(() -> passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Override
    public Mono<String> encode(String rawPassword) {
        return Mono.fromSupplier(() -> passwordEncoder.encode(rawPassword));
    }
}
