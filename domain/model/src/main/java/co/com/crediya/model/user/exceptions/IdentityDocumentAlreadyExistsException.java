package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.exceptions.BusinessException;

public class IdentityDocumentAlreadyExistsException extends BusinessException {

    public IdentityDocumentAlreadyExistsException(Long identityDocument) {
        super(String.format("El documento de identidad '%d' ya se encuentra registrado.", identityDocument));
    }
}
