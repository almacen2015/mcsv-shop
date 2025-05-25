package backend.saleservice.services.impl;

import backend.dto.response.ClientDtoResponse;
import backend.dto.response.MovementDtoResponse;
import backend.dto.response.ProductDtoResponse;
import backend.exception.UtilException;
import backend.pageable.Paginado;
import backend.saleservice.client.ClientFeign;
import backend.saleservice.client.InventoryClient;
import backend.saleservice.client.ProductClient;
import backend.saleservice.exceptions.SaleException;
import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import backend.dto.request.DetailSaleRequestDto;
import backend.dto.request.MovementDtoRequest;
import backend.dto.request.SaleRequestDto;
import backend.dto.response.SaleDtoResponse;
import backend.saleservice.repositories.SaleRepository;
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
    private SaleRepository repository;

    @Mock
    private ClientFeign clientFeign;

    @Mock
    private ProductClient productClient;

    @Mock
    private InventoryClient movementClient;

    @InjectMocks
    private SaleServiceImpl service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void add_whenPriceIsNotValid_returnsError() {
        DetailSaleRequestDto detalleVentaRequestDto = new DetailSaleRequestDto(1, 10);
        SaleRequestDto ventaRequestDto = new SaleRequestDto(1, List.of(detalleVentaRequestDto));
        ProductDtoResponse productoDtoResponse = new ProductDtoResponse(
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
        DetailSaleRequestDto detalleVentaRequestDto = new DetailSaleRequestDto(1, 10);
        SaleRequestDto ventaRequestDto = new SaleRequestDto(1, List.of(detalleVentaRequestDto));
        ProductDtoResponse productoDtoResponse = new ProductDtoResponse(
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
        DetailSaleRequestDto detalleVentaRequestDto = new DetailSaleRequestDto(1, 10);
        DetailSaleRequestDto detalleVentaRequestDto2 = new DetailSaleRequestDto(1, 5);
        SaleRequestDto ventaRequestDto = new SaleRequestDto(1, List.of(detalleVentaRequestDto, detalleVentaRequestDto2));

        ProductDtoResponse productoDtoResponse = new ProductDtoResponse(
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
        DetailSaleRequestDto detalleVentaRequestDto = new DetailSaleRequestDto(null, 10);
        SaleRequestDto ventaRequestDto = new SaleRequestDto(1, List.of(detalleVentaRequestDto));

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.PRODUCT_NOT_FOUND);
    }

    @Test
    void add_whenDetailsIsEmpty_returnsError() {
        SaleRequestDto ventaRequestDto = new SaleRequestDto(1, null);
        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.DETAILS_INVALID);
    }

    @Test
    void add_whenClientIdIsMinusOrEqualsZero_returnsError() {
        DetailSaleRequestDto detalleVentaRequestDto = new DetailSaleRequestDto(1, 10);
        SaleRequestDto ventaRequestDto = new SaleRequestDto(-1, List.of(detalleVentaRequestDto));

        SaleException exception = assertThrows(SaleException.class, () -> service.add(ventaRequestDto));

        assertThat(exception.getMessage()).isEqualTo(SaleException.CLIENT_ID_INVALID);
    }

    @Test
    void add_whenDataIsValid_returnVenta() {
        DetalleVenta detalleVenta1 = createDetalleVenta(1, 10, 10.00, 100.00);
        List<DetalleVenta> detalles = List.of(detalleVenta1);
        Venta venta1 = createVenta("1", 1, detalles);

        DetailSaleRequestDto detalleVentaRequestDto = new DetailSaleRequestDto(1, 10);
        SaleRequestDto ventaRequestDto = new SaleRequestDto(1, List.of(detalleVentaRequestDto));

        ClientDtoResponse clienteResponseDTO = new ClientDtoResponse(
                1L,
                "VICTOR",
                "ORBEGOZO",
                "DNI",
                "2000-10-10",
                "12345678");

        ProductDtoResponse productoDtoResponse = new ProductDtoResponse(
                1,
                "Coca Cola",
                "Coca Cola",
                5.00,
                true,
                LocalDate.now(),
                10
        );

        MovementDtoResponse movimientoDtoResponse = new MovementDtoResponse(1, 1, 10, "SALIDA", "2025-10-10");

        when(repository.save(any(Venta.class))).thenReturn(venta1);
        when(clientFeign.getClient(any(Long.class))).thenReturn(clienteResponseDTO);
        when(productClient.getProduct(any(Integer.class))).thenReturn(productoDtoResponse);
        when(movementClient.createMovimientoDto(any(MovementDtoRequest.class))).thenReturn(movimientoDtoResponse);

        SaleDtoResponse response = service.add(ventaRequestDto);

        assertThat(response).isNotNull();
    }

    @Test
    void getSalesByClient_whenOrderByIsNotValid_returnsError() {
        Paginado paginado = new Paginado(1, 10, null);

        UtilException exception = assertThrows(UtilException.class, () -> service.getSalesByClient(1, paginado));

        assertThat(exception.getMessage()).isEqualTo(UtilException.SORT_NAME_INVALID);
    }

    @Test
    void getSalesByClient_whenSizeIsNotValid_returnsError() {
        Paginado paginado = new Paginado(1, null, "id");
        UtilException exception = assertThrows(UtilException.class, () -> service.getSalesByClient(1, paginado));
        assertThat(exception.getMessage()).isEqualTo(UtilException.SIZE_NUMBER_INVALID);
    }

    @Test
    void getSalesByClient_whenPageIsNotValid_returnsError() {
        Paginado paginado = new Paginado(0, 10, "id");

        UtilException exception = assertThrows(UtilException.class, () -> service.getSalesByClient(1, paginado));

        assertThat(exception.getMessage()).isEqualTo(UtilException.PAGE_NUMBER_INVALID);
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

        Page<SaleDtoResponse> response = service.getSalesByClient(1, paginado);

        assertThat(response.getContent()).isEmpty();
    }

    @Test
    void getSalesByClient_whenSalesExists_returnsPageVentas() {
        DetalleVenta detalleVenta1 = createDetalleVenta(1, 10, 10.00, 100.00);
        DetalleVenta detalleVenta2 = createDetalleVenta(2, 5, 20.00, 200.00);

        List<DetalleVenta> detalles = List.of(detalleVenta1, detalleVenta2);

        Venta venta1 = createVenta("1", 1, detalles);
        Venta venta2 = createVenta("2", 1, detalles);

        ClientDtoResponse clienteResponseDTO = new ClientDtoResponse(
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

        Page<SaleDtoResponse> response = service.getSalesByClient(1, paginado);

        assertThat(response.getContent().size()).isEqualTo(2);
    }

    @Test
    void getAll_whenOrderByIsNotValid_returnsError() {
        UtilException exception = assertThrows(UtilException.class, () -> service.getAll(1, 10, "   "));

        assertThat(exception.getMessage()).isEqualTo(UtilException.SORT_NAME_INVALID);
    }

    @Test
    void getAll_whenSizeIsNotValid_returnsError() {
        UtilException exception = assertThrows(UtilException.class, () -> service.getAll(1, 0, "id"));

        assertThat(exception.getMessage()).isEqualTo(UtilException.SIZE_NUMBER_INVALID);
    }

    @Test
    void getAll_whenPageIsNotValid_returnsError() {
        UtilException exception = assertThrows(UtilException.class, () -> service.getAll(0, 10, "id"));

        assertThat(exception.getMessage()).isEqualTo(UtilException.PAGE_NUMBER_INVALID);
    }

    @Test
    void getAll_whenVentaNoExists_returnsEmptyList() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<SaleDtoResponse> response = service.getAll(1, 10, "id");

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

        ClientDtoResponse clienteResponseDTO = new ClientDtoResponse(
                1L,
                "VICTOR",
                "ORBEGOZO",
                "DNI",
                "2000-10-10",
                "12345678");

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(ventas));
        when(clientFeign.getClient(any(Long.class))).thenReturn(clienteResponseDTO);

        Page<SaleDtoResponse> response = service.getAll(1, 10, "id");

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