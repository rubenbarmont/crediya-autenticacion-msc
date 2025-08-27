package co.com.crediya.model.user.excepcion;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
