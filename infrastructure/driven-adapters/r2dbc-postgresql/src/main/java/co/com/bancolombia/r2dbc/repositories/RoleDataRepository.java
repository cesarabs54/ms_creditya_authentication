package co.com.bancolombia.r2dbc.repositories;

import co.com.bancolombia.model.enums.ERole;
import co.com.bancolombia.r2dbc.entities.ERoleData;
import co.com.bancolombia.r2dbc.entities.RoleData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface RoleDataRepository extends R2dbcRepository<RoleData, Integer> {

    Mono<RoleData> findByName(ERoleData name);

    Flux<RoleData> findAllByNameIn(Collection<ERoleData> names);

}
