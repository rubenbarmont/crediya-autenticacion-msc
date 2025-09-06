package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.exceptions.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("El correo electrónico o la contraseña son incorrectos.");
    }
}