package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.exceptions.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {

    public EmailAlreadyExistsException(String email) {
        super(String.format("El correo electrónico '%s' ya se encuentra registrado.", email));
    }
}