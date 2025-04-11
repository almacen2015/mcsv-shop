package backend.clientservice.models.dtos;

public record ClienteRequestDTO(String nombre,
                                String apellido,
                                String numeroDocumento,
                                String tipoDocumento,
                                String fechaNacimiento) {
}
