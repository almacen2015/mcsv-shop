package backend.saleservice.models.mapper;

import backend.saleservice.models.documents.Venta;
import backend.saleservice.models.dtos.request.SaleRequestDto;
import backend.saleservice.models.dtos.response.SaleResponseDto;
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

    SaleResponseDto toDto(Venta venta);
}
