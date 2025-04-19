package backend.saleservice.controllers;

import backend.saleservice.models.dtos.response.DetailSaleResponseDto;
import backend.saleservice.models.dtos.response.SaleResponseDto;
import backend.saleservice.security.TestSecurityConfig;
import backend.saleservice.services.SaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void getAll_whenDataExists_returnsPageSale() throws Exception {
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