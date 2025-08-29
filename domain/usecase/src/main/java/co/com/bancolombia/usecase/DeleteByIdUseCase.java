package co.com.bancolombia.usecase;

import reactor.core.publisher.Mono;

public interface DeleteByIdUseCase<I> {

    Mono<Void> deleteById(I id);
}
