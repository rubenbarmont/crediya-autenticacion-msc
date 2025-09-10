package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre DTOs de la API y modelos de dominio de User.
 */
@Mapper(componentModel = "spring")
public interface UserApiMapper {

    /**
     * Convierte el DTO de registro de usuario al modelo de dominio User.
     * @param dto El objeto con los datos de entrada de la API.
     * @return Un objeto User del dominio.
     */
    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "identityDocument", source = "identityDocument")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "baseSalary", source = "baseSalary")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "idRol", source = "idRol")
    User toDomain(UserRequestDTO dto);
}
