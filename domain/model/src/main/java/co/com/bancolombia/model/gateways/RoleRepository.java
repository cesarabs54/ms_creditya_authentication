package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.enums.ERole;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    Mono<Role> findByName(ERole name);
}

