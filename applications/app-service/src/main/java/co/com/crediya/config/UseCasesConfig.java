package co.com.crediya.config;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.query.checkuser.CheckUserExistenceUseCase;
import co.com.crediya.usecase.query.finduser.FindUserByIdentityDocumentUseCase;
import co.com.crediya.usecase.query.finduserbyid.FindUserByIdUseCase;
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
                PasswordEncoderGateway passwordEncoderGateway,
                RolRepository rolRepository) {
                return new RegisterUserUseCase(userValidator, userRepository, passwordEncoderGateway, rolRepository);
        }

        @Bean
        public CheckUserExistenceUseCase checkUserExistenceUseCase(UserRepository userRepository) {
                return new CheckUserExistenceUseCase(userRepository);
        }

        @Bean
        public FindUserByIdentityDocumentUseCase findUserByIdentityDocumentUseCase(UserRepository userRepository) {
                return new FindUserByIdentityDocumentUseCase(userRepository);
        }

        @Bean
        public LoginUseCase loginUseCase(
                UserRepository userRepository,
                PasswordVerifier passwordVerifier,
                TokenManager tokenManager,
                RolRepository rolRepository) {
                return new LoginUseCase(userRepository, passwordVerifier, tokenManager, rolRepository);
        }

        @Bean
        public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository) {
                return new FindUserByIdUseCase(userRepository);
        }

}
