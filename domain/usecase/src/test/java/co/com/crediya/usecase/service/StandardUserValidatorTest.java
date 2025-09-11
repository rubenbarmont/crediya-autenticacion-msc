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

    private StandardUserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StandardUserValidator();
    }

    @Test
    void shouldPassValidationForValidUser() {
        // Arrange
        var validUser = new UserTestDataBuilder().build();

        // Act
        Mono<User> result = validator.validate(validUser);

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        // Arrange
        var invalidUser = new UserTestDataBuilder().withEmail("invalid-email").build();

        // Act
        Mono<User> result = validator.validate(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectError(InvalidUserDataException.class)
                .verify();
    }
}
