package co.com.bancolombia.usecase;

import reactor.core.publisher.Mono;

public interface UpdateUseCase<T> {
    Mono<T> update(T entity);
}
