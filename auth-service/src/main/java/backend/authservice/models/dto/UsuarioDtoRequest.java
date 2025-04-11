package backend.authservice.models.dto;

public record UsuarioDtoRequest(String username,
                                String password,
                                Long rolId) {
}
