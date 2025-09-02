package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.r2dbc.entities.ERoleData;
import co.com.bancolombia.r2dbc.entities.RoleData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface RoleDataRepository extends R2dbcRepository<RoleData, Integer> {

    Mono<RoleData> findByName(ERoleData name);

    Flux<RoleData> findAllByRoleIdIn(List<UUID> roleIds);

    Mono<RoleData> findById(UUID roleId);
}
