package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.exceptions.IdentityDocumentAlreadyExistsException;
import co.com.crediya.model.user.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.service.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        User userToRegister = new UserTestDataBuilder().build();
        User userWithId = new UserTestDataBuilder().withId(1L).build();

        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(userToRegister));
        when(userRepository.existsByIdentityDocument(any(Long.class))).thenReturn(Mono.just(false));
        when(userRepository.existsByEmail(any(String.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userWithId));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getIdUser() == 1L)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenIdentityDocumentExists() {
        // Arrange
        User userToRegister = new UserTestDataBuilder().build();

        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(userToRegister));
        when(userRepository.existsByIdentityDocument(any(Long.class))).thenReturn(Mono.just(true));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectError(IdentityDocumentAlreadyExistsException.class)
                .verify();
    }

    @Test
    void shouldFailWhenEmailExists() {
        // Arrange
        User userToRegister = new UserTestDataBuilder().build();

        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(userToRegister));
        when(userRepository.existsByIdentityDocument(any(Long.class))).thenReturn(Mono.just(false));
        when(userRepository.existsByEmail(any(String.class))).thenReturn(Mono.just(true));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }

    @Test
    void shouldFailWhenValidationFails() {
        // Arrange
        User sampleUser = new UserTestDataBuilder().withName(null).build();
        InvalidUserDataException validationError = new InvalidUserDataException(Collections.singletonList("Error de validación"));

        when(userValidator.validate(any(User.class))).thenReturn(Mono.error(validationError));

        // Act
        Mono<User> result = registerUserUseCase.execute(sampleUser);

        // Assert
        StepVerifier.create(result)
                .expectError(InvalidUserDataException.class)
                .verify();
    }
}
