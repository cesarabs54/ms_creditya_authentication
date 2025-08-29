package co.com.bancolombia.usecase;


import reactor.core.publisher.Mono;

public interface GetByIdUseCase<T, I> {

    Mono<T> getById(I id);
}
