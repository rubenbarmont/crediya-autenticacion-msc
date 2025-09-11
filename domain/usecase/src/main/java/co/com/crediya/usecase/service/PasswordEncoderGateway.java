package co.com.crediya.usecase.service;

import reactor.core.publisher.Mono;

/**
 * Puerto para un servicio que codifica contraseñas en texto plano.
 */
public interface PasswordEncoderGateway {
    Mono<String> encode(String rawPassword);
}