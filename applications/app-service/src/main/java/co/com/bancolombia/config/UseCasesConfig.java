package co.com.bancolombia.config;

import co.com.bancolombia.model.gateways.PasswordEncoderService;
import co.com.bancolombia.model.gateways.RoleRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import co.com.bancolombia.usecase.api.security.RegisterUserUseCaseImpl;
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
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                   RoleRepository roleRepository, PasswordEncoderService passwordEncoderService) {
        return new RegisterUserUseCaseImpl(userRepository, roleRepository, passwordEncoderService);
    }

}
