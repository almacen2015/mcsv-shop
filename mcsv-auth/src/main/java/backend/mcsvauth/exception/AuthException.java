package backend.mcsvauth.exception;

public class AuthException extends RuntimeException {
    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    public static final String ROL_NO_ENCONTRADO = "Rol no encontrado";
    public static final String NOMBRE_USUARIO_VACIO = "El nombre de usuario no puede estar vacío";
    public static final String PASSWORD_VACIO = "La contraseña no puede estar vacía";
    public static final String ID_INVALIDO = "El id no puede ser nulo o menor o igual a 0";

    public AuthException(String message) {
        super(message);
    }
}
