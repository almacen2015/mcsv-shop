package backend.authservice;

import backend.authservice.models.entity.Permiso;
import backend.authservice.models.entity.Rol;
import backend.authservice.models.entity.RolEnum;
import backend.authservice.models.entity.Usuario;
import backend.authservice.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    @Profile("test")
    CommandLineRunner init(UsuarioRepository usuarioRepository) {
        return args -> {
            Permiso createPermiso = Permiso.builder()
                    .nombre("CREATE")
                    .build();

            Permiso readPermiso = Permiso.builder()
                    .nombre("READ")
                    .build();

            Permiso updatePermiso = Permiso.builder()
                    .nombre("UPDATE")
                    .build();

            Permiso deletePermiso = Permiso.builder()
                    .nombre("DELETE")
                    .build();

            Rol rolAdmin = Rol.builder()
                    .nombre(RolEnum.ADMIN)
                    .permisos(Set.of(createPermiso, readPermiso, updatePermiso, deletePermiso))
                    .build();

            Rol rolDeveloper = Rol.builder()
                    .nombre(RolEnum.DEVELOPER)
                    .permisos(Set.of(createPermiso, readPermiso))
                    .build();

            Rol rolUser = Rol.builder()
                    .nombre(RolEnum.USER)
                    .permisos(Set.of(createPermiso, readPermiso, updatePermiso, deletePermiso))
                    .build();

            Rol rolInvited = Rol.builder()
                    .nombre(RolEnum.INVITED)
                    .permisos(Set.of(readPermiso))
                    .build();

            Usuario usuarioVictor = Usuario.builder()
                    .username("admin")
                    //1234
                    .password("$2a$10$mbkroRGK4o9feC0IBVZa9e8cBjxqDfyXyzYuvEwYTGGAqHh5FM/Bi")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialsNoExpired(true)
                    .roles(Set.of(rolAdmin))
                    .build();

            usuarioRepository.save(usuarioVictor);
        };
    }
}
