package co.com.bancolombia.model.exceptions;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String code;

    protected DomainException(String message) {
        super(message);
        this.code = null;
    }

    protected DomainException(String message, String code) {
        super(message);
        this.code = code;
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
    }

}
