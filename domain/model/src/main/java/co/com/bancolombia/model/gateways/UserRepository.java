package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.entities.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> findByEmail(String email);

    Mono<User> save(User user);

    Mono<Boolean> existsByDocumentIdentification(String identification);

    Mono<Boolean> existsByEmail(String email);

}
