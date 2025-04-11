package backend.mcsvauth.mappers;

import backend.mcsvauth.models.dto.RolDtoResponse;
import backend.mcsvauth.models.entity.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface RolMapper {

    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    RolDtoResponse toDto(Rol entity);

    Set<RolDtoResponse> toSetDto(Set<Rol> entities);
}
