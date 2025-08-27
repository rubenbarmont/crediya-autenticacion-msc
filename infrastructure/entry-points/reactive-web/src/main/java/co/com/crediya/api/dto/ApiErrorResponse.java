// path: infrastructure/entry-points/reactive-web/src/main/java/co/com/crediya/api/dto/ApiErrorResponse.java
package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
// Anotación clave: si un campo es nulo (como validationErrors), no se incluirá en el JSON de respuesta.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, String> validationErrors;

    // Constructor para errores generales (sin validación de campos)
    public ApiErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.validationErrors = null;
    }

    // Constructor para errores de validación de campos
    public ApiErrorResponse(HttpStatus status, String message, Map<String, String> validationErrors) {
        this.status = status.value();
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.validationErrors = validationErrors;
    }
}