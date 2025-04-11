package backend.saleservice.models.dtos.request;

public record MovimientoDtoRequest(
        Integer productoId,
        Integer cantidad,
        String tipoMovimiento) {
}

