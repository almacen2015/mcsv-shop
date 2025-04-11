package backend.saleservice.models.dtos.request;

import java.util.List;

public record VentaRequestDto(Integer clientId,
                              List<DetalleVentaRequestDto> details) {
}
