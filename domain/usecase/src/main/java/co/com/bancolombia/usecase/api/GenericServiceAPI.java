package co.com.bancolombia.usecase.api;

import co.com.bancolombia.usecase.*;

import java.io.Serializable;

public interface GenericServiceAPI<T, I extends Serializable> extends GetByIdUseCase<T, I>,
        SaveUseCase<T>, GetAllUseCase<T>, DeleteUseCase<T>, DeleteByIdUseCase<I>, UpdateUseCase<T> {
}
