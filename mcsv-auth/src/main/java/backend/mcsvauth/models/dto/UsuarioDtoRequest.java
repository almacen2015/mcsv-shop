package backend.mcsvauth.models.dto;

public record UsuarioDtoRequest(String username,
                                String password,
                                Long rolId) {
}
