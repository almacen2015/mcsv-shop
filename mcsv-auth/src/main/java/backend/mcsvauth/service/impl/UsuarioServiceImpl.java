package backend.mcsvauth.service.impl;

import backend.mcsvauth.exception.AuthException;
import backend.mcsvauth.mappers.UsuarioMapper;
import backend.mcsvauth.models.dto.AuthLoginRequest;
import backend.mcsvauth.models.dto.AuthResponse;
import backend.mcsvauth.models.dto.UsuarioDtoRequest;
import backend.mcsvauth.models.dto.UsuarioDtoResponse;
import backend.mcsvauth.models.entity.Rol;
import backend.mcsvauth.models.entity.Usuario;
import backend.mcsvauth.repository.RolRepository;
import backend.mcsvauth.repository.UsuarioRepository;
import backend.mcsvauth.service.UsuarioService;
import backend.mcsvauth.util.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UserDetailsService, UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername(username);
        if (usuarioEncontrado.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Añadir roles y permisos al usuario encontrado en la base de datos

        usuarioEncontrado.get().getRoles().forEach(rol -> {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre().name()));
        });

        // Añadir permisos al usuario encontrado en la base de datos (opcional)
        usuarioEncontrado.get().getRoles().stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .forEach(permiso -> authorityList.add(new SimpleGrantedAuthority(permiso.getNombre())));

        // Crear un objeto User con los datos del usuario encontrado en la base de datos

        return new User(usuarioEncontrado.get().getUsername(),
                usuarioEncontrado.get().getPassword(),
                usuarioEncontrado.get().isEnabled(),
                usuarioEncontrado.get().isAccountNoExpired(),
                usuarioEncontrado.get().isCredentialsNoExpired(),
                usuarioEncontrado.get().isAccountNoLocked(),
                authorityList);

    }

    public AuthResponse loginUser(AuthLoginRequest userRequest) {
        String username = userRequest.username();
        String password = userRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "Usuario logueado", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("Usuario no encontrado");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UsuarioDtoResponse crearUsuario(UsuarioDtoRequest dto) {
        validarNombreUsuario(dto.username());
        validarPassword(dto.password());
        validarIdRol(dto.rolId());

        Rol rol = obtenerPermisosPorRol(dto.rolId());

        Usuario usuario = Usuario.builder()
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .roles(Set.of(rol))
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialsNoExpired(true)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        UsuarioDtoResponse usuarioDtoResponse = usuarioMapper.toDto(usuarioGuardado);
        return usuarioDtoResponse;
    }

    private Rol obtenerPermisosPorRol(Long rolId) {
        Rol rolEncontrado = rolRepository.findById(rolId).orElseThrow(() -> new AuthException(AuthException.ROL_NO_ENCONTRADO));
        return rolEncontrado;
    }

    private void validarNombreUsuario(String username) {
        if (username == null || username.isEmpty()) {
            throw new AuthException(AuthException.NOMBRE_USUARIO_VACIO);
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new AuthException(AuthException.PASSWORD_VACIO);
        }
    }

    private void validarIdRol(Long rolId) {
        if (rolId == null || rolId <= 0) {
            throw new AuthException(AuthException.ID_INVALIDO);
        }
    }
}
