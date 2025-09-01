package co.com.bancolombia.usecase;

import co.com.bancolombia.model.entities.User;
import lombok.RequiredArgsConstructor;

import co.com.bancolombia.model.gateways.UserRepository;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllUsersUseCase implements GetAllUseCase<User> {

    private final UserRepository userRepository;

    @Override
    public Flux<User> getAll() {
        return userRepository.findAll();
    }

}
