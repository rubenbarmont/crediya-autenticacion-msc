package co.com.crediya.usecase.service;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Define el contrato para los servicios de validación de la entidad User.
 * Es un Servicio de Dominio que encapsula las reglas de negocio
 * para determinar si un usuario es válido.
 */
public interface UserValidator {
    Mono<User> validate(User user);
}