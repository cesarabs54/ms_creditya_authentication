package co.com.bancolombia.model.exceptions;

public class AuthenticationException extends DomainException {

    public AuthenticationException(String message) {
        super(message);
    }

    public static AuthenticationException emailNotFound() {
        return new AuthenticationException("Incorrect information");
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Credentials invalid");
    }

}
