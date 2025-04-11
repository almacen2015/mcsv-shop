package backend.authservice.mappers;

import backend.authservice.models.dto.PermisoDtoResponse;
import backend.authservice.models.entity.Permiso;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface PermisoMapper{

    PermisoMapper INSTANCE = Mappers.getMapper(PermisoMapper.class);

    PermisoDtoResponse toDto(Permiso entity);

    Set<PermisoDtoResponse> toSetDto(Set<Permiso> entities);
}
