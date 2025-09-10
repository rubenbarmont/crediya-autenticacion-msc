package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.rol.gateways.RolRepository;
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
    private final RolRepository rolRepository;

    public Mono<User> execute(User user) {
        String rawPassword = user.getPassword();

        // 1. Buscamos el rol y lo guardamos en una variable Mono para reutilizarlo
        Mono<User> enrichedUserMono = rolRepository.findById(user.getIdRol())
                .switchIfEmpty(Mono.error(new IllegalStateException("El rol con ID " + user.getIdRol() + " no fue encontrado.")))
                .map(rol -> {
                    user.setRol(rol); // Asignamos el objeto Rol completo al usuario
                    return user;
                });

        return enrichedUserMono
                .flatMap(userValidator::validate)
                .flatMap(this::validateIdentityDocumentUniqueness)
                .flatMap(this::validateEmailUniqueness)
                .flatMap(validUser -> passwordEncoderGateway.encode(rawPassword)
                        .map(encodedPassword -> {
                            validUser.setPassword(encodedPassword);
                            return validUser;
                        }))
                .flatMap(userRepository::save)
                // 2. Combinamos el usuario guardado con nuestro usuario enriquecido
                .zipWith(enrichedUserMono, (savedUser, originalEnrichedUser) -> {
                    // 'savedUser' viene de la BD (con el idUser correcto, pero rol=null)
                    // 'originalEnrichedUser' es el que tenemos en memoria con el objeto Rol.
                    // Le ponemos el objeto Rol al usuario guardado antes de devolverlo.
                    savedUser.setRol(originalEnrichedUser.getRol());
                    return savedUser;
                });
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