package backend.saleservice.services.impl;

import backend.dto.request.MovementDtoRequest;
import backend.dto.request.SaleDtoRequest;
import backend.dto.response.ClientDtoResponse;
import backend.dto.response.DetailSaleDtoResponse;
import backend.dto.response.ProductDtoResponse;
import backend.dto.response.SaleDtoResponse;
import backend.pageable.PageableUtils;
import backend.pageable.Paginado;
import backend.saleservice.client.ClientFeign;
import backend.saleservice.client.InventoryClient;
import backend.saleservice.client.ProductClient;
import backend.saleservice.exceptions.SaleException;
import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import backend.saleservice.models.mapper.DetailSaleMapper;
import backend.saleservice.models.mapper.SaleMapper;
import backend.saleservice.repositories.SaleRepository;
import backend.saleservice.services.SaleService;
import backend.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SaleServiceImpl implements SaleService {
    private final SaleRepository repository;
    private final SaleMapper saleMapper = SaleMapper.INSTANCE;
    private final DetailSaleMapper detailSaleMapper = DetailSaleMapper.INSTANCE;
    private final ProductClient productClient;
    private final InventoryClient movementClient;
    private final ClientFeign clientFeign;

    private double total = 0.0;

    public SaleServiceImpl(SaleRepository repository, ProductClient productClient, InventoryClient movementClient, ClientFeign clientFeign) {
        this.repository = repository;
        this.productClient = productClient;
        this.movementClient = movementClient;
        this.clientFeign = clientFeign;
    }

    @Override
    public Page<SaleDtoResponse> getSalesByClient(Integer clientId, Paginado paginado) {
        PageableUtils.validatePagination(paginado);
        ClientDtoResponse client = validateClientId(clientId);

        Pageable pageable = PageableUtils.constructPageable(paginado);

        Page<Venta> ventas = repository.findByClientId(clientId, pageable);

        List<SaleDtoResponse> response = new ArrayList<>();
        if (!ventas.isEmpty()) {
            response = ventas.getContent().stream()
                    .map(venta -> {
                        List<DetailSaleDtoResponse> detalles = detailSaleMapper.toDtos(venta.getDetails());
                        String fullNameClient = getFullNameClient(client.nombre(), client.apellido());

                        return new SaleDtoResponse(
                                venta.getId(),
                                fullNameClient,
                                venta.getDate().toString(),
                                venta.getTotal(),
                                detalles);

                    }).toList();
        }
        return new PageImpl<>(response, pageable, ventas.getTotalElements());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SaleDtoResponse add(SaleDtoRequest requestDto) {
        String fullName;
        ClientDtoResponse client = validateClientId(requestDto.clientId());
        Venta venta = saleMapper.toEntity(requestDto);
        validateDetails(venta.getDetails());

        venta.setDate(LocalDateTime.now());
        venta.setTotal(total);

        Venta ventaSaved = repository.save(venta);

        fullName = getFullNameClient(client.nombre(), client.apellido());

        List<DetailSaleDtoResponse> detalles = detailSaleMapper.toDtos(venta.getDetails());

        SaleDtoResponse response = new SaleDtoResponse(ventaSaved.getId(), fullName, venta.getDate().toString(), venta.getTotal(), detalles);
        addMovement(venta.getDetails());

        return response;
    }

    private void addMovement(List<DetalleVenta> details) {
        final String TIPO_MOVIMIENTO_SALIDA = "SALIDA";

        for (DetalleVenta detail : details) {
            MovementDtoRequest movimiento = new MovementDtoRequest(detail.getProductId(), detail.getQuantity(), TIPO_MOVIMIENTO_SALIDA);

            movementClient.createMovimientoDto(movimiento);
        }
    }

    @Override
    public Page<SaleDtoResponse> getAll(Integer page, Integer size, String orderBy) {
        Paginado paginado = new Paginado(page, size, orderBy);
        PageableUtils.validatePagination(paginado);
        Pageable pageable = PageableUtils.constructPageable(paginado);

        Page<Venta> ventas = repository.findAll(pageable);
        List<SaleDtoResponse> response = new ArrayList<>();
        for (Venta venta : ventas.getContent()) {
            ClientDtoResponse client = clientFeign.getClient(venta.getClientId().longValue());
            String fullNameClient = getFullNameClient(client.nombre(), client.apellido());

            List<DetailSaleDtoResponse> detalles = detailSaleMapper.toDtos(venta.getDetails());
            SaleDtoResponse ventaDto = new SaleDtoResponse(venta.getId(), fullNameClient, venta.getDate().toString(), venta.getTotal(), detalles);

            response.add(ventaDto);
        }
        return new PageImpl<>(response, pageable, ventas.getTotalElements());
    }

    private String getFullNameClient(String nombre, String apellido) {
        return nombre + " " + apellido;
    }

    private Double calculateSubtotal(Double precio, Integer quantity) {
        double subTotal;
        if (Utils.isNotPositive(precio.intValue())) {
            throw new SaleException(SaleException.PRICE_INVALID);
        }
        subTotal = precio * quantity;
        total = total + subTotal;
        return subTotal;
    }

    private ClientDtoResponse validateClientId(Integer id) {
        if (Utils.isNotPositive(id)) {
            throw new SaleException(SaleException.CLIENT_ID_INVALID);
        }

        ClientDtoResponse client = clientFeign.getClient(id.longValue());
        return client;
    }

    private void validateDetails(List<DetalleVenta> details) {
        Set<Integer> productIds = new HashSet<>();

        if (details == null || details.isEmpty()) {
            throw new SaleException(SaleException.DETAILS_INVALID);
        }

        for (DetalleVenta detail : details) {
            validateQuantity(detail.getQuantity());
            validateProductRepeated(detail, productIds);

            ProductDtoResponse product = productClient.getProduct(detail.getProductId());
            validateProduct(product);

            validateQuantityGreaterThanStock(detail.getQuantity(), product.stock());

            detail.setUnitPrice(product.precio());
            detail.setSubTotal(calculateSubtotal(product.precio(), detail.getQuantity()));
        }
    }

    private void validateProductRepeated(DetalleVenta detail, Set<Integer> productIds) {
        if (!productIds.add(detail.getProductId())) {
            throw new SaleException(SaleException.PRODUCT_REPEATED);
        }
    }

    private void validateProduct(ProductDtoResponse product) {
        if (Optional.ofNullable(product).isEmpty()) {
            throw new SaleException(SaleException.PRODUCT_NOT_FOUND);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (Utils.isNotPositive(quantity)) {
            throw new SaleException(SaleException.QUANTITY_INVALID);
        }
    }

    private void validateQuantityGreaterThanStock(Integer quantity, Integer stock) {
        if (quantity > stock) {
            throw new SaleException(SaleException.QUANTITY_GREATER_THAN_STOCK);
        }
    }
}
