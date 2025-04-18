package backend.saleservice.models.dtos.response;

public record MovimientoDtoResponse(Integer id,
                                    Integer productoId,
                                    Integer cantidad,
                                    String tipoMovimiento,
                                    String fechaRegistro) {
}
