package backend.inventoryservice.mappers;

import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.inventoryservice.models.entities.Movimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MovimientoMapper {
    MovimientoMapper INSTANCE = Mappers.getMapper(MovimientoMapper.class);

    @Mapping(target = "id", ignore = true)
    Movimiento toEntity(MovimientoDtoRequest dto);

    MovimientoDtoResponse toDto(Movimiento entity);

    List<MovimientoDtoResponse> toListDto(List<Movimiento> entities);

    List<Movimiento> toListEntity(List<MovimientoDtoRequest> dtos);
}
