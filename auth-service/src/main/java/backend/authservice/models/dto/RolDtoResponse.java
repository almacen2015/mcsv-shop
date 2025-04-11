package backend.authservice.models.dto;

import java.util.Set;

public record RolDtoResponse(Long id, String nombre, Set<PermisoDtoResponse> permisos) {
}
