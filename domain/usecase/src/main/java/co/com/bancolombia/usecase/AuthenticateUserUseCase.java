package co.com.bancolombia.usecase;


import co.com.bancolombia.model.dtos.AuthRequest;
import co.com.bancolombia.model.dtos.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthenticateUserUseCase {

    Mono<AuthResponse> execute(AuthRequest request);
}

