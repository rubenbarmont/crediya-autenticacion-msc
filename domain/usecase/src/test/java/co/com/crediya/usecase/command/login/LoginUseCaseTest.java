package co.com.crediya.usecase.command.login;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.user.exceptions.InvalidCredentialsException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.databuilder.RolTestDataBuilder;
import co.com.crediya.usecase.command.registeruser.UserTestDataBuilder;
import co.com.crediya.usecase.service.PasswordVerifier;
import co.com.crediya.usecase.service.TokenManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordVerifier passwordVerifier;
    @Mock
    private TokenManager tokenManager;
    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void shouldLoginSuccessfullyAndReturnToken() {
        // Arrange
        var user = new UserTestDataBuilder().withPassword("hashedPassword").build();
        var rol = new RolTestDataBuilder().build();
        var token = "generated-jwt-token";

        when(userRepository.findByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordVerifier.verify("plainPassword", "hashedPassword")).thenReturn(Mono.just(true));
        when(rolRepository.findById(any(Long.class))).thenReturn(Mono.just(rol));
        when(tokenManager.generate(any())).thenReturn(Mono.just(token));

        // Act
        Mono<String> result = loginUseCase.execute("test@test.com", "plainPassword");

        // Assert
        StepVerifier.create(result)
                .expectNext(token)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenPasswordIsIncorrect() {
        // Arrange
        var user = new UserTestDataBuilder().withPassword("hashedPassword").build();

        when(userRepository.findByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordVerifier.verify("wrongPassword", "hashedPassword")).thenReturn(Mono.just(false));

        // Act
        Mono<String> result = loginUseCase.execute("test@test.com", "wrongPassword");

        // Assert
        StepVerifier.create(result)
                .expectError(InvalidCredentialsException.class)
                .verify();
    }
}
