package co.com.crediya.config;

import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        // Arrange & Act
        // Creamos el contexto usando nuestra configuración de prueba
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);

        // Assert
        // 1. Hacemos una aserción más robusta: pedimos directamente el bean que nos interesa.
        // Si el contexto no puede crearlo, esta línea lanzará una excepción y el test fallará (lo cual es correcto).
        RegisterUserUseCase useCase = context.getBean(RegisterUserUseCase.class);

        // 2. Verificamos que el bean fue creado exitosamente.
        assertNotNull(useCase, "El bean de RegisterUserUseCase no debería ser nulo.");

        // Cerramos el contexto
        context.close();
    }

    // --- Nuestra Configuración de Prueba anidada ---
    @Configuration
    @Import(UseCasesConfig.class) // Importamos la configuración real de la aplicación
    static class TestConfig {

        // 3. ¡ESTA ES LA CLAVE! Creamos un Bean que es un mock del UserRepository.
        // Spring usará este mock para satisfacer la dependencia del RegisterUserUseCase.
        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }
}