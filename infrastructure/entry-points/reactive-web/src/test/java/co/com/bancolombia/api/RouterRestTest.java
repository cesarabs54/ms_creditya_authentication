package co.com.bancolombia.api;

import co.com.bancolombia.api.config.SecurityConfig;
import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.dto.responses.MessageResponse;
import co.com.bancolombia.api.handler.AuthHandler;
import co.com.bancolombia.api.handler.GlobalErrorHandler;
import co.com.bancolombia.api.router.RouterRest;
import co.com.bancolombia.api.util.RequestValidator;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Set;

@ContextConfiguration(classes = {RouterRest.class, AuthHandler.class, GlobalErrorHandler.class})
@Import(SecurityConfig.class)
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;
    @MockitoBean
    private RequestValidator requestValidator;
    @MockitoBean
    private ObjectMapper objectMapper;

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
                Set.of("")
        );
    }

    @Test
    void testListenGETUseCaseV1() {
        webTestClient.get()
                .uri("/auth/signup")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETUseCaseV2() {
        webTestClient.get()
                .uri("/api/v2/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCaseV1() {
        webTestClient.get()
                .uri("/api/v1/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCaseV2() {
        webTestClient.get()
                .uri("/api/v2/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenPOSTUseCaseV1() {
        webTestClient.post()
                .uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(validBody())
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessageResponse.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse.message()).isEqualTo("Usuario creado correctamente");
                        }
                );
    }

    @Test
    void testListenPOSTUseCaseV2() {
        webTestClient.post()
                .uri("/api/v2/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
}
