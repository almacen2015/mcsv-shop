package backend.inventoryservice.services;

import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;

import java.util.List;

public interface MovimientoService {

    MovimientoDtoResponse add(MovimientoDtoRequest dto);

    List<MovimientoDtoResponse> listByIdProducto(Integer idProducto);
}
