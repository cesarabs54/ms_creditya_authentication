package co.com.bancolombia.api;


import co.com.bancolombia.api.config.JacksonConfig;
import co.com.bancolombia.api.config.TestSecurityConfig;
import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.handler.AuthHandler;
import co.com.bancolombia.api.handler.GlobalErrorHandler;
import co.com.bancolombia.api.router.RouterRest;
import co.com.bancolombia.api.util.RequestValidator;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        RouterRest.class,
        AuthHandler.class,
        GlobalErrorHandler.class,
        TestSecurityConfig.class,
        JacksonConfig.class
})
@Import({RequestValidator.class})
@WebFluxTest
class RouterRestTest {

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSignupReturnsSuccessMessage() {

        var request = new SignUpRequest(
                "88111111", // documentIdentification
                "Cesar",                       // firstName
                "Alfonso",                     // lastName
                "1980-11-10",                  // birthDate
                "Calle 123 #45-67",            // address
                "3001234567",                  // telephoneNumber
                "cesar@correo.com",            // email
                "password123",                 // password
                2_500_000L,                    // baseSalary
                Set.of("ROLE_CLIENT", "ROLE_ADMIN") // roles
        );

        when(registerUserUseCase.execute(any()))
                .thenReturn(Mono.just(new User()));

        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Usuario creado correctamente");

    }

}
