package backend.saleservice.services.impl;

import backend.saleservice.client.ClientFeign;
import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import backend.saleservice.models.dtos.response.ClienteResponseDTO;
import backend.saleservice.models.dtos.response.VentaResponseDto;
import backend.saleservice.repositories.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataMongoTest
@ActiveProfiles("test-local")
class VentaServiceImplTest {

    @Mock
    private VentaRepository repository;

    @Mock
    private ClientFeign clientFeign;

    @InjectMocks
    private VentaServiceImpl service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void findAll_whenVentaNoExists_returnsEmptyList() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<VentaResponseDto> response = service.getAll(1, 10, "id");

        assertThat(response.getContent()).isEmpty();
    }

    @Test
    void findAll_whenVentaExists_returnPageVenta() {
        DetalleVenta detalleVenta = DetalleVenta.builder()
                .productId(1)
                .quantity(10)
                .unitPrice(10.00)
                .subTotal(100.00)
                .build();

        DetalleVenta detalleVenta2 = DetalleVenta.builder()
                .productId(2)
                .quantity(10)
                .unitPrice(20.00)
                .subTotal(200.00)
                .build();

        Venta venta = Venta.builder()
                .id("1")
                .clientId(1)
                .date(LocalDateTime.now())
                .total(100.00)
                .details(List.of(detalleVenta, detalleVenta2))
                .build();

        Venta venta2 = Venta.builder()
                .id("2")
                .clientId(2)
                .date(LocalDateTime.now())
                .total(100.00)
                .details(List.of(detalleVenta, detalleVenta2))
                .build();

        List<Venta> ventas = List.of(venta, venta2);

        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO(
                1L,
                "VICTOR",
                "ORBEGOZO",
                "DNI",
                "2000-10-10",
                "12345678");

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(ventas));
        when(clientFeign.getClient(any(Long.class))).thenReturn(clienteResponseDTO);

        Page<VentaResponseDto> response = service.getAll(1, 10, "id");

        assertThat(response).isNotNull();
        assertThat(response.getContent().size()).isEqualTo(2);
    }
}