package co.com.bancolombia.config;

import co.com.bancolombia.model.gateways.*;
import co.com.bancolombia.usecase.AuthenticateUserUseCase;
import co.com.bancolombia.usecase.GetAllUseCase;
import co.com.bancolombia.usecase.GetUserByDocumentIdentificationUseCase;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import co.com.bancolombia.usecase.api.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public GetUserByDocumentIdentificationUseCase getUserByUsernameUseCase(UserRepository userRepository) {
        return new GetUserByDocumentIdentificationUseCaseImpl(userRepository);
    }

    @Bean
    public GetAllUseCase getAllUseCase(UserRepository userRepository) {
        return userRepository::findAll;
    }

    @Bean
    public CreateRefreshTokenUseCase createRefreshTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {
        return new CreateRefreshTokenUseCase(refreshTokenRepository, userRepository);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            @Value("${security.jwt.jwtRefreshExpirationMs}") long durationMs
    ) {
        return new RefreshTokenUseCase(refreshTokenRepository, userRepository, durationMs);
    }

    @Bean
    public JwtUseCase jwtUseCase(JwtGateway jwtGateway) {
        return new JwtUseCase(jwtGateway);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepository userRepository,
                                                           RoleRepository roleRepository,
                                                           JwtGateway jwtGateway,
                                                           PasswordEncoderService passwordEncoderService) {
        return new AuthenticateUserUseCaseImpl(userRepository, roleRepository, jwtGateway,
                passwordEncoderService);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                   RoleRepository roleRepository, PasswordEncoderService passwordEncoderService) {
        return new RegisterUserUseCaseImpl(userRepository, roleRepository, passwordEncoderService);
    }

    @Bean
    public RefreshAccessTokenUseCase refreshAccessTokenUseCase(
            RefreshTokenUseCase refreshTokenUseCase,
            JwtGateway jwtGateway
    ) {
        return new RefreshAccessTokenUseCase(refreshTokenUseCase, jwtGateway);
    }

}
