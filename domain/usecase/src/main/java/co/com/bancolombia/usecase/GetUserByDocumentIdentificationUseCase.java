package co.com.bancolombia.usecase;

import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;


public interface GetUserByDocumentIdentificationUseCase {

    Mono<User> execute(String username);
}
