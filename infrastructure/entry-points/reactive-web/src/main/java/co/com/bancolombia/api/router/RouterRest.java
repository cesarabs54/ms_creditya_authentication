package co.com.bancolombia.api.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import co.com.bancolombia.api.dto.requests.LogOutRequest;
import co.com.bancolombia.api.dto.requests.LoginRequest;
import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.dto.requests.TokenRefreshRequest;
import co.com.bancolombia.api.dto.responses.JwtResponse;
import co.com.bancolombia.api.dto.responses.MessageResponse;
import co.com.bancolombia.api.dto.responses.TokenRefreshResponse;
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

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/auth/login",
                    beanClass = AuthHandler.class,
                    beanMethod = "authenticateUser",
                    operation = @Operation(
                            summary = "Autenticar usuario",
                            description = "Valida credenciales y genera JWT y Refresh Token",
                            requestBody = @RequestBody(
                                    description = "Credenciales de login",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "JWT generado",
                                            content = @Content(schema = @Schema(implementation = JwtResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
                            }
                    )
            ),
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
            ),
            @RouterOperation(
                    path = "/auth/refresh",
                    beanClass = AuthHandler.class,
                    beanMethod = "refreshToken",
                    operation = @Operation(
                            summary = "Refrescar token",
                            description = "Genera un nuevo Access Token usando el Refresh Token",
                            requestBody = @RequestBody(
                                    description = "Datos para refrescar el token",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = TokenRefreshRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Nuevo token generado",
                                            content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "403", description = "Refresh token inválido o expirado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/auth/logout",
                    beanClass = AuthHandler.class,
                    beanMethod = "logoutUser",
                    operation = @Operation(
                            summary = "Cerrar sesión",
                            description = "Invalida el refresh token del usuario",
                            requestBody = @RequestBody(
                                    description = "Datos para logout",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = LogOutRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Logout exitoso",
                                            content = @Content(schema = @Schema(implementation = MessageResponse.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler handler) {
        return route()
                .POST("/auth/login", handler::authenticateUser)
                .POST("/auth/signup", handler::registerUser)
                .POST("/auth/refresh", handler::refreshToken)
                .POST("/auth/logout", handler::logoutUser)
                .build();
    }
}
