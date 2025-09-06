package co.com.crediya.usecase.service;

import reactor.core.publisher.Mono;

/**
 * Puerto para un servicio que verifica si una contraseña en texto plano
 * coincide con una contraseña codificada (hash).
 */
public interface PasswordVerifier {
    Mono<Boolean> verify(String rawPassword, String encodedPassword);
}
