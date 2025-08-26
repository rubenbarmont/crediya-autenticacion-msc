package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @Mock
    private MyReactiveRepository repository; // El repositorio de Spring Data que mockeamos

    @Mock
    private ObjectMapper mapper; // El mapper que usa tu clase genérica

    @InjectMocks
    private MyReactiveRepositoryAdapter adapter; // La clase que estamos probando

    private User userDomain;
    private UserData userData;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Creamos objetos de prueba que usaremos en varios tests
        userData = UserData.builder()
                .id(userId)
                .name("Test")
                .lastName("User")
                .email("test@crediya.com")
                .birthDate(LocalDate.now().minusYears(30))
                .baseSalary(new BigDecimal("5000000"))
                .build();

        userDomain = User.builder()
                .id(userId)
                .name("Test")
                .lastName("User")
                .email("test@crediya.com")
                .birthDate(LocalDate.now().minusYears(30))
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    @Test
    void saveShouldReturnSavedUser() {
        // Arrange
        // Cuando se llame al mapper para convertir de dominio a datos, devolvemos nuestro UserData
        when(mapper.map(any(User.class), any(Class.class))).thenReturn(userData);
        // Cuando se llame al repositorio para guardar, devolvemos el UserData guardado
        when(repository.save(any(UserData.class))).thenReturn(Mono.just(userData));
        // Cuando se llame al mapper para convertir de datos a dominio, devolvemos nuestro User de dominio
        when(mapper.mapBuilder(any(UserData.class), any(Class.class))).thenReturn(userDomain.toBuilder());

        // Act
        Mono<User> result = adapter.save(userDomain);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getId().equals(userId))
                .verifyComplete();
    }

    @Test
    void findByEmailShouldReturnUserWhenFound() {
        // Arrange
        when(repository.findByEmail("test@crediya.com")).thenReturn(Mono.just(userData));
        when(mapper.mapBuilder(any(UserData.class), any(Class.class))).thenReturn(userDomain.toBuilder());

        // Act
        Mono<User> result = adapter.findByEmail("test@crediya.com");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(foundUser -> foundUser.getEmail().equals("test@crediya.com"))
                .verifyComplete();
    }

    @Test
    void findByEmailShouldReturnEmptyWhenNotFound() {
        // Arrange
        when(repository.findByEmail("notfound@crediya.com")).thenReturn(Mono.empty());

        // Act
        Mono<User> result = adapter.findByEmail("notfound@crediya.com");

        // Assert
        StepVerifier.create(result)
                .verifyComplete(); // Verificamos que el flujo termina vacío
    }
}
