package co.com.bancolombia.usecase.api.security;

import co.com.bancolombia.model.dtos.AuthRequest;
import co.com.bancolombia.model.dtos.AuthResponse;
import co.com.bancolombia.model.exceptions.AuthenticationException;
import co.com.bancolombia.model.gateways.JwtGateway;
import co.com.bancolombia.model.gateways.PasswordEncoderService;
import co.com.bancolombia.model.gateways.RoleRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import co.com.bancolombia.usecase.AuthenticateUserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtGateway jwtGateway;
    private final PasswordEncoderService passwordEncoderService;

    @Override
    public Mono<AuthResponse> execute(AuthRequest request) {
        return userRepository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(AuthenticationException.emailNotFound()))
                .flatMap(user -> passwordEncoderService.matches(request.password(),
                                user.getPassword())
                        .flatMap(matches -> {
                            if (Boolean.FALSE.equals(matches)) {
                                return Mono.error(AuthenticationException.invalidCredentials());
                            }

                            return roleRepository.findByUserId(user.getUserId())
                                    .collect(Collectors.toSet())
                                    .flatMap(roles -> {
                                        user.setRoles(roles);

                                        return jwtGateway.generateAccessToken(user)
                                                .zipWith(jwtGateway.generateRefreshToken(user))
                                                .map(tokens -> new AuthResponse(
                                                        tokens.getT1(),
                                                        tokens.getT2(),
                                                        user.getUserId().toString(),
                                                        user.getFirstName(),
                                                        user.getLastName(),
                                                        user.getEmail(),
                                                        user.getRolesAsString()
                                                ));
                                    });
                        })
                );
    }
}
