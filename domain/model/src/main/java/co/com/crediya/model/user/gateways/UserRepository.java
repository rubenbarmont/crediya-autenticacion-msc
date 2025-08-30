package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Puerto que define las operaciones de persistencia para la entidad User.
 * La implementación de esta interfaz se encontrará en la capa de infraestructura
 * (driven-adapters).
 */
public interface UserRepository {

    /**
     * Guarda un nuevo usuario en la fuente de datos.
     *
     * @param user El usuario a guardar.
     * @return un Mono que emite el usuario guardado, incluyendo su ID generado.
     */
    Mono<User> save(User user);

    /**
     * Verifica si un usuario con el correo electrónico especificado ya existe.
     *
     * @param email El correo electrónico a verificar.
     * @return un Mono que emite 'true' si el correo ya existe, 'false' en caso contrario.
     */
    Mono<Boolean> existsByEmail(String email);

}