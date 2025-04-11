package backend.mcsvauth.models.dto;

import java.util.Set;

public record UsuarioDtoResponse(Long id,
                                 String username,
                                 boolean isEnabled,
                                 boolean accountNoExpired,
                                 boolean accountNoLocked,
                                 boolean credentialsNoExpired,
                                 Set<RolDtoResponse> roles) {
}
