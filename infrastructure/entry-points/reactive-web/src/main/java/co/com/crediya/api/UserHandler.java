package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponseDTO;
import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.api.mapper.UserApiMapper;
import co.com.crediya.model.user.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.user.exceptions.IdentityDocumentAlreadyExistsException;
import co.com.crediya.model.user.exceptions.InvalidUserDataException;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.query.checkuser.CheckUserExistenceUseCase;
import co.com.crediya.usecase.query.finduser.FindUserByIdentityDocumentUseCase;
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
import co.com.crediya.api.dto.LoginRequestDTO;
import co.com.crediya.api.dto.LoginResponseDTO;
import co.com.crediya.model.user.exceptions.InvalidCredentialsException;
import co.com.crediya.usecase.command.login.LoginUseCase;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final RegisterUserUseCase registerUserUseCase;
    private final CheckUserExistenceUseCase checkUserExistenceUseCase;
    private final UserApiMapper userApiMapper;
    private final TransactionalOperator transactionalOperator;
    private final FindUserByIdentityDocumentUseCase findUserUseCase;
    private final LoginUseCase loginUseCase;

    /**
     * Maneja la petición para registrar un nuevo usuario.
     */
    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                // --- AÑADE ESTA LÍNEA PARA DEPURAR ---
                .doOnNext(dto -> log.info("DTO recibido: {}", dto.toString()))
                .map(userApiMapper::toDomain)
                .doOnNext(user -> log.info("Iniciando caso de uso para registro de usuario con documento: {}", user.getIdentityDocument()))
                .flatMap(registerUserUseCase::execute)
                .as(transactionalOperator::transactional)
                .doOnSuccess(savedUser -> log.info("Usuario registrado exitosamente con ID: {}", savedUser.getIdUser()))
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .doOnError(error -> log.error("Error al registrar usuario: {}", error.getMessage()))
                .onErrorResume(InvalidUserDataException.class, e ->
                        ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(e.getErrors()))
                .onErrorResume(IdentityDocumentAlreadyExistsException.class, e ->
                        buildErrorResponse(e, HttpStatus.CONFLICT, "IDENTITY_DOCUMENT_ALREADY_EXISTS", serverRequest))
                .onErrorResume(EmailAlreadyExistsException.class, e ->
                        buildErrorResponse(e, HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", serverRequest));
    }

    /**
     * Maneja la petición para verificar si un usuario existe por su documento.
     */
    public Mono<ServerResponse> checkUserExistsByIdentityDocument(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.queryParam("identityDocument"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("identityDocument es requerido")))
                .map(Long::valueOf)
                .flatMap(checkUserExistenceUseCase::byIdentityDocument)
                .flatMap(exists -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(exists))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(e.getMessage()));
    }

    /**
     * Construye una respuesta de error estandarizada en formato JSON.
     */
    private Mono<ServerResponse> buildErrorResponse(Throwable err, HttpStatus status, String errorCode, ServerRequest request) {
        ErrorResponseDTO errorDto = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .error(errorCode)
                .message(err.getMessage())
                .path(request.path())
                .build();
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorDto);
    }

    // --- NUEVO MÉTODO PARA EL ENDPOINT ---
    public Mono<ServerResponse> findUserByIdentityDocument(ServerRequest serverRequest) {
        // Extraemos el valor para poder loguearlo.
        Long identityDocument = Long.valueOf(serverRequest.pathVariable("identityDocument"));

        return Mono.just(identityDocument)
                .doOnNext(doc -> log.info("Iniciando caso de uso para buscar usuario con documento: {}", doc)) // <-- LOG INICIO
                .flatMap(findUserUseCase::execute)
                .doOnSuccess(user -> log.info("Usuario encontrado exitosamente con ID: {}", user.getIdUser())) // <-- LOG ÉXITO
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                // Ahora el .switchIfEmpty() ya no es necesario aquí, porque el UseCase se encarga de lanzar el error.
                // En su lugar, manejamos ese error específico.
                .doOnError(UserNotFoundException.class, e -> log.warn("Intento de búsqueda de usuario no existente: {}", e.getMessage())) // <-- LOG ERROR
                .onErrorResume(UserNotFoundException.class, e ->
                        buildErrorResponse(e, HttpStatus.NOT_FOUND, "USER_NOT_FOUND", serverRequest));
    }

    // --- NUEVO MÉTODO PARA MANEJAR EL LOGIN ---
    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequestDTO.class)
                .doOnNext(dto -> log.info("Iniciando intento de login para el usuario: {}", dto.getEmail()))
                .flatMap(dto -> loginUseCase.execute(dto.getEmail(), dto.getPassword()))
                .flatMap(token -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new LoginResponseDTO(token)))
                .doOnError(throwable -> log.warn("Fallo el intento de login: {}", throwable.getMessage()))
                .onErrorResume(InvalidCredentialsException.class, e ->
                        buildErrorResponse(e, HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", serverRequest));
    }

}
