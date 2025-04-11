package backend.mcsvauth.controller;

import backend.mcsvauth.models.dto.AuthLoginRequest;
import backend.mcsvauth.models.dto.AuthResponse;
import backend.mcsvauth.models.dto.UsuarioDtoRequest;
import backend.mcsvauth.models.dto.UsuarioDtoResponse;
import backend.mcsvauth.service.UsuarioService;
import backend.mcsvauth.service.impl.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UsuarioServiceImpl userDetailsService;
    private final UsuarioService usuarioService;

    public AuthenticationController(UsuarioServiceImpl userDetailsService, UsuarioService usuarioService) {
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Iniciar sesión", description = "Inicia sesión en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión iniciada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest userRequest) {
        return new ResponseEntity<>(this.userDetailsService.loginUser(userRequest), HttpStatus.OK);
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDtoResponse> registrarUsuario(@RequestBody UsuarioDtoRequest userRequest) {
        return new ResponseEntity<>(usuarioService.crearUsuario(userRequest), HttpStatus.OK);
    }
}
