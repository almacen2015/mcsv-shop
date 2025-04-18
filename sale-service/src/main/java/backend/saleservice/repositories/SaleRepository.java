package backend.saleservice.repositories;

import backend.saleservice.models.documents.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SaleRepository extends MongoRepository<Venta, String> {
    Page<Venta> findByClientId(Integer clientId, Pageable pageable);
}
