package co.com.crediya.usecase.service;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.InvalidUserDataException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StandardUserValidator implements UserValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public Mono<User> validate(User user) {
        List<String> errors = Stream.of(
                        validateRequiredField(user.getName(), "nombres"),
                        validateRequiredField(user.getLastName(), "apellidos"),
                        validateEmail(user.getEmail()),
                        validateSalary(user.getBaseSalary())
                )
                .flatMap(Optional::stream) // Convierte Stream<Optional<String>> a Stream<String>
                .toList(); // Requiere Java 16+. Si usas una versión anterior, usa .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            return Mono.error(new InvalidUserDataException(errors));
        }
        return Mono.just(user);
    }

    private Optional<String> validateRequiredField(String value, String fieldName) {
        return (Objects.isNull(value) || value.isBlank())
                ? Optional.of(String.format("El campo '%s' es obligatorio.", fieldName))
                : Optional.empty();
    }

    private Optional<String> validateEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            return Optional.of("El campo 'correo_electronico' es obligatorio.");
        }
        return !EMAIL_PATTERN.matcher(email).matches()
                ? Optional.of("El formato del correo electrónico no es válido.")
                : Optional.empty();
    }

    private Optional<String> validateSalary(BigDecimal salary) {
        if (Objects.isNull(salary)) {
            return Optional.of("El campo 'salario_base' es obligatorio.");
        }
        BigDecimal minSalary = BigDecimal.ZERO;
        BigDecimal maxSalary = new BigDecimal("15000000");
        return (salary.compareTo(minSalary) < 0 || salary.compareTo(maxSalary) > 0)
                ? Optional.of("El salario base debe estar entre 0 y 15,000,000.")
                : Optional.empty();
    }
}