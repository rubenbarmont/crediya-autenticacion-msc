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
        Mono<User> enrichedUserMono = rolRepository.findById(user.getIdRol())
                .switchIfEmpty(Mono.error(new IllegalStateException("El rol con ID " + user.getIdRol() + " no fue encontrado.")))
                .map(rol -> {
                    user.setRol(rol);
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
                .zipWith(enrichedUserMono, (savedUser, originalEnrichedUser) -> {
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