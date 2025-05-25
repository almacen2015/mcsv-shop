package backend.clientservice.models.mappers;

import backend.dto.request.ClientDtoRequest;
import backend.dto.response.ClientDtoResponse;
import backend.clientservice.models.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(target = "id", ignore = true)
    Cliente toEntity(ClientDtoRequest dto);

    ClientDtoResponse toResponseDTO(Cliente cliente);

    List<ClientDtoResponse> toListResponseDTO(List<Cliente> clientes);
}
