package backend.inventoryservice.mappers;

import backend.dto.request.MovementDtoRequest;
import backend.dto.response.MovementDtoResponse;
import backend.inventoryservice.models.entities.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MovimientoMapper {
    MovimientoMapper INSTANCE = Mappers.getMapper(MovimientoMapper.class);

    @Mapping(target = "id", ignore = true)
    Movimiento toEntity(MovementDtoRequest dto);

    MovementDtoResponse toDto(Movimiento entity);

    List<MovementDtoResponse> toListDto(List<Movimiento> entities);

    List<Movimiento> toListEntity(List<MovementDtoRequest> dtos);
}
