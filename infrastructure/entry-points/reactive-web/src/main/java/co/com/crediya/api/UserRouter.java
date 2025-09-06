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
import co.com.crediya.api.dto.LoginRequestDTO;
import co.com.crediya.api.dto.LoginResponseDTO;

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
            ),
            // --- NUEVA OPERACIÓN SWAGGER PARA EL NUEVO ENDPOINT ---
            @RouterOperation(
                    path = "/api/v1/usuarios/by-identity-document/{identityDocument}", // La nueva ruta
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "findUserByIdentityDocument",
                    operation = @Operation(
                            operationId = "findUserByIdentityDocument",
                            summary = "Buscar un usuario por su documento de identidad",
                            tags = {"Gestión de Usuarios"},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "identityDocument", required = true, description = "Número de documento del usuario a buscar.")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado.", content = @Content(schema = @Schema(implementation = User.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
                            }
                    )
            ),
            // --- NUEVA OPERACIÓN SWAGGER PARA EL LOGIN ---
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "Autenticar un usuario en el sistema",
                            tags = {"Autenticación"},
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas.")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {
        return route(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), userHandler::registerUser)
                .andRoute(GET("/api/v1/usuarios"), userHandler::checkUserExistsByIdentityDocument)
                .andRoute(GET("/api/v1/usuarios/by-identity-document/{identityDocument}"), userHandler::findUserByIdentityDocument)
                .andRoute(POST("/api/v1/login").and(accept(MediaType.APPLICATION_JSON)), userHandler::login);
    }
}
