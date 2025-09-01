package co.com.crediya.config;

import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.service.StandardUserValidator;
import co.com.crediya.usecase.service.UserValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

        // 1. Creamos el bean del validador. Es una clase pura, sin dependencias.
        @Bean
        public UserValidator userValidator() {
                return new StandardUserValidator();
        }

        // 2. Creamos el bean del caso de uso.
        // Spring ve que necesita un UserValidator y un UserRepository, y los inyecta.
        @Bean
        public RegisterUserUseCase registerUserUseCase(
                UserValidator userValidator,
                UserRepository userRepository) {
                return new RegisterUserUseCase(userValidator, userRepository);
        }
}
