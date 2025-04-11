package backend.mcsvauth.mappers;

import backend.mcsvauth.models.dto.UsuarioDtoResponse;
import backend.mcsvauth.models.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioDtoResponse toDto(Usuario entity);


}
