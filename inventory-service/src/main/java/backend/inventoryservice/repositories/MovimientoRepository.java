package backend.inventoryservice.repositories;

import backend.inventoryservice.models.entities.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    Page<Movimiento> findAllByProductoId(Integer idProducto, Pageable pageable);
}
