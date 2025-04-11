package backend.productoservice.controllers;

import backend.productoservice.models.dto.request.ProductoDtoRequest;
import backend.productoservice.models.dto.response.ProductoDtoResponse;
import backend.productoservice.security.TestSecurityConfig;
import backend.productoservice.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import(TestSecurityConfig.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testActualizarProducto() throws Exception {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 2", "Descripcion 1", 100.0);
        String json = objectMapper.writeValueAsString(dto);
        ProductoDtoResponse producto = new ProductoDtoResponse(1, "Producto 2", "Descripcion 1", 100.0, true, LocalDate.now(), 10);
        // Act
        when(service.update(any(ProductoDtoRequest.class), any(Integer.class))).thenReturn(producto);
        // Assert
        mockMvc.perform(patch("/api/producto/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testActualizarStock() throws Exception {
        // Arrange
        ProductoDtoResponse producto = new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.now(), 10);

        // Act
        doNothing().when(service).updateStock(1, 5, "SALIDA");

        // Assert
        mockMvc.perform(put("/api/producto/stock/{idProducto}/{cantidad}/{tipoMovimiento}", 1, 5, "SALIDA"))
                .andExpect(status().isOk());
    }

//    @Test
//    void testBuscarPorNombre() throws Exception {
//        // Arrange
//        ProductoDtoResponse producto = new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.now(), 10);
//        // Act
//        when(service.getByName("Producto 1")).thenReturn(producto);
//        // Assert
//        mockMvc.perform(get("/api/producto/nombre/{nombre}", "Producto 1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.nombre").value("Producto 1"))
//                .andExpect(jsonPath("$.descripcion").value("Descripcion 1"))
//                .andExpect(jsonPath("$.precio").value(100.0))
//                .andExpect(jsonPath("$.estado").value(true));
//    }

    @Test
    void testBuscarPorId() throws Exception {
        // Arrange
        ProductoDtoResponse producto = new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.now(), 10);
        // Act
        when(service.getById(1)).thenReturn(producto);
        // Assert
        mockMvc.perform(get("/api/producto/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto 1"))
                .andExpect(jsonPath("$.descripcion").value("Descripcion 1"))
                .andExpect(jsonPath("$.precio").value(100.0))
                .andExpect(jsonPath("$.estado").value(true));
    }

//    @Test
//    void testListarProductos() throws Exception {
//        // Arrange
//        ProductoDtoResponse producto1 = new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.now(), 20);
//        ProductoDtoResponse producto2 = new ProductoDtoResponse(2, "Producto 2", "Descripcion 2", 200.0, true, LocalDate.now(), 20);
//        // Act
//        when(service.listAll()).thenReturn(List.of(producto1, producto2));
//        // Assert
//        mockMvc.perform(get("/api/producto"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
//                .andExpect(jsonPath("$[0].descripcion").value("Descripcion 1"))
//                .andExpect(jsonPath("$[0].precio").value(100.0))
//                .andExpect(jsonPath("$[0].estado").value(true))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].nombre").value("Producto 2"))
//                .andExpect(jsonPath("$[1].descripcion").value("Descripcion 2"))
//                .andExpect(jsonPath("$[1].precio").value(200.0))
//                .andExpect(jsonPath("$[1].estado").value(true));
//    }

    // Test methods here
    @Test
    void testAgregarProducto() throws Exception {
        // Arrange
        ProductoDtoRequest dto = new ProductoDtoRequest("Producto 1", "Descripcion 1", 100.0);
        String json = objectMapper.writeValueAsString(dto);
        // Act
        ProductoDtoResponse producto = new ProductoDtoResponse(1, "Producto 1", "Descripcion 1", 100.0, true, LocalDate.now(), 10);
        when(service.add(any(ProductoDtoRequest.class))).thenReturn(producto);
        // Assert
        mockMvc.perform(post("/api/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

}