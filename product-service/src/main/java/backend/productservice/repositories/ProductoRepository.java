package backend.productservice.repositories;

import backend.productservice.models.entities.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Page<Producto> findAllByNombreIgnoreCaseContaining(String nombre, Pageable paginado);
}
