package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponseDTO;
import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "Autenticar un usuario en el sistema",
                            tags = {"Autenticación"},
                            requestBody = @RequestBody(required = true, description = "Credenciales del usuario para iniciar sesión", content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa. Devuelve un token JWT.", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Registrar un nuevo usuario",
                            tags = {"Gestión de Usuarios"},
                            requestBody = @RequestBody(required = true, description = "Datos del nuevo usuario a registrar", content = @Content(schema = @Schema(implementation = UserRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente.", content = @Content(schema = @Schema(implementation = User.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos. La respuesta contendrá los detalles de los campos erróneos."),
                                    @ApiResponse(responseCode = "409", description = "Conflicto. El correo o documento de identidad ya se encuentra registrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
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
                                    @Parameter(in = ParameterIn.QUERY, name = "identityDocument", required = true, description = "Número de documento a verificar.", schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Verificación exitosa. Devuelve 'true' si existe, 'false' en caso contrario.", content = @Content(schema = @Schema(implementation = Boolean.class))),
                                    @ApiResponse(responseCode = "400", description = "Parámetro 'identityDocument' es requerido o tiene un formato inválido.")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/by-identity-document/{identityDocument}",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "findUserByIdentityDocument",
                    operation = @Operation(
                            operationId = "findUserByIdentityDocument",
                            summary = "Buscar un usuario por su documento de identidad",
                            tags = {"Gestión de Usuarios"},
                            security = @SecurityRequirement(name = "bearerAuth"), // Endpoint protegido
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "identityDocument", required = true, description = "Número de documento del usuario a buscar.", schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado.", content = @Content(schema = @Schema(implementation = User.class))),
                                    @ApiResponse(responseCode = "400", description = "El documento de identidad proporcionado en la URL no es un número válido.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{id}",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "findUserById",
                    operation = @Operation(
                            operationId = "findUserById",
                            summary = "Buscar un usuario por su ID de sistema",
                            tags = {"Gestión de Usuarios"},
                            security = @SecurityRequirement(name = "bearerAuth"), // Endpoint protegido
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id", required = true, description = "ID único del usuario a buscar.", schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado.", content = @Content(schema = @Schema(implementation = User.class))),
                                    @ApiResponse(responseCode = "400", description = "El ID proporcionado en la URL no es un número válido.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {
        return route(POST("/api/v1/login").and(accept(MediaType.APPLICATION_JSON)), userHandler::login)
                .andRoute(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), userHandler::registerUser)
                .andRoute(GET("/api/v1/usuarios"), userHandler::checkUserExistsByIdentityDocument)
                .andRoute(GET("/api/v1/usuarios/by-identity-document/{identityDocument}"), userHandler::findUserByIdentityDocument)
                .andRoute(GET("/api/v1/usuarios/{id}"), userHandler::findUserById);
    }
}
