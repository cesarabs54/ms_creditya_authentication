package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.r2dbc.entities.UserRoleData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface UserRoleDataRepository extends R2dbcRepository<UserRoleData, Integer> {

    Flux<UserRoleData> findByUserId(UUID userId);

}
