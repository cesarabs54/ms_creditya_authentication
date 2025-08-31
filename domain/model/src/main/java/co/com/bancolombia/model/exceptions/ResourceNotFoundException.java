package co.com.bancolombia.model.exceptions;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException roleNotFound(String roleName) {
        return new ResourceNotFoundException("Rol no encontrado: " + roleName);
    }

    public static ResourceNotFoundException defaultRoleMissing(String roleName) {
        return new ResourceNotFoundException("No se encontró el rol " + roleName);
    }

}
