// path: .../api/Handler.java
package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.api.validator.RequestValidator; // <-- 1. Importamos la nueva clase
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCase registerUserUseCase;
    private final RequestValidator requestValidator; // <-- 2. Inyectamos nuestro nuevo validador

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(UserRequestDTO.class)
                .flatMap(requestValidator::validate) // <-- 3. Usamos el validador. Es más limpio con flatMap.
                .flatMap(this::mapToDomain)
                .flatMap(registerUserUseCase::execute)
                .flatMap(savedUser -> ServerResponse
                        .created(URI.create("/api/v1/usuarios/" + savedUser.getId()))
                        .bodyValue(savedUser));
    }

    // 4. El método privado 'validateDto' se elimina por completo de esta clase.

    private Mono<User> mapToDomain(UserRequestDTO dto) {
        try {
            User user = User.builder()
                    .name(dto.getName())
                    .lastName(dto.getLastName())
                    .birthDate(LocalDate.parse(dto.getBirthDate()))
                    .email(dto.getEmail())
                    .address(dto.getAddress())
                    .phoneNumber(dto.getPhoneNumber())
                    .baseSalary(dto.getBaseSalary())
                    .build();
            return Mono.just(user);
        } catch (DateTimeParseException e) {
            return Mono.error(new ServerWebInputException("Invalid date format for birthDate, use YYYY-MM-DD"));
        }
    }
}