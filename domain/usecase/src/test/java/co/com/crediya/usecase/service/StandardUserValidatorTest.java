package co.com.crediya.usecase.service;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.InvalidUserDataException;
import co.com.crediya.usecase.command.registeruser.UserTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static co.com.crediya.usecase.service.UserConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardUserValidatorTest {

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StandardUserValidator();
    }

    @Test
    void shouldPassValidationForValidUser() {
        // Arrange
        User validUser = new UserTestDataBuilder().build();

        // Act
        Mono<User> result = validator.validate(validUser);

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenNameIsMissing() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withName(null).build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidUserDataException
                                && ((InvalidUserDataException) throwable).getErrors().contains(String.format(ERROR_FIELD_REQUIRED, FIELD_NAME)))
                .verify();
    }

    @Test
    void shouldFailWhenEmailFormatIsInvalid() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withEmail("john.doe@invalid").build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidUserDataException
                                && ((InvalidUserDataException) throwable).getErrors().contains(ERROR_INVALID_EMAIL_FORMAT))
                .verify();
    }

    @Test
    void shouldFailWhenSalaryIsOutOfRange() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withBaseSalary(new BigDecimal("20000000")).build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidUserDataException
                                && ((InvalidUserDataException) throwable).getErrors().contains(ERROR_SALARY_OUT_OF_RANGE))
                .verify();
    }

    @Test
    void shouldReturnMultipleErrorsForMultipleInvalidFields() {
        // Arrange
        User invalidUser = new UserTestDataBuilder()
                .withName("")
                .withLastName(null)
                .withEmail("invalid-email")
                .withBaseSalary(new BigDecimal("-100"))
                .build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof InvalidUserDataException);
                    List<String> errors = ((InvalidUserDataException) throwable).getErrors();
                    assertEquals(4, errors.size());
                    assertTrue(errors.contains(String.format(ERROR_FIELD_REQUIRED, FIELD_NAME)));
                    assertTrue(errors.contains(String.format(ERROR_FIELD_REQUIRED, FIELD_LAST_NAME)));
                    assertTrue(errors.contains(ERROR_INVALID_EMAIL_FORMAT));
                    assertTrue(errors.contains(ERROR_SALARY_OUT_OF_RANGE));
                })
                .verify();
    }
}
