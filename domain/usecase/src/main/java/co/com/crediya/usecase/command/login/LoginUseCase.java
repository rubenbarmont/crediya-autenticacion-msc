package co.com.crediya.usecase.command.login;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.InvalidCredentialsException;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.service.PasswordVerifier;
import co.com.crediya.usecase.service.TokenManager;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final TokenManager tokenManager;
    private final RolRepository rolRepository;

    public Mono<String> execute(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> verifyPassword(user, rawPassword))
                .flatMap(this::enrichUserWithRole)
                .flatMap(tokenManager::generate);
    }

    private Mono<User> verifyPassword(User user, String rawPassword) {
        return passwordVerifier.verify(rawPassword, user.getPassword())
                .filter(Boolean::booleanValue)
                .map(isValid -> user)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()));
    }

    private Mono<User> enrichUserWithRole(User user) {
        if (user.getIdRol() == null) {
            return Mono.error(new IllegalStateException("El usuario con email " + user.getEmail() + " no tiene un rol asignado."));
        }
        return rolRepository.findById(user.getIdRol())
                .switchIfEmpty(Mono.error(new IllegalStateException("El rol con ID " + user.getIdRol() + " no fue encontrado.")))
                .map(rol -> {
                    user.setRol(rol);
                    return user;
                });
    }


}