package backend.saleservice.models.dtos.response;

import java.time.LocalDate;

public record ProductResponseDto(Integer id,
                                 String nombre,
                                 String descripcion,
                                 Double precio,
                                 Boolean estado,
                                 LocalDate fechaCreacion,
                                 Integer stock) {
}
