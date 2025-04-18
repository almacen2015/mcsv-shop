package backend.saleservice.models.dtos.response;

public record MovementResponseDto(Integer id,
                                  Integer productoId,
                                  Integer cantidad,
                                  String tipoMovimiento,
                                  String fechaRegistro) {
}
