package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
// Quitamos @RequiredArgsConstructor de la clase para definir nuestro propio constructor
public class MyReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserData, UUID, MyReactiveRepository>
        implements UserRepository {

    private final TransactionalOperator transactionalOperator; // <-- 2. AÑADIMOS LA DEPENDENCIA

    // 3. Modificamos el constructor para recibir el TransactionalOperator
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.mapBuilder(d, User.UserBuilder.class).build());
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> save(User user) {
        // La lógica de guardado de la clase padre es Mono<User>
        // La envolvemos en el operador transaccional.
        return super.save(user)
                .as(transactionalOperator::transactional); // <-- 4. ENVOLVEMOS LA OPERACIÓN EN UNA TRANSACCIÓN
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }
}
