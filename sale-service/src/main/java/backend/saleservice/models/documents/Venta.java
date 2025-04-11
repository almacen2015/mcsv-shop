package backend.saleservice.models.documents;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    private String id;
    private Integer clientId;
    private LocalDateTime date;
    private Double total;
    private List<DetalleVenta> details;
}
