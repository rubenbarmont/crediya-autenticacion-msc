package co.com.crediya.usecase.query.checkuser;

import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckUserExistenceUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CheckUserExistenceUseCase useCase;

    @Test
    void shouldReturnTrueWhenDocumentExists() {
        // Arrange
        Long document = 123L;
        when(userRepository.existsByIdentityDocument(document)).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = useCase.byIdentityDocument(document);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Arrange
        String email = "test@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = useCase.byEmail(email);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
