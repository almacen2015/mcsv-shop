package backend.inventoryservice.repositories;

import backend.inventoryservice.models.entities.Movimiento;
import backend.inventoryservice.models.entities.TipoMovimiento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MovimientoRepositoryTest {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE movimientos ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setUp() {
        movimientoRepository.deleteAll();
    }

    @Test
    void testFindMovimientoByProductoId_dadoProductoNoExistente_RetornaVacio() {
        // Arrange
        Movimiento movimiento = Movimiento.builder()
                .productoId(1)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .build();
        movimientoRepository.save(movimiento);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Movimiento> movimientoEncontrado = movimientoRepository.findAllByProductoId(2, pageable);

        // Assert
        assertTrue(movimientoEncontrado.isEmpty());
    }

    @Test
    void testFindMovimientoByProductoId_dadoProductoExistente_RetornaMovimientos() {
        // Arrange
        Movimiento movimiento = Movimiento.builder()
                .productoId(1)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .build();
        movimientoRepository.save(movimiento);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Movimiento> movimientoEncontrado = movimientoRepository.findAllByProductoId(1, pageable);

        // Assert
        assertNotNull(movimientoEncontrado);
        assertThat(movimientoEncontrado.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testGuardarMovimiento_DadoMovimiento_RetornaMovimiento() {
        // Arrange
        Movimiento movimiento = Movimiento.builder()
                .productoId(1)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .build();

        // Act
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        // Assert
        assertNotNull(movimientoGuardado);
        assertEquals(1, movimientoGuardado.getProductoId());
        assertEquals(TipoMovimiento.ENTRADA, movimientoGuardado.getTipoMovimiento());
        assertEquals(10, movimientoGuardado.getCantidad());
    }

}