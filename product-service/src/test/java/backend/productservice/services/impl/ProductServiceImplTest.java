package backend.productservice.services.impl;

import backend.productservice.exceptions.ProductException;
import backend.productservice.models.dto.request.ProductoDtoRequest;
import backend.productservice.models.dto.response.ProductoDtoResponse;
import backend.productservice.models.entities.Producto;
import backend.productservice.repositories.ProductoRepository;
import backend.productservice.util.Paginado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

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

        assertThrows(ProductException.class, () -> service.updateStock(1, 10, "ENTRADA"));
    }

    @Test
    void testActualizarStock_DadoStockFinalNegativoSalida_RetornaError() {
        Producto producto = Producto.builder()
                .id(1)
                .stock(10)
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(ProductException.class, () -> service.updateStock(1, 20, "SALIDA"));
    }

    @Test
    void testActualizarStock_DadoCantidadNegativa_RetornaError() {
        assertThrows(ProductException.class, () -> service.updateStock(1, -10, "SALIDA"));
    }

    @Test
    void testBuscarPorNombre_ProductoNoExiste_RetornaNull() {
        Paginado paginado = new Paginado(1, 10, "id");

        // Arrange
        when(repository.findAllByNombreIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<ProductoDtoResponse> producto = service.listByname("Producto 1", paginado);

        // Assert
        assertThat(producto).isNull();
    }

    @Test
    void testBuscarPorNombre_ProductoExiste_RetornaProducto() {
        // Arrange
        Producto producto = Producto.builder()
                .id(1)
                .nombre("Producto 1")
                .descripcion("Descripcion 1")
                .precio(100.0)
                .estado(true)
                .fechaCreacion(LocalDate.of(2024, 1, 1))
                .stock(20)
                .build();

        List<Producto> listaProductos = Collections.singletonList(producto);

        Pageable pageable = PageRequest.of(1 - 1, 10);

        Paginado paginado = new Paginado(1, 10, "id");

        Page<Producto> page = new PageImpl<>(listaProductos, pageable, listaProductos.size());

        // Act
        when(repository.findAllByNombreIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        Page<ProductoDtoResponse> productoDtoResponse = service.listByname("Producto 1", paginado);

        // Assert
        assertThat(productoDtoResponse).isNotNull();
        assertThat(productoDtoResponse.getTotalElements()).isEqualTo(1);

    }

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

    @Test
    void testListar_SinProductosEnBD_RetornaListaVacia() {
        // Arrange
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        // Act
        Page<ProductoDtoResponse> productos = service.listAll(1, 10, "id");

        // Assert
        assertThat(productos.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testListar_ProductosEnBD_RetornaListaProductos() {
        // Arrange
        Producto producto1 = Producto.builder()
                .id(1)
                .nombre("Producto 1")
                .descripcion("Descripcion 1")
                .precio(100.0)
                .estado(true)
                .fechaCreacion(LocalDate.of(2024, 1, 1))
                .stock(20)
                .build();
        Producto producto2 = Producto.builder()
                .id(2)
                .nombre("Producto 2")
                .descripcion("Descripcion 2")
                .precio(200.0)
                .estado(true)
                .fechaCreacion(LocalDate.of(2024, 1, 1))
                .stock(40)
                .build();

        List<Producto> listaProductos = Arrays.asList(producto1, producto2);
        Pageable pageable = PageRequest.of(1 - 1, 10);

        // Act
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(listaProductos, pageable, listaProductos.size()));
        Page<ProductoDtoResponse> productos = service.listAll(1, 10, "id");

        // Assert
        assertThat(productos).isNotNull();
        assertThat(productos).hasSize(2);
    }

    // Test methods here
    @Test
    void testAgregarProducto_DadoPrecioNegativo_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "Descripcion 1", -100.0);

        // Act & Assert
        assertThrows(ProductException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoPrecioCero_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "Descripcion 1", 0.0);

        // Act & Assert
        assertThrows(ProductException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoDescripcionVacia_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "", 100.0);

        // Act & Assert
        assertThrows(ProductException.class, () -> service.add(dto));
    }

    @Test
    void testAgregarProducto_DadoNombreVacio_RetornaError() {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("", "Descripcion 1", 100.0);

        // Act & Assert
        assertThrows(ProductException.class, () -> service.add(dto));
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