package backend.dto.response;

public record ClientDtoResponse(Long id,
                                String nombre,
                                String apellido,
                                String tipoDocumento,
                                String fechaNacimiento,
                                String numeroDocumento) {
}
