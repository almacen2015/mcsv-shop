package backend.clientservice.models.mappers;

import backend.clientservice.models.dtos.ClienteRequestDTO;
import backend.clientservice.models.dtos.ClienteResponseDTO;
import backend.clientservice.models.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(target = "id", ignore = true)
    Cliente toEntity(ClienteRequestDTO dto);

    ClienteResponseDTO toResponseDTO(Cliente cliente);

    List<ClienteResponseDTO> toListResponseDTO(List<Cliente> clientes);
}
