package co.com.bancolombia.usecase;


import co.com.bancolombia.model.dtos.RegisterRequest;
import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<User> execute(RegisterRequest request);
}
