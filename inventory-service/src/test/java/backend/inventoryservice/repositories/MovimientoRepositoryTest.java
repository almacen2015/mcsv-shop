package backend.inventoryservice.repositories;

import backend.inventoryservice.models.entities.Movimiento;
import backend.inventoryservice.models.entities.TipoMovimiento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MovimientoRepositoryTest {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Test
    void testFindMovimientoByProductoId_dadoProductoNoExistente_RetornaVacio() {
        // Arrange
        Movimiento movimiento = Movimiento.builder()
                .productoId(1)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10)
                .build();
        movimientoRepository.save(movimiento);

        // Act
        List<Movimiento> movimientoEncontrado = movimientoRepository.findByProductoId(2);

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

        // Act
        List<Movimiento> movimientoEncontrado = movimientoRepository.findByProductoId(1);

        // Assert
        assertNotNull(movimientoEncontrado);
        assertEquals(1, movimientoEncontrado.get(0).getProductoId());
        assertEquals(TipoMovimiento.ENTRADA, movimientoEncontrado.get(0).getTipoMovimiento());
        assertEquals(10, movimientoEncontrado.get(0).getCantidad());
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