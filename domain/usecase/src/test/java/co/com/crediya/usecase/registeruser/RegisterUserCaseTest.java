package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.excepcion.DomainException;
import co.com.crediya.model.user.excepcion.EmailAlreadyExistsException;
import co.com.crediya.model.user.gateways.LoggerPort;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoggerPort logger;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void registerUserSuccessfully() {
        // Arrange
        User userToRegister = User.builder()
                .name("Robert")
                .lastName("Martin")
                .email("uncle.bob@example.com")
                .baseSalary(new BigDecimal("10000"))
                .birthDate(LocalDate.now().minusYears(40))
                .build();

        User savedUser = userToRegister.toBuilder().id(UUID.randomUUID()).build();

        // Mockeamos el comportamiento de los puertos
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId() != null && user.getEmail().equals(userToRegister.getEmail()))
                .verifyComplete();
    }

    @Test
    void registerUserFailsWhenEmailAlreadyExists() {
        // Arrange
        User userToRegister = User.builder().email("exists@example.com").name("a").lastName("b").baseSalary(BigDecimal.ONE).build();
        User existingUser = userToRegister.toBuilder().id(UUID.randomUUID()).build();

        when(userRepository.findByEmail("exists@example.com")).thenReturn(Mono.just(existingUser));

        // Act
        Mono<User> result = registerUserUseCase.execute(userToRegister);

        // Assert
        StepVerifier.create(result)
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }

    @Test
    void registerUserFailsWhenValidationFails() {
        // Arrange: Usuario con nombre nulo, que hará fallar el UserValidator
        User invalidUser = User.builder().name(null).lastName("a").email("test@test.com").baseSalary(BigDecimal.ONE).build();

        // Act
        Mono<User> result = registerUserUseCase.execute(invalidUser);

        // Assert
        StepVerifier.create(result)
                .expectError(DomainException.class) // Esperamos la excepción que lanza el validador
                .verify();
    }
}