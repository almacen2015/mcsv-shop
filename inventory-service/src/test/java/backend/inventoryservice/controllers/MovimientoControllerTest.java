package backend.inventoryservice.controllers;

import backend.dto.request.MovementDtoRequest;
import backend.dto.response.MovementDtoResponse;
import backend.inventoryservice.security.TestSecurityConfig;
import backend.inventoryservice.services.MovimientoService;
import backend.pageable.Paginado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);
        // Arrange
        when(service.listByIdProducto(any(Integer.class), any(Paginado.class))).thenReturn(Page.empty());

        // Act
        mockMvc.perform(post("/api/inventory/2")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void testListarMovimientosPorProducto() throws Exception {
        // Arrange
        MovementDtoResponse response = new MovementDtoResponse(10, 1, 5, "ENTRADA", "2025-01-01");
        Paginado paginado = new Paginado(1, 10, "id");
        Pageable pageable = PageRequest.of(0, 10);
        String json = objectMapper.writeValueAsString(paginado);

        List<MovementDtoResponse> listResponse = List.of(response);

        when(service.listByIdProducto(any(Integer.class), any(Paginado.class))).thenReturn(new PageImpl<>(listResponse, pageable, listResponse.size()));
        // Act
        mockMvc.perform(post("/api/inventory/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].productoId").value(1))
                .andExpect(jsonPath("$.content[0].cantidad").value(5));
    }

    @Test
    void testRegistrarMovimiento() throws Exception {
        // Arrange
        MovementDtoRequest dto = new MovementDtoRequest(1, 1, "ENTRADA");
        String json = objectMapper.writeValueAsString(dto);
        MovementDtoResponse response = new MovementDtoResponse(1, 1, 1, "ENTRADA", "2025-01-01");

        when(service.add(any(MovementDtoRequest.class))).thenReturn(response);
        // Act
        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}