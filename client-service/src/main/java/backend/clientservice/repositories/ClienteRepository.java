package backend.clientservice.repositories;

import backend.clientservice.models.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);

    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
}
