package co.com.bancolombia.usecase;

import reactor.core.publisher.Mono;

public interface SaveUseCase<T> {

    Mono<T> save(T entity);
}
