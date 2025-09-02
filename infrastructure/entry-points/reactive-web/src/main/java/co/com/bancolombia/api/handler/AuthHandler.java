package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.requests.LoginRequest;
import co.com.bancolombia.api.dto.requests.SignUpRequest;
import co.com.bancolombia.api.dto.responses.JwtResponse;
import co.com.bancolombia.api.dto.responses.MessageResponse;
import co.com.bancolombia.api.util.RequestValidator;
import co.com.bancolombia.model.dtos.AuthRequest;
import co.com.bancolombia.model.dtos.RegisterRequest;
import co.com.bancolombia.usecase.AuthenticateUserUseCase;
import co.com.bancolombia.usecase.RegisterUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RequestValidator requestValidator;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> authenticateUser(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .flatMap(requestValidator::validate)
                .map(loginRequest -> objectMapper.convertValue(loginRequest, AuthRequest.class))
                .flatMap(authenticateUserUseCase::execute) // <-- Ejecuta el caso de uso
                .map(authResponse -> objectMapper.convertValue(authResponse, JwtResponse.class))
                .flatMap(jwtResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(jwtResponse));

    }

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(SignUpRequest.class)
                .flatMap(requestValidator::validate)
                .map(signUpRequest -> objectMapper.convertValue(signUpRequest,
                        RegisterRequest.class))
                .flatMap(registerUserUseCase::execute)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new MessageResponse("Usuario creado correctamente")));
    }

}
