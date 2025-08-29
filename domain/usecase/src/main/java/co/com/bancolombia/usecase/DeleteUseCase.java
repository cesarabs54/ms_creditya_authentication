package co.com.bancolombia.usecase;


import reactor.core.publisher.Mono;

public interface DeleteUseCase<T> {

    Mono<Void> delete(T entity);
}
