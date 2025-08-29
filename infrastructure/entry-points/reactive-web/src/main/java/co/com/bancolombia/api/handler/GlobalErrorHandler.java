package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.responses.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@Order(-2) // Se registra antes del handler por defecto de Spring Boot
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorHandler(ApplicationContext applicationContext,
                              ServerCodecConfigurer codecConfigurer,
                              WebProperties webProperties) {
        super(defaultErrorAttributes(), webProperties.getResources(), applicationContext);
        super.setMessageWriters(codecConfigurer.getWriters());
        super.setMessageReaders(codecConfigurer.getReaders());
    }

    private static ErrorAttributes defaultErrorAttributes() {
        return new DefaultErrorAttributes();
    }

    @Override
    @NonNull
    protected RouterFunction<ServerResponse> getRoutingFunction(
            @NonNull ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable e = getError(request);

        HttpStatus status;
        String message;

        if (e instanceof MethodArgumentTypeMismatchException matme) {
            status = HttpStatus.BAD_REQUEST;
            message = "Tipo de argumento inválido: " + matme.getName();

        } else if (e instanceof ConstraintViolationException cve) {
            status = HttpStatus.BAD_REQUEST;
            message = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));

        } else if (e instanceof BindException be) {
            status = HttpStatus.BAD_REQUEST;
            message = be.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining("; "));

        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Error interno del servidor: " + (e.getMessage() != null ? e.getMessage()
                    : "Sin detalle");
        }

        ErrorMessage body = new ErrorMessage(
                status.value(),
                LocalDateTime.now(),
                message,
                request.path()
        );

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body));
    }

}
