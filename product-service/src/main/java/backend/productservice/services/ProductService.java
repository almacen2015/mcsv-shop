package backend.productservice.services;

import backend.pageable.Paginado;
import backend.dto.request.ProductDtoRequest;
import backend.dto.response.ProductDtoResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductDtoResponse add(ProductDtoRequest dto);

    ProductDtoResponse update(ProductDtoRequest dto, Integer id);

    Page<ProductDtoResponse> listAll(Integer page, Integer size, String orderBy);

    ProductDtoResponse getById(Integer id);

    Page<ProductDtoResponse> listByname(String nombre, Paginado paginado);

    void updateStock(Integer idProducto, Integer cantidad, String tipoMovimiento);
}
