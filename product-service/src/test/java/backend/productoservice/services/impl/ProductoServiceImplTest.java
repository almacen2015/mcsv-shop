package backend.productoservice.services.impl;

import backend.productoservice.exceptions.ProductoException;
import backend.productoservice.models.dto.request.ProductoDtoRequest;
import backend.productoservice.models.dto.response.ProductoDtoResponse;
import backend.productoservice.models.entities.Producto;
import backend.productoservice.repositories.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoServiceImpl service;

    @Test
    void testActualizarProducto_DadoProductoValido_RetornaProductoActualizado() {
        Producto producto = Producto.builder()
                .id(1)
                .nombre("Producto 1")
                .descripcion("Descripcion 1")
                .precio(100.0)
                .build();

        Producto productoActualizado = Producto.builder()
                .id(1)
                .nombre("Producto 2")
                .descripcion("Descripcion 2")
                .precio(200.0)
                .build();

        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 2", "Descripcion 2", 200.0);

        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenReturn(productoActualizado);

        ProductoDtoResponse productoDtoResponse = service.update(dto, 1);

        assertEquals(1, productoDtoResponse.id());
        assertEquals("Producto 2", productoDtoResponse.nombre());
        assertEquals("Descripcion 2", productoDtoResponse.descripcion());
        assertEquals(200.0, productoDtoResponse.precio());
        verify(repository).save(producto);
    }

    @Test
    void testActualizarStock_dadoDatosCorrectosSalida_RetornaProductoActualizado() {
        Producto producto = Producto.builder()
                .id(1)
                .stock(10)
                .descripcion("Descripcion 1")
                .nombre("Producto 1")
                .estado(true)
                .fechaCreacion(LocalDate.of(2024, 1, 1))
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(producto));

        service.updateStock(1, 5, "SALIDA");

        assertEquals(5, producto.getStock());
        verify(repository).save(producto);
    }

    @Test
    void testActualizarStock_DadoDatosCorrectosEntrada_RetornaProductoActualizado() {
        Producto producto = Producto.builder()
                .id(1)
                .stock(10)
                .descripcion("Descripcion 1")
                .nombre("Producto 1")
                .estado(true)
                .fechaCreacion(LocalDate.of(2024, 1, 1))
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(producto));

        service.updateStock(1, 30, "ENTRADA");

        assertEquals(40, producto.getStock());
        verify(repository).save(producto);
    }

    @Test
    void testActualizarStock_DadoProductoNoEncontrado_RetornaError() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ProductoException.class, () -> service.updateStock(1, 10, "ENTRADA"));
    }

    @Test
    void testActualizarStock_DadoStockFinalNegativoSalida_RetornaError() {
        Producto producto = Producto.builder()
                .id(1)
                .stock(10)
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(ProductoException.class, () -> service.updateStock(1, 20, "SALIDA"));
    }

    @Test
    void testActualizarStock_DadoCantidadNegativa_RetornaError() {
        assertThrows(ProductoException.class, () -> service.updateStock(1, -10, "SALIDA"));
    }

//    @Test
//    void testBuscarPorNombre_ProductoNoExiste_RetornaNull() {
//        // Arrange
//        when(repository.findByNombre("Producto 1")).thenReturn(Optional.empty());
//
//        // Act
//        ProductoDtoResponse producto = service.getByName("Producto 1");
//
//        // Assert
//        assertThat(producto).isNull();
//    }

