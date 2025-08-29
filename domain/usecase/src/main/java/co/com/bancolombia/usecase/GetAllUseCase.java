package co.com.bancolombia.usecase;



import reactor.core.publisher.Flux;

public interface GetAllUseCase<T> {

    Flux<T> getAll();
}
