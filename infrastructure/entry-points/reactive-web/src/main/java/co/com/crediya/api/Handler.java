/*
package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.command.registeruser.RegisterUserUseCase;
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
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(UserRequestDTO.class)
                .flatMap(requestValidator::validate)
                .flatMap(this::mapToDomain)
                .flatMap(registerUserUseCase::execute)
                .flatMap(savedUser -> ServerResponse
                        .created(URI.create("/api/v1/usuarios/" + savedUser.getId()))
                        .bodyValue(savedUser));
    }

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
}*/
