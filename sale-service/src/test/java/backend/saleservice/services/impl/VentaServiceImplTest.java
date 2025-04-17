package backend.saleservice.services.impl;

import backend.saleservice.client.ClientFeign;
import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import backend.saleservice.models.dtos.response.ClienteResponseDTO;
import backend.saleservice.models.dtos.response.VentaResponseDto;
import backend.saleservice.repositories.VentaRepository;
import backend.saleservice.util.Paginado;
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
    void getSalesByClient_whenSalesNoExists_returnsEmptyList() {
        Paginado paginado = new Paginado(1, 10, "id");
        when(repository.findByClientId(any(Integer.class), any(Pageable.class))).thenReturn(Page.empty());

        Page<VentaResponseDto> response = service.getSalesByClient(1, paginado);

        assertThat(response.getContent()).isEmpty();
    }

    @Test
    void getSalesByClient_whenSalesExists_returnsPageVentas() {
        DetalleVenta detalleVenta1 = createDetalleVenta(1, 10, 10.00, 100.00);
        DetalleVenta detalleVenta2 = createDetalleVenta(2, 5, 20.00, 200.00);

        List<DetalleVenta> detalles = List.of(detalleVenta1, detalleVenta2);

        Venta venta1 = createVenta("1", 1, detalles);
        Venta venta2 = createVenta("2", 1, detalles);

        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO(
                1L,
                "VICTOR",
                "ORBEGOZO",
                "DNI",
                "2000-10-10",
                "12345678");

        List<Venta> ventas = List.of(venta1, venta2);
        Paginado paginado = new Paginado(1, 10, "id");

        when(repository.findByClientId(any(Integer.class), any(Pageable.class))).thenReturn(new PageImpl<>(ventas));
        when(clientFeign.getClient(any(Long.class))).thenReturn(clienteResponseDTO);

        Page<VentaResponseDto> response = service.getSalesByClient(1, paginado);

        assertThat(response.getContent().size()).isEqualTo(2);
    }

    @Test
    void getAll_whenVentaNoExists_returnsEmptyList() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<VentaResponseDto> response = service.getAll(1, 10, "id");

        assertThat(response.getContent()).isEmpty();
    }

    @Test
    void getAll_whenVentaExists_returnsPageVenta() {
        DetalleVenta detalleVenta1 = createDetalleVenta(1, 10, 10.00, 100.00);
        DetalleVenta detalleVenta2 = createDetalleVenta(2, 10, 20.00, 200.00);

        List<DetalleVenta> detalles = List.of(detalleVenta1, detalleVenta2);

        Venta venta1 = createVenta("1", 1, detalles);
        Venta venta2 = createVenta("2", 2, detalles);

        List<Venta> ventas = List.of(venta1, venta2);

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

    private DetalleVenta createDetalleVenta(Integer productId, Integer quantity, Double unitPrice, Double subTotal) {
        return DetalleVenta.builder()
                .productId(productId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .subTotal(subTotal)
                .build();
    }

    private Venta createVenta(String id, Integer clientId, List<DetalleVenta> detalles) {
        return Venta.builder()
                .id(id)
                .clientId(clientId)
                .date(LocalDateTime.now())
                .total(detalles.stream().mapToDouble(DetalleVenta::getSubTotal).sum())
                .details(detalles)
                .build();
    }
}