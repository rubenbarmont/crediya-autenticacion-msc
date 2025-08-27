package co.com.crediya.api;

import co.com.crediya.model.user.excepcion.DomainException;
import co.com.crediya.model.user.excepcion.EmailAlreadyExistsException;
import co.com.crediya.model.user.gateways.LoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LoggerPort logger;

    // ... (otros manejadores) ...

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistsException(EmailAlreadyExistsException ex) {
        // LOG CONTROLADO: Registramos el evento de negocio como una línea de INFO.
        logger.info("Business validation failed: {}", ex.getMessage());
        // Devolvemos la respuesta HTTP 409 Conflict al cliente.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    // ... (otros manejadores para DataAccessException y Exception) ...
}
