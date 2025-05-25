package backend.saleservice.models.mapper;

import backend.saleservice.models.documents.Venta;
import backend.dto.request.SaleRequestDto;
import backend.dto.response.SaleDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaleMapper {

    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "details.subtotal", ignore = true)
    Venta toEntity(SaleRequestDto requestDto);

    SaleDtoResponse toDto(Venta venta);
}
