package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entities.UserRoleData;
import co.com.bancolombia.r2dbc.mappers.UserMapper;
import co.com.bancolombia.r2dbc.repositories.UserDataRepository;
import co.com.bancolombia.r2dbc.repositories.UserRoleDataRepository;
import co.com.bancolombia.r2dbc.util.IdGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDataRepositoryAdapter implements UserRepository {

    private final UserDataRepository repository;
    private final UserRoleDataRepository userRoleDataRepository;
    private final UserMapper mapper;

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toModel);
    }

    @Override
    public Mono<User> save(User user) {
        log.debug("Iniciando guardado de usuario: {}", user.getFirstName());

        Set<Role> roles = user.getRoles();
        if(roles == null || roles.isEmpty()){
            return Mono.error(new IllegalArgumentException("El usuario debe tener al menos un rol"));
        }

        if(user.getUserId() != null) {
            log.debug("Usuario existente, actualizando...");
            return repository.save(mapper.toData(user)).map(mapper::toModel);
        }

        var generateId = IdGeneratorUtil.generateDefaultUUID();
        log.debug("Usuario nuevo con ID: {}", user.getUserId());
        user.setUserId(generateId);

        var userData = mapper.toData(user);
        userData.setNew(true);

        return repository.save(userData)
                .doOnNext(savedUser -> log.debug("Usuario guardado: {}", savedUser.getUserId()))
                .doOnError(error -> log.error("Error guardando usuario: ", error))
                .flatMap(savedUserData ->
                        Flux.fromIterable(roles)
                                .doOnNext(role -> log.debug("Guardando rol: {}", role.getRoleId()))
                                .flatMap(role -> {
                                    UserRoleData userRoleData = new UserRoleData(
                                            savedUserData.getUserId(),
                                            role.getRoleId()
                                    );
                                    return userRoleDataRepository.save(userRoleData)
                                            .doOnNext(saved -> log.debug("Rol guardado: {}", saved))
                                            .doOnError(error -> log.error("Error guardando rol: ", error));
                                })
                                .then(Mono.just(savedUserData))
                .map(mapper::toModel)
                .doOnSuccess(result -> log.debug("Usuario con roles guardado exitosamente: {}", result))
                .doOnError(error -> log.error("Error finalizando guardado de usuario con roles: ", error))
                );
    }

    @Override
    public Mono<Boolean> existsByDocumentIdentification(String identification) {
        return repository.existsByDocumentIdentification(identification);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

}
