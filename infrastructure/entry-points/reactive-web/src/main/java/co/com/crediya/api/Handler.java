package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class Handler {
    // Inyectamos el caso de uso que realmente vamos a utilizar
    private final RegisterUserUseCase registerUserUseCase;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(UserRequestDTO.class)
                .flatMap(this::validateAndMapToDomain)
                .flatMap(registerUserUseCase::execute)
                .flatMap(savedUser -> ServerResponse
                        // En caso de éxito, devolvemos un 201 Created con la URI del nuevo recurso
                        .created(URI.create("/api/v1/usuarios/" + savedUser.getId()))
                        .bodyValue(savedUser));
    }

    private Mono<User> validateAndMapToDomain(UserRequestDTO dto) {
        try {
            // Mapeo del DTO de la capa API al Modelo de Dominio puro
            User user = User.builder()
                    .name(dto.getName())
                    .lastName(dto.getLastName())
                    .birthDate(LocalDate.parse(dto.getBirthDate())) // Parseamos String -> LocalDate
                    .address(dto.getAddress())
                    .phoneNumber(dto.getPhoneNumber())
                    .email(dto.getEmail())
                    .baseSalary(dto.getBaseSalary())
                    .build();
            return Mono.just(user);
        } catch (DateTimeParseException e) {
            // Si el formato de fecha es incorrecto, devolvemos un error controlado
            return Mono.error(new IllegalArgumentException("Invalid date format for birthDate, use YYYY-MM-DD"));
        }
    }
}
