package co.com.bancolombia.model.exceptions;

public class InvalidRoleException extends DomainException {

    public InvalidRoleException(String message) {
        super(message);
    }

    public static InvalidRoleException unknownValue(String role) {
        return new InvalidRoleException("Rol inválido: " + role);
    }

}
