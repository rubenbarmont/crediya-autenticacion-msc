package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByIdentityDocument(Long identityDocument);
    Mono<User> findByIdentityDocument(Long identityDocument);
    Mono<User> findByEmail(String email);
    Mono<User> findById(Long idUser);
}