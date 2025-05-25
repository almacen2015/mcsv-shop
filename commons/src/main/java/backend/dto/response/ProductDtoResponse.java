package backend.dto.response;

import java.time.LocalDate;

public record ProductDtoResponse(Integer id,
                                 String nombre,
                                 String descripcion,
                                 Double precio,
                                 Boolean estado,
                                 LocalDate fechaCreacion,
                                 Integer stock) {
}
