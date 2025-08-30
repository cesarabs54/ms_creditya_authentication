package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.dto.responses.ErrorMessage;
import co.com.bancolombia.api.dto.responses.MessageResponse;
import co.com.bancolombia.api.handler.AuthHandler;
import co.com.bancolombia.api.handler.GlobalErrorHandler;
import co.com.bancolombia.api.util.RequestValidator;
import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.enums.ERole;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        co.com.bancolombia.api.router.RouterRest.class,
        AuthHandler.class,
        RequestValidator.class,
        GlobalErrorHandler.class,
        WebProperties.class
})
@AutoConfigureWebTestClient
class AuthRouterIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    private SignUpRequest validBody() {
        return new SignUpRequest(
                "1002003000",
                "Juan",
                "Pérez",
                "1990-05-10", // SignUpRequest usa String para birthDate
                "Calle 1 # 2-3",
                "+57 3001112233",
                "jperez@example.com",
                "Secreta*123",
                5_000_000L,
                Set.of("ROLE_APPLICANT")
        );
    }

    private User fakeUser() {
        return User.builder()
                .documentIdentification("1002003000")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 10))
                .direction("Calle 1 # 2-3")
                .telephoneNumber("+57 3001112233")
                .email("jperez@example.com")
                .password("encoded")
                .baseSalary(5_000_000L)
                .roles(Set.of(Role.builder().name(ERole.ROLE_APPLICANT).build()))
                .build();
    }

    @Test
    @DisplayName("POST /auth/signup -> 200 y mensaje de éxito")
    void signUp_ok() {
        when(registerUserUseCase.execute(ArgumentMatchers.any()))
                .thenReturn(Mono.just(fakeUser()));

        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(validBody())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(MessageResponse.class)
                .value(resp -> {
                    // Mensaje definido por AuthHandler
                    org.assertj.core.api.Assertions.assertThat(resp.message())
                            .isEqualTo("Usuario creado correctamente");
                });
    }

    @Test
    @DisplayName("POST /auth/signup -> 400 cuando la validación falla (email inválido)")
    void signUp_validationError_badRequest() {
        SignUpRequest invalid = new SignUpRequest(
                "1002003000",
                "Juan",
                "Pérez",
                "1990-05-10",
                "Calle 1 # 2-3",
                "+57 3001112233",
                "no-es-email", // inválido
                "Secreta*123",
                5_000_000L,
                Set.of("ROLE_APPLICANT")
        );

        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(ErrorMessage.class)
                .value(err -> {
                    org.assertj.core.api.Assertions.assertThat(err.statusCode()).isEqualTo(400);
                    // GlobalErrorHandler junta mensajes de ConstraintViolation
                    org.assertj.core.api.Assertions.assertThat(err.message()).contains("correo", "válido");
                    org.assertj.core.api.Assertions.assertThat(err.description()).isEqualTo("/auth/signup");
                });
    }

    @Test
    @DisplayName("POST /auth/signup -> 500 cuando el caso de uso falla con IllegalArgumentException")
    void signUp_useCaseBusinessError_mappedByGlobalHandler() {
        when(registerUserUseCase.execute(ArgumentMatchers.any()))
                .thenReturn(Mono.error(new IllegalArgumentException("El correo ya está en uso")));

        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(validBody())
                .exchange()
                .expectStatus().is5xxServerError() // GlobalErrorHandler actual no mapea IllegalArgumentException a 400
                .expectBody(ErrorMessage.class)
                .value(err -> {
                    org.assertj.core.api.Assertions.assertThat(err.statusCode()).isBetween(500, 599);
                    org.assertj.core.api.Assertions.assertThat(err.message())
                            .contains("Error interno del servidor", "El correo ya está en uso");
                });
    }

}
