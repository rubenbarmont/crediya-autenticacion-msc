package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UserRequestDTO;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserApiMapper {
    @Mapping(target = "idUser", ignore = true)
    User toDomain(UserRequestDTO dto);
}
