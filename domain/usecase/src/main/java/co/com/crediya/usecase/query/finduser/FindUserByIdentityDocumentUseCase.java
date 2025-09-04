package co.com.crediya.usecase.query.finduser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindUserByIdentityDocumentUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(Long identityDocument) {
        return userRepository.findByIdentityDocument(identityDocument)
                // Si el Mono que viene del repositorio está vacío, lanza nuestra excepción de negocio.
                .switchIfEmpty(Mono.error(new UserNotFoundException(identityDocument)));
    }
}
