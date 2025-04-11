package backend.authservice.service;

import backend.authservice.models.dto.UsuarioDtoRequest;
import backend.authservice.models.dto.UsuarioDtoResponse;

public interface UsuarioService {
    UsuarioDtoResponse crearUsuario(UsuarioDtoRequest crearUsuarioDtoRequest);
}
