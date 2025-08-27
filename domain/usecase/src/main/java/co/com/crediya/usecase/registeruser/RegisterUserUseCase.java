package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.UserValidator;
import co.com.crediya.model.user.excepcion.EmailAlreadyExistsException;
import co.com.crediya.model.user.gateways.LoggerPort;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final LoggerPort logger; // <-- La nueva dependencia

    public Mono<User> execute(User user) {
        return Mono.just(user)
                // Usamos nuestra interfaz logger
                .doOnNext(u -> logger.info("Starting user registration process for email: {}", u.getEmail()))
                .doOnNext(UserValidator::validate)
                .flatMap(this::checkIfEmailExists)
                .flatMap(userRepository::save)
                .doOnSuccess(savedUser -> logger.info("User registered successfully with ID: {}", savedUser.getId()))
                .doOnError(error -> logger.error(
                        String.format("Error during user registration for email: %s", user.getEmail()),
                        error
                ));
    }

    private User assignId(User user) {
        return user.toBuilder().id(UUID.randomUUID()).build();
    }

    private Mono<User> checkIfEmailExists(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(foundUser -> Mono.<User>error(new EmailAlreadyExistsException("Email already registered: " + user.getEmail())))
                .switchIfEmpty(Mono.just(user));
    }
}
