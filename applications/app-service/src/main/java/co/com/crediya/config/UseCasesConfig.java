package co.com.crediya.config;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.query.checkuser.CheckUserExistenceUseCase;
import co.com.crediya.usecase.query.finduser.FindUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import co.com.crediya.usecase.command.login.LoginUseCase;

@Configuration
public class UseCasesConfig {

        @Bean
        public UserValidator userValidator() {
                return new StandardUserValidator();
        }

        @Bean
        public RegisterUserUseCase registerUserUseCase(
                UserValidator userValidator,
                UserRepository userRepository,
                PasswordEncoderGateway passwordEncoderGateway) { // <-- CAMBIAR LA DEPENDENCIA
                return new RegisterUserUseCase(userValidator, userRepository, passwordEncoderGateway); // <-- PASARLA AL CONSTRUCTOR
        }

        @Bean
        public CheckUserExistenceUseCase checkUserExistenceUseCase(UserRepository userRepository) {
                return new CheckUserExistenceUseCase(userRepository);
        }

        // --- NUEVO BEAN AÑADIDO ---
        @Bean // <-- 2. DECLARAR EL BEAN
        public FindUserByIdentityDocumentUseCase findUserByIdentityDocumentUseCase(UserRepository userRepository) {
                // Spring ve que este caso de uso necesita un UserRepository,
                // busca un bean de ese tipo y lo inyecta automáticamente.
                return new FindUserByIdentityDocumentUseCase(userRepository);
        }

        // --- NUEVO BEAN PARA EL LOGINUSECASE ---
        @Bean
        public LoginUseCase loginUseCase(
                UserRepository userRepository,
                PasswordVerifier passwordVerifier,
                TokenManager tokenManager,
                RolRepository rolRepository) { // <-- AÑADIR RolRepository
                return new LoginUseCase(userRepository, passwordVerifier, tokenManager, rolRepository); // <-- PASARLO AL CONSTRUCTOR
        }

}
