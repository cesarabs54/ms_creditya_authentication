package co.com.bancolombia.api.router;

import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.dto.responses.MessageResponse;
import co.com.bancolombia.api.dto.responses.UserResponse;
import co.com.bancolombia.api.handler.AuthHandler;
import co.com.bancolombia.api.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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
                            tags = {"Autenticación"},
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Datos de registro",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = SignUpRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario registrado exitosamente",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = MessageResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos inválidos",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Usuario ya existe",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno",
                                            content = @Content(mediaType = "application/json")
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/auth/signing",
                    beanClass = AuthHandler.class,
                    beanMethod = "authenticateUser",
                    operation = @Operation(
                            summary = "Autenticar usuario",
                            description = "Autentica un usuario y retorna un token JWT",
                            tags = {"Autenticación"},
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Credenciales de inicio de sesión",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = SignUpRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Autenticación exitosa, retorna token JWT",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = MessageResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos inválidos",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Credenciales incorrectas",
                                            content = @Content(mediaType = "application/json")
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno",
                                            content = @Content(mediaType = "application/json")
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/users/users",
                    beanClass = UserHandler.class,
                    beanMethod = "listUsers",
                    operation = @Operation(
                            summary = "Listar usuarios",
                            description = "Retorna todos los usuarios registrados",
                            tags = {"Usuarios"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado de usuarios",
                                            content = @Content(
                                                    array = @ArraySchema(
                                                            schema = @Schema(implementation = UserResponse.class)
                                                    )
                                            )
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Error interno")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler handler, UserHandler userHandler) {
        return route()
                .POST("/auth/signup", handler::registerUser)
                .POST("/auth/signing", handler::authenticateUser)
                .GET("/users/users", userHandler::listUsers)
                .build();
    }
}
