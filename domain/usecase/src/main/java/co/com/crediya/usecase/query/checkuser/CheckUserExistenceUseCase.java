package co.com.crediya.usecase.query.checkuser;

import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CheckUserExistenceUseCase {

    private final UserRepository userRepository;

    /**
     * Verifica si un usuario existe por su documento de identidad.
     * @param identityDocument El documento a verificar.
     * @return Un Mono<Boolean> que emite true si existe, false en caso contrario.
     */
    public Mono<Boolean> byIdentityDocument(Long identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument);
    }

    /**
     * Verifica si un usuario existe por su correo electrónico.
     * @param email El correo a verificar.
     * @return Un Mono<Boolean> que emite true si existe, false en caso contrario.
     */
    public Mono<Boolean> byEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
