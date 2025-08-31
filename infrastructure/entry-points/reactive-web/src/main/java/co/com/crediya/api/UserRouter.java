package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class UserRouter {

    @Bean
    @RouterOperation(
            // 1. Describe la ruta y el método HTTP
            path = "/api/v1/usuarios",
            method = RequestMethod.POST,
            beanClass = UserHandler.class,
            beanMethod = "registerUser",

            // 2. Proporciona la metadata para la UI de Swagger
            operation = @Operation(
                    operationId = "registerUser",
                    summary = "Registrar un nuevo usuario",
                    description = "Crea un nuevo solicitante en el sistema con sus datos personales.",
                    tags = {"Gestión de Usuarios"},

                    // 3. Describe el cuerpo de la petición (el JSON que se envía)
                    requestBody = @RequestBody(
                            required = true,
                            description = "Datos del nuevo usuario a registrar.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserRequestDTO.class)
                            )
                    ),

                    // 4. Describe todas las posibles respuestas del endpoint
                    responses = {
                            @ApiResponse(
                                    responseCode = "201",
                                    description = "Usuario creado exitosamente.",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Datos de entrada inválidos (campos faltantes, formato incorrecto, etc.).",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = java.util.List.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "409",
                                    description = "Conflicto. El correo electrónico ya se encuentra registrado.",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = String.class)
                                    )
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {
        return route(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), userHandler::registerUser);
    }
}
