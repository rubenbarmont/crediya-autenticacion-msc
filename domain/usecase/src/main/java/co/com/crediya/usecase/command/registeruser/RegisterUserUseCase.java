package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.service.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final TransactionalOperator transactionalOperator;

    public Mono<User> registerUser(User user) {
        log.info("Iniciando caso de uso para registro de usuario con email: {}", user.getEmail());

        // La cadena de ejecución ahora es más fluida
        return userValidator.validate(user)
                .flatMap(this::validateEmailUniqueness)
                .flatMap(userToSave -> userRepository.save(userToSave)
                        // Aplicamos la transacción directamente sobre la operación de guardado
                        .as(transactionalOperator::transactional))
                .doOnSuccess(savedUser -> log.info("Usuario registrado exitosamente con ID: {}", savedUser.getIdUser()))
                .doOnError(error -> log.error("Error al registrar usuario: {}", error.getMessage()));
    }

    private Mono<User> validateEmailUniqueness(User user) {
        return userRepository.existsByEmail(user.getEmail())
                // Continúa el flujo solo si 'exists' es false
                .filter(exists -> !exists)
                // Si el flujo se vació (porque exists era true), lanza un error
                .switchIfEmpty(Mono.error(new EmailAlreadyExistsException(user.getEmail())))
                // Si el flujo continuó, ignora el booleano (false) y devuelve el usuario original
                .thenReturn(user);
    }
}