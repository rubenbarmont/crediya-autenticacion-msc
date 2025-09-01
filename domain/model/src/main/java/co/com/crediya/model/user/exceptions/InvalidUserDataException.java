package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.exceptions.BusinessException;
import java.util.List;

public class InvalidUserDataException extends BusinessException {

    private final List<String> errors;

    public InvalidUserDataException(List<String> errors) {
        super("Los datos del usuario son inválidos.");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}