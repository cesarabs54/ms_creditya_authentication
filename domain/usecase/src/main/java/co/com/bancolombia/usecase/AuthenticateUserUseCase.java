package co.com.bancolombia.usecase;


import co.com.bancolombia.model.dto.AuthRequest;
import co.com.bancolombia.model.dto.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthenticateUserUseCase {

    Mono<AuthResponse> execute(AuthRequest request);
}

