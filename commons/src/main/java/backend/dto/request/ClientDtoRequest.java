package backend.dto.request;

public record ClientDtoRequest(String nombre,
                               String apellido,
                               String numeroDocumento,
                               String tipoDocumento,
                               String fechaNacimiento) {
}
