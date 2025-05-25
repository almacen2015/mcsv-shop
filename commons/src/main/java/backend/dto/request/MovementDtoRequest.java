package backend.dto.request;

public record MovementDtoRequest(
        Integer productoId,
        Integer cantidad,
        String tipoMovimiento) {
}

