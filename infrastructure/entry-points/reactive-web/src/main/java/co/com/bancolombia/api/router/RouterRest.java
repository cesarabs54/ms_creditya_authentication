package co.com.bancolombia.api.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.dto.responses.MessageResponse;
import co.com.bancolombia.api.handler.AuthHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration("authRouterConfig")
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/auth/signup",
                    beanClass = AuthHandler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            summary = "Registrar usuario",
                            description = "Crea un nuevo usuario en el sistema",
                            requestBody = @RequestBody(
                                    description = "Datos de registro",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = SignUpRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario registrado exitosamente",
                                            content = @Content(schema = @Schema(implementation = MessageResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler handler) {
        return route()
                .POST("/auth/signup", handler::registerUser)
                .build();
    }
}
