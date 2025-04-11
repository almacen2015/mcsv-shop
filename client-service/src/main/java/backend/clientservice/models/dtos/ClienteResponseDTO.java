package backend.clientservice.models.dtos;

public record ClienteResponseDTO(Long id,
                                 String nombre,
                                 String apellido,
                                 String tipoDocumento,
                                 String fechaNacimiento,
                                 String numeroDocumento) {
}
