package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.exceptions.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long identityDocument) {
        super(String.format("No se encontró un usuario con el documento de identidad '%d'.", identityDocument));
    }

    // --- NUEVO CONSTRUCTOR SOBRECARGADO ---
    public UserNotFoundException(Long id, String searchType) {
        super(String.format("No se encontró un usuario con el %s '%d'.", searchType, id));
    }
}
