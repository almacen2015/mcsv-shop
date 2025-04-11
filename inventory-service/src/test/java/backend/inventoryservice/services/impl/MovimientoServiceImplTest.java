package backend.inventoryservice.services.impl;

import backend.inventoryservice.client.ProductoClient;
import backend.inventoryservice.exceptions.MovimientoException;
import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.inventoryservice.models.dtos.ProductoDtoResponse;
import backend.inventoryservice.models.entities.Movimiento;
import backend.inventoryservice.models.entities.TipoMovimiento;
import backend.inventoryservice.repositories.MovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private MovimientoServiceImpl service;

    @Mock
    private ProductoClient productoClient;

    @Test
    void testListarMovimientosPorProducto_DadoIdEsMenorIgualZero_RetornaError() {
        // Arrange
        Integer idProducto = 0;

        // Act
        assertThrows(MovimientoException.class, () -> service.listByIdProducto(idProducto));
    }

    @Test
    void testListarMovimientosPorProducto_DadoProductoNoTieneMovimientos_RetornaListaVacia() {
        // Arrange
        when(movimientoRepository.findByProductoId(1)).thenReturn(List.of());

        // Act
        List<MovimientoDtoResponse> movimientos = service.listByIdProducto(1);

        // Assert
        assertTrue(movimientos.isEmpty());
    }

    @Test
    void testListarMovimientosPorProducto_DadoProductoTieneMovimientos_RetornaMovimientos() {
        // Arrange
        Movimiento movimiento = Movimiento.builder()
                .id(1)
                .productoId(1)
                .cantidad(10)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .fechaRegistro(LocalDateTime.of(2021, 10, 10, 10, 10, 10))
                .build();

        when(movimientoRepository.findByProductoId(1)).thenReturn(java.util.List.of(movimiento));

        // Act
        List<MovimientoDtoResponse> movimientos = service.listByIdProducto(1);

        // Assert
        assertNotNull(movimientos);
        assertEquals(1, movimientos.size());
        assertEquals(1, movimientos.get(0).id());
    }

    @Test
    void testRegistrarMovimiento_tipoMovimientoSalidaSinStock_retornaMovimientoException() {
        // Arrange
        MovimientoDtoRequest dto = new MovimientoDtoRequest(1, 10, "SALIDA");

        // Act
        when(productoClient.getProduct(1)).thenReturn(new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.of(2021, 10, 10), 0));

        assertThrows(MovimientoException.class, () -> service.add(dto));
        // Assert
    }

    @Test
    void testRegistrarMovimiento_productoNoExiste_retornaMovimientoException() {
        // Arrange
        MovimientoDtoRequest dto = new MovimientoDtoRequest(1, 10, "ENTRADA");

        // Act
        when(productoClient.getProduct(1)).thenReturn(null);

        assertThrows(MovimientoException.class, () -> service.add(dto));
        // Assert
    }

    @Test
    void testRegistrarMovimiento_cantidadMenorZero_retornaMovimientoException() {
        // Arrange
        MovimientoDtoRequest dto = new MovimientoDtoRequest(1, -10, "ENTRADA");

        // Act

        assertThrows(MovimientoException.class, () -> service.add(dto));
        // Assert
    }

    @Test
    void TestRegistrarMovimiento_MovimientoValido_RetornaMovimiento() {
        // Arrange
        MovimientoDtoRequest dto = new MovimientoDtoRequest(1, 10, "ENTRADA");
        Movimiento movimiento = Movimiento.builder()
                .id(1)
                .productoId(1)
                .cantidad(10)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .fechaRegistro(LocalDateTime.of(2021, 10, 10, 10, 10, 10))
                .build();


        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        when(productoClient.getProduct(1)).thenReturn(new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.of(2021, 10, 10), 10));

        MovimientoDtoResponse movimientoDtoResponse = service.add(dto);

        // Assert
        assertNotNull(movimientoDtoResponse);
        assertEquals(1, movimientoDtoResponse.id());

    }

}