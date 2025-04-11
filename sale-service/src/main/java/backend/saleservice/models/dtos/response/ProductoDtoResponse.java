package backend.saleservice.models.dtos.response;

import java.time.LocalDate;

public record ProductoDtoResponse(Integer id,
                                  String nombre,
                                  String descripcion,
                                  Double precio,
                                  Boolean estado,
                                  LocalDate fechaCreacion,
                                  Integer stock) {
}
