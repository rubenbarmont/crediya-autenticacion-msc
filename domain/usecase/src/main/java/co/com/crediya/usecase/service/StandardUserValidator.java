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

import static co.com.crediya.usecase.service.UserConstants.*;

public class StandardUserValidator implements UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public Mono<User> validate(User user) {
        List<String> errors = Stream.of(
                        validateIdentityDocument(user.getIdentityDocument()),
                        validateRequiredField(user.getName(), FIELD_NAME),
                        validateRequiredField(user.getLastName(), FIELD_LAST_NAME),
                        validateEmail(user.getEmail()),
                        validateSalary(user.getBaseSalary())
                )
                .flatMap(Optional::stream)
                .toList();

        if (!errors.isEmpty()) {
            return Mono.error(new InvalidUserDataException(errors));
        }
        return Mono.just(user);
    }

    private Optional<String> validateIdentityDocument(Long identityDocument) {
        if (Objects.isNull(identityDocument)) {
            return Optional.of(String.format(ERROR_FIELD_REQUIRED, FIELD_IDENTITY_DOCUMENT));
        }
        return Optional.empty();
    }


    private Optional<String> validateRequiredField(String value, String fieldName) {
        return (Objects.isNull(value) || value.isBlank())
                ? Optional.of(String.format(ERROR_FIELD_REQUIRED, fieldName))
                : Optional.empty();
    }

    private Optional<String> validateEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            return Optional.of(String.format(ERROR_FIELD_REQUIRED, FIELD_EMAIL));
        }
        return !EMAIL_PATTERN.matcher(email).matches()
                ? Optional.of(ERROR_INVALID_EMAIL_FORMAT)
                : Optional.empty();
    }

    private Optional<String> validateSalary(BigDecimal salary) {
        if (Objects.isNull(salary)) {
            return Optional.of(String.format(ERROR_FIELD_REQUIRED, FIELD_BASE_SALARY));
        }
        return (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0)
                ? Optional.of(ERROR_SALARY_OUT_OF_RANGE)
                : Optional.empty();
    }
}