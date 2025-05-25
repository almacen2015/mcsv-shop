package backend.dto.response;

public record MovementDtoResponse(Integer id,
                                  Integer productoId,
                                  Integer cantidad,
                                  String tipoMovimiento,
                                  String fechaRegistro) {
}
