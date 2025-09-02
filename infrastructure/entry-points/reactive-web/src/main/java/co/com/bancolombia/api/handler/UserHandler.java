package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.responses.UserResponse;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.usecase.GetAllUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final GetAllUseCase<User> getAllUseCase;

    public Mono<ServerResponse> listUsers(ServerRequest request) {
        Flux<UserResponse> users = getAllUseCase.getAll()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getDocumentIdentification(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthDate(),
                        user.getAddress(),
                        user.getTelephoneNumber(),
                        user.getEmail(),
                        user.getBaseSalary(),
                        user.getRolesAsString()
                ));

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(users, UserResponse.class));
    }

}
