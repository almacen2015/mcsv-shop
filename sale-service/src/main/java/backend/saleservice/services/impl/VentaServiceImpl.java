package backend.saleservice.services.impl;

import backend.saleservice.client.ClientFeign;
import backend.saleservice.client.MovementClient;
import backend.saleservice.client.ProductClient;
import backend.saleservice.exceptions.SaleException;
import backend.saleservice.models.documents.DetalleVenta;
import backend.saleservice.models.documents.Venta;
import backend.saleservice.models.dtos.request.MovimientoDtoRequest;
import backend.saleservice.models.dtos.request.VentaRequestDto;
import backend.saleservice.models.dtos.response.ClienteResponseDTO;
import backend.saleservice.models.dtos.response.ProductoDtoResponse;
import backend.saleservice.models.dtos.response.VentaResponseDto;
import backend.saleservice.models.mapper.VentaMapper;
import backend.saleservice.repositories.VentaRepository;
import backend.saleservice.services.VentaService;
import backend.saleservice.util.Paginado;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VentaServiceImpl implements VentaService {
    private final VentaRepository repository;
    private final VentaMapper ventaMapper = VentaMapper.INSTANCE;
    private final ProductClient productClient;
    private final MovementClient movementClient;
    private final ClientFeign clientFeign;

    private double total = 0.0;

    public VentaServiceImpl(VentaRepository repository, ProductClient productClient, MovementClient movementClient, ClientFeign clientFeign) {
        this.repository = repository;
        this.productClient = productClient;
        this.movementClient = movementClient;
        this.clientFeign = clientFeign;
    }

    @Override
    public Page<VentaResponseDto> getSalesByClient(Integer clientId, Paginado paginado) {
        validatePaginado(paginado);
        ClienteResponseDTO client = validateClientId(clientId);

        Pageable pageable = constructPageable(paginado);

        Page<Venta> ventas = repository.findByClientId(clientId, pageable);

        List<VentaResponseDto> response = new ArrayList<>();
        if (!ventas.isEmpty()) {
            response = ventas.getContent().stream()
                    .map(venta -> new VentaResponseDto(
                            venta.getId(),
                            getFullNameClient(client.nombre(), client.apellido()),
                            venta.getDate().toString(),
                            venta.getTotal(),
                            venta.getDetails()
                    )).toList();
        }
        return new PageImpl<>(response, pageable, ventas.getTotalElements());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public VentaResponseDto add(VentaRequestDto requestDto) {
        String fullName;
        ClienteResponseDTO client = validateClientId(requestDto.clientId());
        Venta venta = ventaMapper.toEntity(requestDto);
        validateDetails(venta.getDetails());

        venta.setDate(LocalDateTime.now());
        venta.setTotal(total);

        Venta ventaSaved = repository.save(venta);

        fullName = getFullNameClient(client.nombre(), client.apellido());

        VentaResponseDto response = new VentaResponseDto(ventaSaved.getId(), fullName, venta.getDate().toString(), venta.getTotal(), venta.getDetails());
        addMovement(venta.getDetails());

        return response;
    }

    private void addMovement(List<DetalleVenta> details) {
        final String TIPO_MOVIMIENTO = "SALIDA";

        for (DetalleVenta detail : details) {
            MovimientoDtoRequest movimiento = new MovimientoDtoRequest(detail.getProductId(), detail.getQuantity(), TIPO_MOVIMIENTO);

            movementClient.createMovimientoDto(movimiento);
        }
    }

    @Override
    public Page<VentaResponseDto> getAll(Integer page, Integer size, String orderBy) {
        validatePaginado(page, size, orderBy);
        Pageable pageable = constructPageable(page, size, orderBy);

        Page<Venta> ventas = repository.findAll(pageable);
        List<VentaResponseDto> response = new ArrayList<>();
        for (Venta venta : ventas.getContent()) {
            ClienteResponseDTO client = clientFeign.getClient(venta.getClientId().longValue());
            String fullNameClient = getFullNameClient(client.nombre(), client.apellido());
            VentaResponseDto ventaDto = new VentaResponseDto(venta.getId(), fullNameClient, venta.getDate().toString(), venta.getTotal(), venta.getDetails());
            response.add(ventaDto);
        }
        return new PageImpl<>(response, pageable, ventas.getTotalElements());
    }

    private String getFullNameClient(String nombre, String apellido) {
        return nombre + " " + apellido;
    }

    private Double calculateSubtotal(Double precio, Integer quantity) {
        double subTotal;
        if (precio == null || precio <= 0) {
            throw new SaleException(SaleException.PRICE_INVALID);
        }
        subTotal = precio * quantity;
        total = total + subTotal;
        return subTotal;
    }

    private ClienteResponseDTO validateClientId(Integer id) {
        if (id == null || id <= 0) {
            throw new SaleException(SaleException.CLIENT_ID_INVALID);
        }

        ClienteResponseDTO client = clientFeign.getClient(id.longValue());
        return client;
    }

    private PageRequest constructPageable(Paginado paginado) {
        return PageRequest.of(paginado.page() - 1, paginado.size(), Sort.by(paginado.orderBy()).descending());
    }

    private void validatePaginado(Paginado paginado) {
        if (paginado.page() == null || paginado.page() <= 0) {
            throw new SaleException(SaleException.PAGE_NUMBER_INVALID);
        }

        if (paginado.size() == null || paginado.size() <= 0) {
            throw new SaleException(SaleException.SIZE_NUMBER_INVALID);
        }

        if (paginado.orderBy() == null || paginado.orderBy().isBlank()) {
            throw new SaleException(SaleException.SORT_NAME_INVALID);
        }
    }

    private void validateDetails(List<DetalleVenta> details) {
        Set<Integer> productIds = new HashSet<>();

        if (details == null || details.isEmpty()) {
            throw new SaleException(SaleException.DETAILS_INVALID);
        }

        for (DetalleVenta detail : details) {
            validateQuantity(detail.getQuantity());
            validateProductRepeated(detail, productIds);

            ProductoDtoResponse product = productClient.getProduct(detail.getProductId());
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

    private void validateProduct(ProductoDtoResponse product) {
        if (Optional.ofNullable(product).isEmpty()) {
            throw new SaleException(SaleException.PRODUCT_NOT_FOUND);
        }
    }

    private void validateProductId(Integer id) {
        if (id == null || id >= 0) {
            throw new SaleException(SaleException.PRODUCT_ID_INVALID);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new SaleException(SaleException.QUANTITY_INVALID);
        }
    }

    private void validateQuantityGreaterThanStock(Integer quantity, Integer stock) {
        if (quantity > stock) {
            throw new SaleException(SaleException.QUANTITY_GREATER_THAN_STOCK);
        }
    }

    private PageRequest constructPageable(Integer page, Integer size, String orderBy) {
        return PageRequest.of(page - 1, size, Sort.by(orderBy).descending());
    }

    private void validatePaginado(Integer page, Integer size, String orderBy) {
        if (page <= 0) {
            throw new SaleException(SaleException.PAGE_NUMBER_INVALID);
        }

        if (size <= 0) {
            throw new SaleException(SaleException.SIZE_NUMBER_INVALID);
        }

        if (orderBy == null || orderBy.isBlank()) {
            throw new SaleException(SaleException.SORT_NAME_INVALID);
        }
    }
}
