package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.r2dbc.entities.UserData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserDataRepository extends R2dbcRepository<UserData, String> {

    Mono<UserData> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByDocumentIdentification(String identification);

}
