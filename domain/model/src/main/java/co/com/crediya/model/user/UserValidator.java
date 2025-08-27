package co.com.crediya.model.user;


import co.com.crediya.model.user.excepcion.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    private UserValidator() {}

    public static void validate(User user) {
        Objects.requireNonNull(user, "User cannot be null");

        if (isNullOrEmpty(user.getName())) {
            throw new DomainException("The 'name' field is required.");
        }
        if (isNullOrEmpty(user.getLastName())) {
            throw new DomainException("The 'lastName' field is required.");
        }
        if (isNullOrEmpty(user.getEmail())) {
            throw new DomainException("The 'email' field is required.");
        }
        if (user.getBaseSalary() == null) {
            throw new DomainException("The 'baseSalary' field is required.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new DomainException("The email format is invalid.");
        }
        if (user.getBaseSalary().compareTo(MIN_SALARY) < 0 || user.getBaseSalary().compareTo(MAX_SALARY) > 0) {
            throw new DomainException("The base salary must be between " + MIN_SALARY + " and " + MAX_SALARY + ".");
        }

        if (user.getBirthDate() != null && user.getBirthDate().isAfter(LocalDate.now())) {
            throw new DomainException("The birth date cannot be in the future.");
        }
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
