package co.com.bancolombia.usecase.api.security;

import co.com.bancolombia.model.dtos.RegisterRequest;
import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.enums.ERole;
import co.com.bancolombia.model.gateways.PasswordEncoderService;
import co.com.bancolombia.model.gateways.RoleRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderService passwordEncoderService;

    public RegisterUserUseCaseImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Mono<User> execute(RegisterRequest request) {
        return userRepository.existsByEmail(request.email())
                .flatMap(existsEmail -> {
                    if (Boolean.TRUE.equals(existsEmail)) {
                        return Mono.error(new IllegalArgumentException("El correo ya está en uso"));
                    }
                    return userRepository.existsByDocumentIdentification(request.documentIdentification());
                }).flatMap(existsDocumentIdentification -> {
                    if (Boolean.TRUE.equals(existsDocumentIdentification)) {
                        return Mono.error(
                                new IllegalArgumentException("El número de documento ya existe"));
                    }

                    Set<String> strRoles = request.roles();
                    Flux<Role> rolesFlux;

                    if (strRoles == null || strRoles.isEmpty()) {
                        rolesFlux = roleRepository.findByName(ERole.ROLE_APPLICANT)
                                .switchIfEmpty(Mono.error(new IllegalStateException(
                                        "No se encontró el rol ROLE_APPLICANT")))
                                .flux();
                    } else {
                        rolesFlux = Flux.fromIterable(strRoles)
                                .flatMap(role -> {
                                    ERole eRole = ERole.valueOf(role);
                                    return roleRepository.findByName(eRole)
                                            .switchIfEmpty(Mono.error(new IllegalStateException(
                                                    "Rol no encontrado: " + role)));
                                });
                    }

                    return rolesFlux.collectList()
                            .flatMap(roles ->
                                    passwordEncoderService.encode(request.password())
                                            .flatMap(encodedPassword -> {
                                                User newUser = new User(
                                                        null,
                                                        request.documentIdentification(),
                                                        request.firstName(),
                                                        request.lastName(),
                                                        request.birthDate(),
                                                        request.direction(),
                                                        request.telephoneNumber(),
                                                        request.email(),
                                                        encodedPassword,
                                                        request.baseSalary(),
                                                        new HashSet<>(roles)
                                                );
                                                return userRepository.save(newUser);
                                            })
                            );
                }
        );
    }

}
