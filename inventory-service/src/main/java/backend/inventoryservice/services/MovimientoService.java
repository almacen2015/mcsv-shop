package backend.inventoryservice.services;

import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.inventoryservice.util.Paginado;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovimientoService {

    MovimientoDtoResponse add(MovimientoDtoRequest dto);

    Page<MovimientoDtoResponse> listByIdProducto(Integer idProducto, Paginado paginado);
}
