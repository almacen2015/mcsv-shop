package backend.saleservice.models.documents;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVenta {
    private Integer productId;
    private Integer quantity;
    private Double unitPrice;
    private Double subTotal;
}
