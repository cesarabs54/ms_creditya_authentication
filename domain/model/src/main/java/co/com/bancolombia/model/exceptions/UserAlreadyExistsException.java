package co.com.bancolombia.model.exceptions;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String documentIdentification) {
        super("El usuario con identificación " + documentIdentification + " ya existe.");
    }
}
