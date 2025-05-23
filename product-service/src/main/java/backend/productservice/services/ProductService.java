package backend.productservice.services;

import backend.pageable.Paginado;
import backend.productservice.models.dto.request.ProductoDtoRequest;
import backend.productservice.models.dto.response.ProductoDtoResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductoDtoResponse add(ProductoDtoRequest dto);

    ProductoDtoResponse update(ProductoDtoRequest dto, Integer id);

    Page<ProductoDtoResponse> listAll(Integer page, Integer size, String orderBy);

    ProductoDtoResponse getById(Integer id);

    Page<ProductoDtoResponse> listByname(String nombre, Paginado paginado);

    void updateStock(Integer idProducto, Integer cantidad, String tipoMovimiento);
}
