package backend.mcsvauth.service;

import backend.mcsvauth.models.dto.UsuarioDtoRequest;
import backend.mcsvauth.models.dto.UsuarioDtoResponse;

public interface UsuarioService {
    UsuarioDtoResponse crearUsuario(UsuarioDtoRequest crearUsuarioDtoRequest);
}
