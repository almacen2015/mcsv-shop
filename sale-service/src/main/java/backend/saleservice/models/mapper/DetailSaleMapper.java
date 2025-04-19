package backend.saleservice.models.mapper;

import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.dtos.response.DetailSaleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DetailSaleMapper {
    DetailSaleMapper INSTANCE = Mappers.getMapper(DetailSaleMapper.class);

    List<DetailSaleResponseDto> toDtos(List<DetalleVenta> detalles);
}
