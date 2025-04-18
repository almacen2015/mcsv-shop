package backend.saleservice.models.dtos.response;

import backend.saleservice.models.documents.DetalleVenta;

import java.util.List;

public record SaleResponseDto(
        String id,
        String client,
        String date,
        Double total,
        List<DetalleVenta> details) {
}
