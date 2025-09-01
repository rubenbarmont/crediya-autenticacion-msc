package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
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
        User sampleUser = new UserTestDataBuilder().withEmail("jane.doe@example.com").build();
        User savedUser = sampleUser.toBuilder().idUser(1L).build();

        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act
        Mono<User> result = registerUserUseCase.execute(sampleUser);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getIdUser() == 1L && user.getName().equals("John"))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        // Arrange
        User sampleUser = new UserTestDataBuilder().withEmail("existing@example.com").build();

        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(userRepository.existsByEmail(sampleUser.getEmail())).thenReturn(Mono.just(true));

        // Act
        Mono<User> result = registerUserUseCase.execute(sampleUser);

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
