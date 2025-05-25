package backend.saleservice.controllers;

import backend.pageable.Paginado;
import backend.saleservice.exceptions.SaleException;
import backend.dto.request.DetailSaleRequestDto;
import backend.dto.request.SaleRequestDto;
import backend.dto.response.DetailSaleDtoResponse;
import backend.dto.response.SaleDtoResponse;
import backend.saleservice.security.TestSecurityConfig;
import backend.saleservice.services.SaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createVenta_whenStockIsZero_returnsError() throws Exception {
        DetailSaleRequestDto detalle1 = new DetailSaleRequestDto(1, 5);
        SaleRequestDto requestDto = new SaleRequestDto(1, List.of(detalle1));

        final String json = objectMapper.writeValueAsString(requestDto);

        when(service.add(any(SaleRequestDto.class))).thenThrow(new SaleException(SaleException.QUANTITY_GREATER_THAN_STOCK));

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.QUANTITY_GREATER_THAN_STOCK));

        verify(service, times(1)).add(eq(requestDto));
    }

    @Test
    void createVenta_whenDetailIsEmpty_returnsError() throws Exception {
        SaleRequestDto requestDto = new SaleRequestDto(1, List.of());

        final String json = objectMapper.writeValueAsString(requestDto);

        when(service.add(any(SaleRequestDto.class))).thenThrow(new SaleException(SaleException.DETAILS_INVALID));

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.DETAILS_INVALID));

        verify(service, times(1)).add(eq(requestDto));
    }

    @Test
    void createVenta_whenClientIdIsNotValid_returnsError() throws Exception {
        DetailSaleRequestDto detalle1 = new DetailSaleRequestDto(1, 10);
        DetailSaleRequestDto detalle2 = new DetailSaleRequestDto(2, 5);
        SaleRequestDto requestDto = new SaleRequestDto(null, List.of(detalle1, detalle2));

        final String json = objectMapper.writeValueAsString(requestDto);

        when(service.add(any(SaleRequestDto.class))).thenThrow(new SaleException(SaleException.CLIENT_ID_INVALID));

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.CLIENT_ID_INVALID));

        verify(service, times(1)).add(eq(requestDto));
    }

    @Test
    void createVenta_whenProductIdIsRepeated_returnsError() throws Exception {
        DetailSaleRequestDto detalle1 = new DetailSaleRequestDto(1, 10);
        DetailSaleRequestDto detalle2 = new DetailSaleRequestDto(1, 5);
        SaleRequestDto requestDto = new SaleRequestDto(1, List.of(detalle1, detalle2));

        final String json = objectMapper.writeValueAsString(requestDto);

        when(service.add(any(SaleRequestDto.class))).thenThrow(new SaleException(SaleException.PRODUCT_REPEATED));

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.PRODUCT_REPEATED));

        verify(service, times(1)).add(eq(requestDto));
    }

    @Test
    void createVenta_whenProductIdIsNotValid_returnsError() throws Exception {
        DetailSaleRequestDto detailSaleRequestDto = new DetailSaleRequestDto(0, 10);
        SaleRequestDto requestDto = new SaleRequestDto(1, List.of(detailSaleRequestDto));

        final String json = objectMapper.writeValueAsString(requestDto);

        when(service.add(any(SaleRequestDto.class))).thenThrow(new SaleException(SaleException.PRODUCT_ID_INVALID));

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.PRODUCT_ID_INVALID));

        verify(service, times(1)).add(eq(requestDto));
    }

    @Test
    void createVenta_whenDataIsValid_returnsVenta() throws Exception {
        DetailSaleRequestDto detailSaleRequestDto = new DetailSaleRequestDto(1, 10);
        SaleRequestDto requestDto = new SaleRequestDto(1, List.of(detailSaleRequestDto));

        DetailSaleDtoResponse detalle1 = constructDetailSaleResponseDto(1, 10, 10.00, 100.00);
        SaleDtoResponse saleDtoResponse1 = constructSaleResponseDto("1", "Victor Orbegozo", "2025-10-10", 75.00, List.of(detalle1));

        final String json = objectMapper.writeValueAsString(requestDto);

        when(service.add(any(SaleRequestDto.class))).thenReturn(saleDtoResponse1);

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saleDtoResponse1.id()))
                .andExpect(jsonPath("$.client").value(saleDtoResponse1.client()));

        verify(service, times(1)).add(eq(requestDto));
    }

    @Test
    void getByClient_whenOrderByIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(1, 10, "");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenThrow(new SaleException(SaleException.SORT_NAME_INVALID));

        mockMvc.perform(post("/api/sales/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SORT_NAME_INVALID));

        verify(service, times(1)).getSalesByClient(1, paginado);
    }

    @Test
    void getByClient_whenSizeNumberIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(1, 0, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenThrow(new SaleException(SaleException.SIZE_NUMBER_INVALID));

        mockMvc.perform(post("/api/sales/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SIZE_NUMBER_INVALID));

        verify(service, times(1)).getSalesByClient(1, paginado);
    }

    @Test
    void getByClient_whenPageNumberIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(0, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenThrow(new SaleException(SaleException.PAGE_NUMBER_INVALID));

        mockMvc.perform(post("/api/sales/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.PAGE_NUMBER_INVALID));

        verify(service, times(1)).getSalesByClient(1, paginado);
    }

    @Test
    void getByClient_whenIdIsNotValid_returnsError() throws Exception {
        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(0, paginado)).thenThrow(new SaleException(SaleException.CLIENT_ID_INVALID));

        mockMvc.perform(post("/api/sales/client/{id}", 0)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.CLIENT_ID_INVALID));

        verify(service, times(1)).getSalesByClient(0, paginado);
    }

    @Test
    void getByClient_whenDataNoExists_returnsPageSale() throws Exception {
        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenReturn(Page.empty());

        mockMvc.perform(post("/api/sales/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(service, times(1)).getSalesByClient(1, paginado);
    }

    @Test
    void getByClient_whenDataExists_returnsPageSale() throws Exception {
        DetailSaleDtoResponse detalle1 = constructDetailSaleResponseDto(1, 10, 10.00, 100.00);
        DetailSaleDtoResponse detalle2 = constructDetailSaleResponseDto(2, 5, 5.00, 25.00);
        SaleDtoResponse saleDtoResponse1 = constructSaleResponseDto("1", "Victor Orbegozo", "2025-10-10", 75.00, List.of(detalle1, detalle2));

        List<SaleDtoResponse> ventas = List.of(saleDtoResponse1);

        Paginado paginado = new Paginado(1, 10, "id");
        String json = objectMapper.writeValueAsString(paginado);

        when(service.getSalesByClient(1, paginado)).thenReturn(new PageImpl<>(ventas));

        mockMvc.perform(post("/api/sales/client/{id}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(service, times(1)).getSalesByClient(1, paginado);
    }

    @Test
    void list_whenOrderByIsNotValid_returnsError() throws Exception {
        when(service.getAll(1, 10, " ")).thenThrow(new SaleException(SaleException.SORT_NAME_INVALID));

        mockMvc.perform(get("/api/sales")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", " ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SORT_NAME_INVALID));

        verify(service, times(1)).getAll(1, 10, " ");
    }

    @Test
    void list_whenSizeNumberIsNotValid_returnsError() throws Exception {
        when(service.getAll(1, -1, "id")).thenThrow(new SaleException(SaleException.SIZE_NUMBER_INVALID));

        mockMvc.perform(get("/api/sales")
                        .param("page", "1")
                        .param("size", "-1")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.SIZE_NUMBER_INVALID));

        verify(service, times(1)).getAll(1, -1, "id");
    }

    @Test
    void list_whenPageNumberIsNotValid_returnsError() throws Exception {
        when(service.getAll(0, 10, "id")).thenThrow(new SaleException(SaleException.PAGE_NUMBER_INVALID));

        mockMvc.perform(get("/api/sales")
                        .param("page", "0")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(SaleException.PAGE_NUMBER_INVALID));

        verify(service, times(1)).getAll(0, 10, "id");
    }

    @Test
    void list_whenDataNoExists_returnsEmpty() throws Exception {
        when(service.getAll(1, 10, "id")).thenReturn(Page.empty());

        mockMvc.perform(get("/api/sales")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(service, times(1)).getAll(1, 10, "id");
    }

    @Test
    void list_whenDataExists_returnsPageSale() throws Exception {
        DetailSaleDtoResponse detalle1 = constructDetailSaleResponseDto(1, 10, 10.00, 100.00);
        DetailSaleDtoResponse detalle2 = constructDetailSaleResponseDto(2, 5, 5.00, 25.00);
        DetailSaleDtoResponse detalle3 = constructDetailSaleResponseDto(1, 10, 5.00, 50.00);
        DetailSaleDtoResponse detalle4 = constructDetailSaleResponseDto(2, 5, 5.00, 25.00);

        SaleDtoResponse saleDtoResponse1 = constructSaleResponseDto("1", "Victor Orbegozo", "2025-10-10", 75.00, List.of(detalle1, detalle2));
        SaleDtoResponse saleDtoResponse2 = constructSaleResponseDto("2", "Luis Torres", "2025-10-10", 75.00, List.of(detalle3, detalle4));

        List<SaleDtoResponse> ventas = List.of(saleDtoResponse1, saleDtoResponse2);

        when(service.getAll(1, 10, "id")).thenReturn(new PageImpl<>(ventas));

        mockMvc.perform(get("/api/sales")
                        .param("page", "1")
                        .param("size", "10")
                        .param("orderBy", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(service, times(1)).getAll(1, 10, "id");

    }

    private DetailSaleDtoResponse constructDetailSaleResponseDto(Integer productId, Integer quantity, Double price, Double subTotal) {
        return new DetailSaleDtoResponse(productId, quantity, price, subTotal);
    }

    private SaleDtoResponse constructSaleResponseDto(String id, String client, String date, Double total, List<DetailSaleDtoResponse> details) {
        return new SaleDtoResponse(id, client, date, total, details);
    }
}