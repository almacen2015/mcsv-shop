package backend.dto.request;

public record ClienteRequestDTO(String nombre,
                                String apellido,
                                String numeroDocumento,
                                String tipoDocumento,
                                String fechaNacimiento) {
}
