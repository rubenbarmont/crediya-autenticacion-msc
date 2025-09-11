package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.exceptions.IdentityDocumentAlreadyExistsException;
import co.com.crediya.model.user.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.databuilder.RolTestDataBuilder;
import co.com.crediya.usecase.service.PasswordEncoderGateway;
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
    @Mock
    private PasswordEncoderGateway passwordEncoderGateway;
    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        var userToRegister = new UserTestDataBuilder().build();
        var rol = new RolTestDataBuilder().build();
        var savedUser = new UserTestDataBuilder().withIdUser(100L).build();

        when(rolRepository.findById(any(Long.class))).thenReturn(Mono.just(rol));
        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(userToRegister));
        when(userRepository.existsByEmail(any(String.class))).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(any(Long.class))).thenReturn(Mono.just(false));
        when(passwordEncoderGateway.encode(any(String.class))).thenReturn(Mono.just("encodedPassword"));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getIdUser().equals(100L))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        // Arrange
        var userToRegister = new UserTestDataBuilder().build();
        var rol = new RolTestDataBuilder().build();

        when(rolRepository.findById(any(Long.class))).thenReturn(Mono.just(rol));
        when(userValidator.validate(any(User.class))).thenReturn(Mono.just(userToRegister));
        when(userRepository.existsByEmail(any(String.class))).thenReturn(Mono.just(true)); // Email ya existe
        when(userRepository.existsByIdentityDocument(any(Long.class))).thenReturn(Mono.just(false));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }
}
