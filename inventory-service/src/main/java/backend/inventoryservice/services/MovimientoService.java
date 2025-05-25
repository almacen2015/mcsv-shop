package backend.inventoryservice.services;

import backend.dto.request.MovementDtoRequest;
import backend.dto.response.MovementDtoResponse;
import backend.pageable.Paginado;
import org.springframework.data.domain.Page;

public interface MovimientoService {

    MovementDtoResponse add(MovementDtoRequest dto);

    Page<MovementDtoResponse> listByIdProducto(Integer idProducto, Paginado paginado);
}
