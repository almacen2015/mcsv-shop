package backend.inventoryservice.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productoId;

    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento;

    private LocalDateTime fechaRegistro;
}
