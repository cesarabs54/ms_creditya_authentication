package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.r2dbc.entities.ERoleData;
import co.com.bancolombia.r2dbc.entities.RoleData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface RoleDataRepository extends R2dbcRepository<RoleData, UUID> {

    Mono<RoleData> findByName(ERoleData name);

    Flux<RoleData> findAllByRoleIdIn(List<UUID> roleIds);

    @Query("SELECT role_id, name FROM roles WHERE role_id = :id")
    Mono<RoleData> findById(@Param("id") UUID id);

}
