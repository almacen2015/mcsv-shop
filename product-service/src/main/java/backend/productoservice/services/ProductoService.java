package backend.productoservice.services;

import backend.productoservice.models.dto.request.ProductoDtoRequest;
import backend.productoservice.models.dto.response.ProductoDtoResponse;
import backend.productoservice.util.Paginado;
import org.springframework.data.domain.Page;

public interface ProductoService {

    ProductoDtoResponse add(ProductoDtoRequest dto);

    ProductoDtoResponse update(ProductoDtoRequest dto, Integer id);

    Page<ProductoDtoResponse> listAll(Integer page, Integer size, String orderBy);

    ProductoDtoResponse getById(Integer id);

    Page<ProductoDtoResponse> listByname(String nombre, Paginado paginado);

    void updateStock(Integer idProducto, Integer cantidad, String tipoMovimiento);
}
