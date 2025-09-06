package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// Heredamos de las interfaces de Spring Data
public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, Long>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByIdentityDocument(Long identityDocument);
    Mono<UserEntity> findByIdentityDocument(Long identityDocument);
    // --- AÑADIR MÉTODO FALTANTE ---
    Mono<UserEntity> findByEmail(String email);
}