
package co.com.crediya.api.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    /**
     * Valida un DTO de forma genérica. Si es válido, devuelve el mismo DTO.
     * Si es inválido, devuelve un Mono.error con la excepción apropiada.
     * @param dto El objeto DTO a validar.
     * @param <T> El tipo del DTO.
     * @return Un Mono que emite el DTO si es válido, o un Mono de error si no lo es.
     */
    public <T> Mono<T> validate(T dto) {
        if (dto == null) {
            return Mono.error(new ServerWebInputException("Request body cannot be null"));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (violations.isEmpty()) {
            return Mono.just(dto);
        }

        String errors = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));

        return Mono.error(new ServerWebInputException(errors));
    }
}