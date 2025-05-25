package backend.productservice.mappers;

import backend.dto.request.ProductDtoRequest;
import backend.dto.response.ProductDtoResponse;
import backend.productservice.models.entities.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductoMapper {
    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    @Mapping(target = "id", ignore = true)
    Producto toEntity(ProductDtoRequest dto);

    Producto toEntity(ProductDtoResponse dto);

    ProductDtoResponse toDto(Producto entity);

    List<ProductDtoResponse> toDtoList(List<Producto> list);
}
