package co.com.bancolombia.r2dbc.helper;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.function.Function;

public abstract class ReactiveAdapterOperations<E, D, I, R extends R2dbcRepository<D, I>> {

    private final Class<D> dataClass;
    private final Function<D, E> toEntityFn;
    protected R repository;
    protected ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    protected ReactiveAdapterOperations(R repository, ObjectMapper mapper, Function<D, E> toEntityFn) {
        this.repository = repository;
        this.mapper = mapper;
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.dataClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];
        this.toEntityFn = toEntityFn;
    }

    protected D toData(E entity) {
        return mapper.map(entity, dataClass);
    }

    protected E toEntity(D data) {
        return data != null ? toEntityFn.apply(data) : null;
    }

    public Mono<E> save(E entity) {
        return saveData(toData(entity))
                .map(this::toEntity);
    }

    protected Flux<E> saveAllEntities(Flux<E> entities) {
        return saveData(entities.map(this::toData))
                .map(this::toEntity);
    }

    protected Mono<D> saveData(D data) {
        return repository.save(data);
    }

    protected Flux<D> saveData(Flux<D> data) {
        return repository.saveAll(data);
    }

    public Mono<E> findById(I id) {
        return repository.findById(id).map(this::toEntity);
    }

    public Flux<E> findByExample(E entity) {
        return repository.findAll(Example.of(toData(entity)))
                .map(this::toEntity);
    }

    public Flux<E> findAll() {
        return repository.findAll()
                .map(this::toEntity);
    }

    public Mono<Void> deleteById(I id) {
        return repository.deleteById(id);
    }

    public Mono<Void> delete(E entity) {
        return repository.delete(toData(entity));
    }

    public Mono<E> update(E entity) {
        var data = toData(entity);
        invokeSetAsNotNewIfExists(data);
        return saveData(data).map(this::toEntity);
    }

    @SuppressWarnings("all")
    private void invokeSetAsNotNewIfExists(D data) {
        try {
            var method = data.getClass().getMethod("setAsNotNew");
            method.invoke(data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SetAsNotNewException(
                    "Error invocando setAsNotNew en " + data.getClass().getName(),
                    e);
        } catch (NoSuchMethodException ignored) {
            // No se requiere acción ya que 'setAsNotNew' no existe en este caso
        }
    }

    static class SetAsNotNewException extends RuntimeException {
        public SetAsNotNewException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
