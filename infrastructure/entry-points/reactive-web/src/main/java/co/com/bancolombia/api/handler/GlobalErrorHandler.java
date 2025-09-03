package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.responses.ErrorMessage;
import co.com.bancolombia.model.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
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

        /*
         * 400 - Errores de validación y binding
         * - MethodArgumentTypeMismatchException: tipo de argumento incorrecto
         * - ConstraintViolationException: viola Bean Validation
         * - BindException: errores al mapear campos
         */

        switch (e) {
            // 400 - El tipo del argumento no coincide con el esperado (p.ej., se esperaba Integer y llegó String)
            case MethodArgumentTypeMismatchException matme -> {
                status = HttpStatus.BAD_REQUEST;
                message = "Tipo de argumento inválido: " + matme.getName();
            }
            // 400 - Violaciones de validaciones por anotaciones (Bean Validation), con detalle de campos
            case ConstraintViolationException cve -> {
                status = HttpStatus.BAD_REQUEST;
                message = cve.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));
            }
            // 400 - Errores de binding al mapear parámetros/cuerpo a un objeto (incluye mensajes por campo)
            case BindException be -> {
                status = HttpStatus.BAD_REQUEST;
                message = be.getBindingResult().getFieldErrors().stream()
                        .map(err -> err.getField() + ": " + err.getDefaultMessage())
                        .collect(Collectors.joining("; "));
            }
            // Propaga el estado HTTP indicado explícitamente en la excepción
            case ResponseStatusException rse -> {
                status = (HttpStatus) rse.getStatusCode();
                message = rse.getReason() != null ? rse.getReason() : "Error de estado HTTP";
            }
            // 400 - Argumento inválido (programación/validación básica)
            case IllegalArgumentException iae -> {
                status = HttpStatus.BAD_REQUEST;
                message = iae.getMessage() != null ? iae.getMessage() : "Argumento inválido";
            }
            // 409 - Recurso duplicado (conflicto), p.ej. usuario ya existente
            case DuplicateResourceException dre -> {
                status = HttpStatus.CONFLICT;
                message = dre.getMessage();
            }
            // 400 - Rol inválido en la operación (dominio/negocio)
            case InvalidRoleException ire -> {
                status = HttpStatus.BAD_REQUEST; // 400
                message = ire.getMessage();
            }
            // 404 - Recurso no encontrado en el dominio
            case ResourceNotFoundException rnfe -> {
                status = HttpStatus.NOT_FOUND;
                message = rnfe.getMessage();
            }
            // 401 - Errores de autenticación
            case AuthenticationException ae -> {
                status = HttpStatus.UNAUTHORIZED;
                message = ae.getMessage();
            }
            // 400 - Excepciones de dominio no mapeadas específicamente
            case DomainException de -> {
                status = HttpStatus.BAD_REQUEST;
                message = de.getMessage();
            }
            // 500 - Cualquier otro error no controlado
            default -> {
                log.error("Unhandled exception", e);
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Sin detalle");
            }
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
