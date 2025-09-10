package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        UserReactiveRepository
        > implements UserRepository {

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.mapBuilder(entity, User.UserBuilder.class).build());
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByIdentityDocument(Long identityDocument) {
        return repository.existsByIdentityDocument(identityDocument);
    }

    // --- IMPLEMENTACIÓN DEL NUEVO MÉTODO ---
    @Override
    public Mono<User> findByIdentityDocument(Long identityDocument) {
        return repository.findByIdentityDocument(identityDocument)
                .map(this::toEntity); // 'toEntity' es el mapper que ya tienes en ReactiveAdapterOperations
    }

    // --- IMPLEMENTACIÓN DEL MÉTODO FALTANTE ---
    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity); // Reutilizamos el helper 'toEntity'
    }

    // --- IMPLEMENTACIÓN DEL NUEVO MÉTODO DEL DOMINIO ---
    @Override
    public Mono<User> findById(Long idUser) {
        // La interfaz 'repository' ya tiene un método findById gracias a ReactiveCrudRepository
        return repository.findById(idUser)
                .map(this::toEntity);
    }

}
