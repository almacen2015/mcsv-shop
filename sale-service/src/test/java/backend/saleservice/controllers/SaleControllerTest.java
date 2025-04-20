package backend.saleservice.controllers;

import backend.saleservice.exceptions.SaleException;
import backend.saleservice.models.dtos.response.DetailSaleResponseDto;
import backend.saleservice.models.dtos.response.SaleResponseDto;
import backend.saleservice.security.TestSecurityConfig;
import backend.saleservice.services.SaleService;
import backend.saleservice.util.Paginado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@Import(TestSecurityConfig.class)
class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getByClient_whenOrderByIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(1, 10, "");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenThrow(new SaleException(SaleException.SORT_NAME_INVALID));

        mockMvc.perform(post("/api/ventas/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SORT_NAME_INVALID));
    }

    @Test
    void getByClient_whenSizeNumberIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(1, 0, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenThrow(new SaleException(SaleException.SIZE_NUMBER_INVALID));

        mockMvc.perform(post("/api/ventas/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SIZE_NUMBER_INVALID));
    }

    @Test
    void getByClient_whenPageNumberIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(0, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenThrow(new SaleException(SaleException.PAGE_NUMBER_INVALID));

        mockMvc.perform(post("/api/ventas/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.PAGE_NUMBER_INVALID));
    }

    @Test
    void getByClient_whenIdIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(0, paginado)).thenThrow(new SaleException(SaleException.CLIENT_ID_INVALID));

        mockMvc.perform(post("/api/ventas/client/{id}", 0)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.CLIENT_ID_INVALID));
    }

    @Test
    void getByClient_whenDataNoExists_returnsPageSale() throws Exception {
        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenReturn(Page.empty());

        mockMvc.perform(post("/api/ventas/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getByClient_whenDataExists_returnsPageSale() throws Exception {
        DetailSaleResponseDto detalle1 = constructDetailSaleResponseDto(1, 10, 10.00, 100.00);
        DetailSaleResponseDto detalle2 = constructDetailSaleResponseDto(2, 5, 5.00, 25.00);
        SaleResponseDto saleResponseDto1 = constructSaleResponseDto("1", "Victor Orbegozo", "2025-10-10", 75.00, List.of(detalle1, detalle2));

        List<SaleResponseDto> ventas = List.of(saleResponseDto1);

        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenReturn(new PageImpl<>(ventas));

        mockMvc.perform(post("/api/ventas/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void list_whenOrderByIsNotValid_returnsError() throws Exception {
        when(service.getAll(1, 10, " ")).thenThrow(new SaleException(SaleException.SORT_NAME_INVALID));

        mockMvc.perform(get("/api/ventas")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", " ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SORT_NAME_INVALID));
    }

    @Test
    void list_whenSizeNumberIsNotValid_returnsError() throws Exception {
        when(service.getAll(1, -1, "id")).thenThrow(new SaleException(SaleException.SIZE_NUMBER_INVALID));

        mockMvc.perform(get("/api/ventas")
                        .param("page", "1")
                        .param("size", "-1")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SIZE_NUMBER_INVALID));
    }

    @Test
    void list_whenPageNumberIsNotValid_returnsError() throws Exception {
        when(service.getAll(0, 10, "id")).thenThrow(new SaleException(SaleException.PAGE_NUMBER_INVALID));

        mockMvc.perform(get("/api/ventas")
                        .param("page", "0")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.PAGE_NUMBER_INVALID));
    }

    @Test
    void list_whenDataNoExists_returnsEmpty() throws Exception {
        when(service.getAll(1, 10, "id")).thenReturn(Page.empty());

        mockMvc.perform(get("/api/ventas")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void list_whenDataExists_returnsPageSale() throws Exception {
        DetailSaleResponseDto detalle1 = constructDetailSaleResponseDto(1, 10, 10.00, 100.00);
        DetailSaleResponseDto detalle2 = constructDetailSaleResponseDto(2, 5, 5.00, 25.00);
        DetailSaleResponseDto detalle3 = constructDetailSaleResponseDto(1, 10, 5.00, 50.00);
        DetailSaleResponseDto detalle4 = constructDetailSaleResponseDto(2, 5, 5.00, 25.00);

        SaleResponseDto saleResponseDto1 = constructSaleResponseDto("1", "Victor Orbegozo", "2025-10-10", 75.00, List.of(detalle1, detalle2));
        SaleResponseDto saleResponseDto2 = constructSaleResponseDto("2", "Luis Torres", "2025-10-10", 75.00, List.of(detalle3, detalle4));

        List<SaleResponseDto> ventas = List.of(saleResponseDto1, saleResponseDto2);

        when(service.getAll(1, 10, "id")).thenReturn(new PageImpl<>(ventas));

        mockMvc.perform(get("/api/ventas")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

    }

    private DetailSaleResponseDto constructDetailSaleResponseDto(Integer productId, Integer quantity, Double price, Double subTotal) {
        return new DetailSaleResponseDto(productId, quantity, price, subTotal);
    }

    private SaleResponseDto constructSaleResponseDto(String id, String nameClient, String date, Double total, List<DetailSaleResponseDto> details) {
        return new SaleResponseDto(id, nameClient, date, total, details);
    }
}