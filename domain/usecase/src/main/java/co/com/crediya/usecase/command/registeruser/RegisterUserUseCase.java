package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.exceptions.IdentityDocumentAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.service.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public Mono<User> execute(User user) {
        return userValidator.validate(user)
                .flatMap(this::validateIdentityDocumentUniqueness)
                .flatMap(this::validateEmailUniqueness)
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