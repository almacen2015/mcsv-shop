package backend.saleservice.models.dtos.request;

public record MovementRequestDto(
        Integer productoId,
        Integer cantidad,
        String tipoMovimiento) {
}

