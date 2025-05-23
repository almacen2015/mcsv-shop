package backend.inventoryservice.services;

import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.pageable.Paginado;
import org.springframework.data.domain.Page;

public interface MovimientoService {

    MovimientoDtoResponse add(MovimientoDtoRequest dto);

    Page<MovimientoDtoResponse> listByIdProducto(Integer idProducto, Paginado paginado);
}
