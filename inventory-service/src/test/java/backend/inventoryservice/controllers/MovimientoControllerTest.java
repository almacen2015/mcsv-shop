package backend.inventoryservice.controllers;

import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.inventoryservice.security.TestSecurityConfig;
import backend.inventoryservice.services.MovimientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@Import(TestSecurityConfig.class)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovimientoService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testListarMovimientosPorProducto_dadoQueNoHayMovimientos() throws Exception {
        // Arrange
        when(service.listByIdProducto(2)).thenReturn(List.of());
        // Act
        mockMvc.perform(get("/api/movimiento/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testListarMovimientosPorProducto() throws Exception {
        // Arrange
        MovimientoDtoResponse response = new MovimientoDtoResponse(10, 1, 5, "ENTRADA", "2025-01-01");
        when(service.listByIdProducto(1)).thenReturn(List.of(response));
        // Act
        mockMvc.perform(get("/api/movimiento/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].productoId").value(1))
                .andExpect(jsonPath("$[0].cantidad").value(5));
    }

    @Test
    void testRegistrarMovimiento() throws Exception {
        // Arrange
        MovimientoDtoRequest dto = new MovimientoDtoRequest(1, 1, "ENTRADA");
        String json = objectMapper.writeValueAsString(dto);
        MovimientoDtoResponse response = new MovimientoDtoResponse(1, 1, 1, "ENTRADA", "2025-01-01");

        when(service.add(any(MovimientoDtoRequest.class))).thenReturn(response);
        // Act
        mockMvc.perform(post("/api/movimiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}