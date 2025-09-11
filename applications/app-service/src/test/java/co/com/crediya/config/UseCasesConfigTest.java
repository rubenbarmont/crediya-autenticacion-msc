package co.com.crediya.config;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.login.LoginUseCase;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.query.checkuser.CheckUserExistenceUseCase;
import co.com.crediya.usecase.query.finduser.FindUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.query.finduserbyid.FindUserByIdUseCase;
import co.com.crediya.usecase.service.PasswordEncoderGateway;
import co.com.crediya.usecase.service.PasswordVerifier;
import co.com.crediya.usecase.service.TokenManager;
import co.com.crediya.usecase.service.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCasesConfigTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        // Arrange: Creamos el contexto usando nuestra configuración de prueba
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @AfterEach
    void tearDown() {
        // Limpiamos el contexto después de cada test
        context.close();
    }

    @Test
    void shouldCreateRegisterUserUseCaseBean() {
        // Act & Assert
        RegisterUserUseCase useCase = context.getBean(RegisterUserUseCase.class);
        assertNotNull(useCase, "El bean de RegisterUserUseCase no debería ser nulo.");
    }

    @Test
    void shouldCreateLoginUseCaseBean() {
        // Act & Assert
        LoginUseCase useCase = context.getBean(LoginUseCase.class);
        assertNotNull(useCase, "El bean de LoginUseCase no debería ser nulo.");
    }

    @Test
    void shouldCreateFindUserByIdUseCaseBean() {
        // Act & Assert
        FindUserByIdUseCase useCase = context.getBean(FindUserByIdUseCase.class);
        assertNotNull(useCase, "El bean de FindUserByIdUseCase no debería ser nulo.");
    }

    @Test
    void shouldCreateCheckUserExistenceUseCaseBean() {
        // Act & Assert
        CheckUserExistenceUseCase useCase = context.getBean(CheckUserExistenceUseCase.class);
        assertNotNull(useCase, "El bean de CheckUserExistenceUseCase no debería ser nulo.");
    }

    @Test
    void shouldCreateFindUserByIdentityDocumentUseCaseBean() {
        // Act & Assert
        FindUserByIdentityDocumentUseCase useCase = context.getBean(FindUserByIdentityDocumentUseCase.class);
        assertNotNull(useCase, "El bean de FindUserByIdentityDocumentUseCase no debería ser nulo.");
    }


    // --- Nuestra Configuración de Prueba anidada ---
    @Configuration
    @Import(UseCasesConfig.class) // Importamos la configuración real de la aplicación
    static class TestConfig {

        // Creamos Mocks para TODAS las dependencias que los UseCases necesitan.
        // Spring usará estos mocks para satisfacer las dependencias.

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public RolRepository rolRepository() {
            return Mockito.mock(RolRepository.class);
        }

        @Bean
        public UserValidator userValidator() {
            return Mockito.mock(UserValidator.class);
        }

        @Bean
        public PasswordEncoderGateway passwordEncoderGateway() {
            return Mockito.mock(PasswordEncoderGateway.class);
        }

        @Bean
        public PasswordVerifier passwordVerifier() {
            return Mockito.mock(PasswordVerifier.class);
        }

        @Bean
        public TokenManager tokenManager() {
            return Mockito.mock(TokenManager.class);
        }
    }
}