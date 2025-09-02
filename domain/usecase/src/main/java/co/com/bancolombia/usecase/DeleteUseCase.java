package co.com.bancolombia.usecase;


import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;

public interface DeleteUseCase<T> {

    Mono<Void> delete(T entity);

    interface GetUserByUsernameUseCase {

        Mono<User> execute(String username);
    }
}
