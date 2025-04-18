package backend.saleservice.models.dtos.response;

public record ClientResponseDTO(Long id,
                                String nombre,
                                String apellido,
                                String tipoDocumento,
                                String fechaNacimiento,
                                String numeroDocumento) {
}
