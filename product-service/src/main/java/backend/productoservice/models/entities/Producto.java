package backend.productoservice.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "productos")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
    @Column(name = "descripcion", nullable = false, unique = true)
    private String descripcion;
    private Double precio;
    private Boolean estado;
    private LocalDate fechaCreacion;
    private Integer stock;
}
