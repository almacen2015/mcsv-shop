package backend.mcsvauth.mappers;

import backend.mcsvauth.models.dto.PermisoDtoResponse;
import backend.mcsvauth.models.entity.Permiso;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface PermisoMapper{

    PermisoMapper INSTANCE = Mappers.getMapper(PermisoMapper.class);

    PermisoDtoResponse toDto(Permiso entity);

    Set<PermisoDtoResponse> toSetDto(Set<Permiso> entities);
}
