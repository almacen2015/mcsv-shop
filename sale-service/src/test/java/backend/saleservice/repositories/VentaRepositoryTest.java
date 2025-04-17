package backend.saleservice.repositories;

import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test-local")
class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @BeforeEach
    void setUp() {
        ventaRepository.deleteAll();
    }

    @Test
    void findAllByClient_whenVentaNoExists_returnEmpty() {
        Page<Venta> ventas = ventaRepository.findByClientId(1, PageRequest.of(0, 10));

        assertThat(ventas).isEmpty();
    }

    @Test
    void findAllByClient_whenVentaExists_returnPageVenta() {
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
                .clientId(1)
                .date(LocalDateTime.now())
                .total(100.00)
                .details(List.of(detalleVenta, detalleVenta2))
                .build();

        ventaRepository.save(venta);
        ventaRepository.save(venta2);

        Page<Venta> ventas = ventaRepository.findByClientId(1, PageRequest.of(0, 10));

        assertThat(ventas).isNotNull();
        assertThat(ventas.getContent()).hasSize(2);
    }

    @Test
    void save() {
        DetalleVenta detalleVenta = DetalleVenta.builder()
                .productId(1)
                .quantity(10)
                .unitPrice(10.00)
                .subTotal(100.00)
                .build();

        Venta venta = Venta.builder()
                .id(null)
                .clientId(1)
                .date(LocalDateTime.now())
                .total(100.00)
                .details(List.of(detalleVenta))
                .build();

        Venta savedVenta = ventaRepository.save(venta);

        assertThat(savedVenta.getId()).isNotNull();
        assertThat(savedVenta.getClientId()).isEqualTo(venta.getClientId().intValue());
    }

    @Test
    void findAll_whenVentasExists_ReturnPageVentas() {
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

        ventaRepository.save(venta);
        ventaRepository.save(venta2);

        Page<Venta> ventas = ventaRepository.findAll(PageRequest.of(0, 10));

        assertThat(ventas.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findAll_whenVentasNotExists_ReturnEmpty() {
        Page<Venta> ventas = ventaRepository.findAll(PageRequest.of(0, 10));

        assertThat(ventas).isEmpty();
    }

}