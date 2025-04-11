package backend.authservice.mappers;

import backend.authservice.models.dto.UsuarioDtoResponse;
import backend.authservice.models.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioDtoResponse toDto(Usuario entity);


}
