package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class MyReactiveRepositoryAdapter
        // 1. Extendemos del helper con nuestros tipos específicos
        extends ReactiveAdapterOperations<User, UserData, UUID, MyReactiveRepository>
        // 2. Implementamos el puerto de nuestro dominio
        implements UserRepository {

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        // 3. Pasamos al constructor del padre el repositorio, el mapper y la función de mapeo
        // que convierte de la capa de datos (UserData) al dominio (User).
        // Usamos el patrón builder de nuestro modelo de dominio.
        super(repository, mapper, d -> mapper.mapBuilder(d, User.UserBuilder.class).build());
    }

    // 4. Implementamos el método específico de nuestro puerto que no está en el helper genérico.
    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email) // Llama al método del repositorio de Spring Data
                .map(this::toEntity);       // Usa la función de mapeo del helper para convertir a dominio
    }

    // NOTA: El método save() ya está implementado en la clase padre ReactiveAdapterOperations,
    // por lo que no necesitamos sobreescribirlo. ¡Reutilización de código en acción!
}
