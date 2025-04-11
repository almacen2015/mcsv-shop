package backend.productoservice.mappers;

import backend.productoservice.models.dto.request.ProductoDtoRequest;
import backend.productoservice.models.dto.response.ProductoDtoResponse;
import backend.productoservice.models.entities.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductoMapper {
    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    @Mapping(target = "id", ignore = true)
    Producto toEntity(ProductoDtoRequest dto);

    Producto toEntity(ProductoDtoResponse dto);

    ProductoDtoResponse toDto(Producto entity);

    List<ProductoDtoResponse> toDtoList(List<Producto> list);
}
