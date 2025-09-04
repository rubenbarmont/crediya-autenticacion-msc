package co.com.crediya.config;

import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.query.checkuser.CheckUserExistenceUseCase;
import co.com.crediya.usecase.query.finduser.FindUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.service.StandardUserValidator;
import co.com.crediya.usecase.service.UserValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

        @Bean
        public UserValidator userValidator() {
                return new StandardUserValidator();
        }

        @Bean
        public RegisterUserUseCase registerUserUseCase(
                UserValidator userValidator,
                UserRepository userRepository) {
                return new RegisterUserUseCase(userValidator, userRepository);
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

}
