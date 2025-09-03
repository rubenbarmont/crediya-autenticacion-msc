package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Registrar un nuevo usuario",
                            tags = {"Gestión de Usuarios"},
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente.", content = @Content(schema = @Schema(implementation = User.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos."),
                                    @ApiResponse(responseCode = "409", description = "El correo o documento ya existe.")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "checkUserExistsByIdentityDocument",
                    operation = @Operation(
                            operationId = "checkUserExistsByIdentityDocument",
                            summary = "Verificar si un usuario existe por su documento de identidad",
                            tags = {"Gestión de Usuarios"},
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "identityDocument", required = true, description = "Número de documento a verificar.")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Verificación exitosa.", content = @Content(schema = @Schema(implementation = Boolean.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {
        return route(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), userHandler::registerUser)
                .andRoute(GET("/api/v1/usuarios"), userHandler::checkUserExistsByIdentityDocument);
    }
}
