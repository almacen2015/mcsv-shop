package backend.saleservice.services.impl;

import backend.saleservice.client.ClientFeign;
import backend.saleservice.client.MovementClient;
import backend.saleservice.client.ProductClient;
import backend.saleservice.exceptions.SaleException;
import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import backend.saleservice.models.dtos.request.DetalleVentaRequestDto;
import backend.saleservice.models.dtos.request.MovimientoDtoRequest;
import backend.saleservice.models.dtos.request.VentaRequestDto;
import backend.saleservice.models.dtos.response.ClienteResponseDTO;
import backend.saleservice.models.dtos.response.MovimientoDtoResponse;
import backend.saleservice.models.dtos.response.ProductoDtoResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataMongoTest
@ActiveProfiles("test-local")
class VentaServiceImplTest {

    @Mock
    private VentaRepository repository;

    @Mock
    private ClientFeign clientFeign;

    @Mock
    private ProductClient productClient;

    @Mock
    private MovementClient movementClient;

    @InjectMocks
    private VentaServiceImpl service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void add_whenPriceIsNotValid_returnsError() {
        DetalleVentaRequestDto detalleVentaRequestDto = new DetalleVentaRequestDto(1, 10);
        VentaRequestDto ventaRequestDto = new VentaRequestDto(1, List.of(detalleVentaRequestDto));
        ProductoDtoResponse productoDtoResponse = new ProductoDtoResponse(
                1,
                "Coca Cola",
                "Coca Cola",
                0.00,
                true,
                LocalDate.now(),
                10
        );

        when(productClient.getProduct(any(Integer.class))).thenReturn(productoDtoResponse);

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));
        assertThat(exception.getMessage()).isEqualTo(SaleException.PRICE_INVALID);
    }

    @Test
    void add_whenStockIsNotValid_returnsError() {
        DetalleVentaRequestDto detalleVentaRequestDto = new DetalleVentaRequestDto(1, 10);
        VentaRequestDto ventaRequestDto = new VentaRequestDto(1, List.of(detalleVentaRequestDto));
        ProductoDtoResponse productoDtoResponse = new ProductoDtoResponse(
                1,
                "Coca Cola",
                "Coca Cola",
                5.00,
                true,
                LocalDate.now(),
                0
        );

        when(productClient.getProduct(any(Integer.class))).thenReturn(productoDtoResponse);

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));
        assertThat(exception.getMessage()).isEqualTo(SaleException.QUANTITY_GREATER_THAN_STOCK);
    }

    @Test
    void add_whenProductIsRepeated_returnsError() {
        DetalleVentaRequestDto detalleVentaRequestDto = new DetalleVentaRequestDto(1, 10);
        DetalleVentaRequestDto detalleVentaRequestDto2 = new DetalleVentaRequestDto(1, 5);
        VentaRequestDto ventaRequestDto = new VentaRequestDto(1, List.of(detalleVentaRequestDto, detalleVentaRequestDto2));

        ProductoDtoResponse productoDtoResponse = new ProductoDtoResponse(
                1,
                "Coca Cola",
                "Coca Cola",
                5.00,
                true,
                LocalDate.now(),
                10
        );

        when(productClient.getProduct(any(Integer.class))).thenReturn(productoDtoResponse);

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.PRODUCT_REPEATED);
    }

    @Test
    void add_whenProductIdIsNotValid_returnsError() {
        DetalleVentaRequestDto detalleVentaRequestDto = new DetalleVentaRequestDto(null, 10);
        VentaRequestDto ventaRequestDto = new VentaRequestDto(1, List.of(detalleVentaRequestDto));

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.PRODUCT_NOT_FOUND);
    }

    @Test
    void add_whenDetailsIsEmpty_returnsError() {
        VentaRequestDto ventaRequestDto = new VentaRequestDto(1, null);
        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.DETAILS_INVALID);
    }

    @Test
    void add_whenClientIdIsMinusOrEqualsZero_returnsError() {
        DetalleVentaRequestDto detalleVentaRequestDto = new DetalleVentaRequestDto(1, 10);
        VentaRequestDto ventaRequestDto = new VentaRequestDto(-1, List.of(detalleVentaRequestDto));

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.CLIENT_ID_INVALID);
    }

    @Test
    void add_whenDataIsValid_returnVenta() {
        DetalleVenta detalleVenta1 = createDetalleVenta(1, 10, 10.00, 100.00);
        List<DetalleVenta> detalles = List.of(detalleVenta1);
        Venta venta1 = createVenta("1", 1, detalles);

        DetalleVentaRequestDto detalleVentaRequestDto = new DetalleVentaRequestDto(1, 10);
        VentaRequestDto ventaRequestDto = new VentaRequestDto(1, List.of(detalleVentaRequestDto));

        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO(
                1L,
                "VICTOR",
                "ORBEGOZO",
                "DNI",
                "2000-10-10",
                "12345678");

        ProductoDtoResponse productoDtoResponse = new ProductoDtoResponse(
                1,
                "Coca Cola",
                "Coca Cola",
                5.00,
                true,
                LocalDate.now(),
                10
        );

        MovimientoDtoResponse movimientoDtoResponse = new MovimientoDtoResponse(1, 1, 10, "SALIDA", "2025-10-10");

        when(repository.save(any(Venta.class))).thenReturn(venta1);
        when(clientFeign.getClient(any(Long.class))).thenReturn(clienteResponseDTO);
        when(productClient.getProduct(any(Integer.class))).thenReturn(productoDtoResponse);
        when(movementClient.createMovimientoDto(any(MovimientoDtoRequest.class))).thenReturn(movimientoDtoResponse);

        VentaResponseDto response = service.add(ventaRequestDto);

        assertThat(response).isNotNull();
    }

    @Test
    void getSalesByClient_whenOrderByIsNotValid_returnsError() {
        Paginado paginado = new Paginado(1, 10, null);

        SaleException exception = assertThrows(SaleException.class, () -> service.getSalesByClient(1, paginado));

        assertThat(exception.getMessage()).isEqualTo(SaleException.SORT_NAME_INVALID);
    }

    @Test
    void getSalesByClient_whenSizeIsNotValid_returnsError() {
        Paginado paginado = new Paginado(1, null, "id");
        SaleException exception = assertThrows(SaleException.class, () -> service.getSalesByClient(1, paginado));
        assertThat(exception.getMessage()).isEqualTo(SaleException.SIZE_NUMBER_INVALID);
    }

    @Test
    void getSalesByClient_whenPageIsNotValid_returnsError() {
        Paginado paginado = new Paginado(0, 10, "id");

        SaleException exception = assertThrows(SaleException.class, () -> service.getSalesByClient(1, paginado));

        assertThat(exception.getMessage()).isEqualTo(SaleException.PAGE_NUMBER_INVALID);
    }

    @Test
    void getSalesByClient_whenClientIdIsNotValid_returnsError() {
        Paginado paginado = new Paginado(1, 10, "id");

        assertThrows(SaleException.class, () -> service.getSalesByClient(0, paginado));
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
    void getAll_whenOrderByIsNotValid_returnsError() {
        SaleException exception = assertThrows(SaleException.class, () -> service.getAll(1, 10, "   "));

        assertThat(exception.getMessage()).isEqualTo(SaleException.SORT_NAME_INVALID);
    }

    @Test
    void getAll_whenSizeIsNotValid_returnsError() {
        SaleException exception = assertThrows(SaleException.class, () -> service.getAll(1, 0, "id"));

        assertThat(exception.getMessage()).isEqualTo(SaleException.SIZE_NUMBER_INVALID);
    }

    @Test
    void getAll_whenPageIsNotValid_returnsError() {
        SaleException exception = assertThrows(SaleException.class, () -> service.getAll(0, 10, "id"));

        assertThat(exception.getMessage()).isEqualTo(SaleException.PAGE_NUMBER_INVALID);
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