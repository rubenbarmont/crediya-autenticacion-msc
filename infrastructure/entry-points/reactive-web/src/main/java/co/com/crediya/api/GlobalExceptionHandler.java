// path: infrastructure/entry-points/reactive-web/src/main/java/co/com/crediya/api/GlobalExceptionHandler.java
package co.com.crediya.api;

import co.com.crediya.api.dto.ApiErrorResponse;
import co.com.crediya.model.user.excepcion.EmailAlreadyExistsException;
import co.com.crediya.model.user.gateways.LoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LoggerPort logger;

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiErrorResponse> handleWebInputException(ServerWebInputException ex) {
        logger.info("Invalid request received: {}", ex.getReason());
        var apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getReason());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailExistsException(EmailAlreadyExistsException ex) {
        logger.info("Business validation failed: {}", ex.getMessage());
        var apiError = new ApiErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        logger.error("An unexpected internal server error occurred", ex);
        var apiError = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno inesperado.");
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Y así para cualquier otra excepción específica que queramos controlar...
}