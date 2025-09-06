package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.exceptions.IdentityDocumentAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.service.PasswordEncoderGateway;
import co.com.crediya.usecase.service.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;

    public Mono<User> execute(User user) {
        String rawPassword = user.getPassword();

        return userValidator.validate(user) // El 'user' ya trae el idRol desde el DTO
                .flatMap(this::validateIdentityDocumentUniqueness)
                .flatMap(this::validateEmailUniqueness)
                .flatMap(validUser -> passwordEncoderGateway.encode(rawPassword)
                        .map(encodedPassword -> {
                            validUser.setPassword(encodedPassword);
                            return validUser;
                        }))
                .flatMap(userRepository::save);
    }

    private Mono<User> validateEmailUniqueness(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new EmailAlreadyExistsException(user.getEmail())))
                .thenReturn(user);
    }

    private Mono<User> validateIdentityDocumentUniqueness(User user) {
        return userRepository.existsByIdentityDocument(user.getIdentityDocument())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new IdentityDocumentAlreadyExistsException(user.getIdentityDocument())))
                .thenReturn(user);
    }
}