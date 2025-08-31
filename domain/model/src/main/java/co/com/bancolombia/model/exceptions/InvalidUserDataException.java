package co.com.bancolombia.model.exceptions;

public class InvalidUserDataException extends DomainException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
