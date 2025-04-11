package backend.mcsvauth.repository;

import backend.mcsvauth.models.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findById(Long id);
}
