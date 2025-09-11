package co.com.crediya.usecase.service;

import co.com.crediya.model.token.TokenDetails;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Puerto para un servicio que gestiona la creación de tokens de autenticación.
 */
public interface TokenManager {
    Mono<String> generate(User user);
    Mono<TokenDetails> validate(String token);
}
