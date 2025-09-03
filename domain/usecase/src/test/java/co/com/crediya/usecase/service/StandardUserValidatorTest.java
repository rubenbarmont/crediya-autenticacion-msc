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
    void shouldFailWhenIdentityDocumentIsNull() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withIdentityDocument(null).build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidUserDataException
                        && ((InvalidUserDataException) throwable).getErrors().get(0).contains(FIELD_IDENTITY_DOCUMENT))
                .verify();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withName(" ").build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidUserDataException
                        && ((InvalidUserDataException) throwable).getErrors().get(0).contains(FIELD_NAME))
                .verify();
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
    void shouldFailWhenEmailIsInvalidFormat() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withEmail("invalid-email").build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidUserDataException
                        && ((InvalidUserDataException) throwable).getErrors().contains(ERROR_INVALID_EMAIL_FORMAT))
                .verify();
    }

    @Test
    void shouldFailWhenSalaryIsBelowMinimum() {
        // Arrange
        User invalidUser = new UserTestDataBuilder().withBaseSalary(new BigDecimal("-1")).build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof InvalidUserDataException
                        && ((InvalidUserDataException) throwable).getErrors().contains(ERROR_SALARY_OUT_OF_RANGE))
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
    void shouldReturnMultipleErrors() {
        // Arrange
        User completelyInvalidUser = new User(); // Todos los campos son nulos

        // Act
        Mono<User> result = validator.validate(completelyInvalidUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof InvalidUserDataException);
                    List<String> errors = ((InvalidUserDataException) throwable).getErrors();
                    assertEquals(5, errors.size());
                    assertTrue(errors.get(0).contains(FIELD_IDENTITY_DOCUMENT));
                    assertTrue(errors.get(1).contains(FIELD_NAME));
                })
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