//    @Test
//    void testBuscarPorNombre_ProductoExiste_RetornaProducto() {
//        // Arrange
//        Producto producto = Producto.builder()
//                .id(1)
//                .nombre("Producto 1")
//                .descripcion("Descripcion 1")
//                .precio(100.0)
//                .build();
//
//        // Act
//        when(repository.findByNombre("Producto 1")).thenReturn(Optional.of(producto));
//        ProductoDtoResponse productoDtoResponse = service.getByName("Producto 1");
//
//        // Assert
//        assertThat(productoDtoResponse).isNotNull();
//        assertThat(productoDtoResponse.id()).isEqualTo(1);
//        assertThat(productoDtoResponse.nombre()).isEqualTo("Producto 1");
//        assertThat(productoDtoResponse.descripcion()).isEqualTo("Descripcion 1");
//        assertThat(productoDtoResponse.precio()).isEqualTo(100.0);
//    }

    @Test
    void testBuscarPorId_ProductoExiste_RetornaProducto() {
        // Arrange
        Producto producto = Producto.builder()
                .id(1)
                .nombre("Producto 1")
                .descripcion("Descripcion 1")
                .precio(100.0)
                .build();

        // Act
        when(repository.findById(1)).thenReturn(java.util.Optional.of(producto));
        ProductoDtoResponse productoDtoResponse = service.getById(1);

        // Assert
        assertThat(productoDtoResponse).isNotNull();
        assertThat(productoDtoResponse.id()).isEqualTo(1);
        assertThat(productoDtoResponse.nombre()).isEqualTo("Producto 1");
        assertThat(productoDtoResponse.descripcion()).isEqualTo("Descripcion 1");
        assertThat(productoDtoResponse.precio()).isEqualTo(100.0);
    }

    @Test
    void testBuscarPorId_ProductoNoExiste_RetornaNull() {
        // Arrange

        // Act
        when(repository.findById(1)).thenReturn(java.util.Optional.empty());
        ProductoDtoResponse producto = service.getById(1);

        // Assert
        assertThat(producto).isNull();
    }

//    @Test
//    void testListar_SinProductosEnBD_RetornaListaVacia() {
//        // Arrange
//
//        // Act
//        when(repository.findAll()).thenReturn(List.of());
//        List<ProductoDtoResponse> productos = service.listAll();
//
//        // Assert
//        assertThat(productos).isNotNull();
//        assertThat(productos).isEmpty();
//    }

//    @Test
//    void testListar_ProductosEnBD_RetornaListaProductos() {
//        // Arrange
//        Producto producto1 = Producto.builder()
//                .id(1)
//                .nombre("Producto 1")
//                .descripcion("Descripcion 1")
//                .precio(100.0)
//                .build();
//        Producto producto2 = Producto.builder()
//                .id(2)
//                .nombre("Producto 2")
//                .descripcion("Descripcion 2")
//                .precio(200.0)
//                .build();
//
//        // Act
//        when(repository.findAll()).thenReturn(List.of(producto1, producto2));
//        List<ProductoDtoResponse> productos = service.listAll();
//
//        // Assert
//        assertThat(productos).isNotNull();
//        assertThat(productos).hasSize(2);
//        assertThat(productos.get(0).id()).isEqualTo(1);
//        assertThat(productos.get(0).nombre()).isEqualTo("Producto 1");
//        assertThat(productos.get(0).descripcion()).isEqualTo("Descripcion 1");
//        assertThat(productos.get(0).precio()).isEqualTo(100.0);
//        assertThat(productos.get(1).id()).isEqualTo(2);
//        assertThat(productos.get(1).nombre()).isEqualTo("Producto 2");
//        assertThat(productos.get(1).descripcion()).isEqualTo("Descripcion 2");
//        assertThat(productos.get(1).precio()).isEqualTo(200.0);
//    }

    // Test methods here
    @Test
    void testAgregarProducto_DadoPrecioNegativo_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "Descripcion 1", -100.0);

        // Act & Assert
        assertThrows(ProductoException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoPrecioCero_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "Descripcion 1", 0.0);

        // Act & Assert
        assertThrows(ProductoException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoDescripcionVacia_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "", 100.0);

        // Act & Assert
        assertThrows(ProductoException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoNombreVacio_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("", "Descripcion 1", 100.0);

        // Act & Assert
        assertThrows(ProductoException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoParametrosValidos_RetornaProducto() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "Descripcion 1", 100.0);
        Producto productoGuardado = Producto.builder()
                .id(1)
                .nombre("Producto 1")
                .descripcion("Descripcion 1")
                .precio(100.0)
                .build();

        // Act
        when(repository.save(any(Producto.class))).thenReturn(productoGuardado);
        ProductoDtoResponse productoDtoResponse = service.add(dto);

        // Assert
        assertThat(productoDtoResponse).isNotNull();
        assertThat(productoDtoResponse.id()).isGreaterThan(0);
        assertThat(productoDtoResponse.nombre()).isEqualTo("Producto 1");
        assertThat(productoDtoResponse.descripcion()).isEqualTo("Descripcion 1");
        assertThat(productoDtoResponse.precio()).isEqualTo(100.0);
    }
}