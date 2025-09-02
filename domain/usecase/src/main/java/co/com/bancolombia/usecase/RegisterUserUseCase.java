package co.com.bancolombia.usecase;


import co.com.bancolombia.model.dto.RegisterRequest;
import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<User> execute(RegisterRequest request);
}
