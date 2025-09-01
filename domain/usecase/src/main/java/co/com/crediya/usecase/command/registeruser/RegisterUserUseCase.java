package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.service.UserValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public Mono<User> execute(User user) {
        // La lógica de negocio pura, sin logs ni transacciones
        return userValidator.validate(user)
                .flatMap(this::validateEmailUniqueness)
                .flatMap(userRepository::save); // <-- La llamada directa a save
    }

    private Mono<User> validateEmailUniqueness(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new EmailAlreadyExistsException(user.getEmail())))
                .thenReturn(user);
    }
}