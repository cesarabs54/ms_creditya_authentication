package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.enums.ERole;
import co.com.bancolombia.model.gateways.RoleRepository;
import co.com.bancolombia.r2dbc.entities.ERoleData;
import co.com.bancolombia.r2dbc.mappers.RoleMapper;
import co.com.bancolombia.r2dbc.repositories.RoleDataRepository;
import co.com.bancolombia.r2dbc.repositories.UserRoleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleDataRepository roleDataRepository;
    private final UserRoleDataRepository userRoleDataRepository;
    private final RoleMapper roleMapper;

    @Override
    public Mono<Role> findByName(ERole name) {
        return roleDataRepository.findByName(ERoleData.valueOf(name.name()))
                .map(roleMapper::toModel);
    }

    @Override
    public Flux<Role> findByUserId(UUID userId) {
        return userRoleDataRepository.findByUserId(userId)
                .flatMap(userRoleData -> roleDataRepository.findById(userRoleData.getRoleId())
                        .map(roleMapper::toModel)
                );
    }

}
