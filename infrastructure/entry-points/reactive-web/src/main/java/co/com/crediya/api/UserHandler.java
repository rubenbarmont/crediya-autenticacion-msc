package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.api.mapper.UserApiMapper;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.exceptions.IdentityDocumentAlreadyExistsException;
import co.com.crediya.model.user.exceptions.InvalidUserDataException;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.query.checkuser.CheckUserExistenceUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final RegisterUserUseCase registerUserUseCase;
    private final CheckUserExistenceUseCase checkUserExistenceUseCase;
    private final UserApiMapper userApiMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .map(userApiMapper::toDomain)
                .doOnNext(user -> log.info("Iniciando caso de uso para registro de usuario con email: {}", user.getEmail()))
                .flatMap(registerUserUseCase::execute) // Llamamos al método "puro"
                .as(transactionalOperator::transactional) // Aplicamos la transacción aquí, fuera del caso de uso
                .doOnSuccess(savedUser -> log.info("Usuario registrado exitosamente con ID: {}", savedUser.getIdUser()))
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .doOnError(error -> log.error("Error al registrar usuario: {}", error.getMessage()))
                .onErrorResume(InvalidUserDataException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getErrors()))
                .onErrorResume(IdentityDocumentAlreadyExistsException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT).bodyValue(e.getMessage()))
                .onErrorResume(EmailAlreadyExistsException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT).bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> checkUserExistsByIdentityDocument(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParam("identityDocument").orElse("0"))
                .map(Long::valueOf)
                .flatMap(checkUserExistenceUseCase::byIdentityDocument)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(exists));
    }


}
