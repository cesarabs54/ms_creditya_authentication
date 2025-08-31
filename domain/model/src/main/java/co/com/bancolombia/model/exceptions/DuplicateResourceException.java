package co.com.bancolombia.model.exceptions;

public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException emailInUse(String email) {
        return new DuplicateResourceException("El correo ya está en uso: " + email);
    }

    public static DuplicateResourceException documentInUse(String document) {
        return new DuplicateResourceException("El número de documento ya existe: " + document);
    }

}
