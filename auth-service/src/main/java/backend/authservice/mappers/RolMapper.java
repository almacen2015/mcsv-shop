package backend.authservice.mappers;

import backend.authservice.models.dto.RolDtoResponse;
import backend.authservice.models.entity.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface RolMapper {

    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    RolDtoResponse toDto(Rol entity);

    Set<RolDtoResponse> toSetDto(Set<Rol> entities);
}
