package backend.inventoryservice.services.impl;

import backend.dto.request.MovementDtoRequest;
import backend.dto.response.ProductDtoResponse;
import backend.inventoryservice.client.ProductClient;
import backend.inventoryservice.exceptions.InventoryException;
import backend.inventoryservice.mappers.MovimientoMapper;
import backend.dto.response.MovementDtoResponse;
import backend.inventoryservice.models.entities.Movimiento;
import backend.inventoryservice.models.entities.TipoMovimiento;
import backend.inventoryservice.repositories.MovimientoRepository;
import backend.inventoryservice.services.MovimientoService;
import backend.pageable.PageableUtils;
import backend.pageable.Paginado;
import backend.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper = MovimientoMapper.INSTANCE;
    private final ProductClient productClient;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, ProductClient productClient) {
        this.movimientoRepository = movimientoRepository;
        this.productClient = productClient;
    }

    @Override
    public Page<MovementDtoResponse> listByIdProducto(Integer productId, Paginado paginado) {
        PageableUtils.validatePagination(paginado);
        Utils.validateIdProduct(productId);

        Pageable pageable = PageableUtils.constructPageable(paginado);

        Page<Movimiento> movimientos = movimientoRepository.findAllByProductoId(productId, pageable);
        if (movimientos.isEmpty()) {
            return null;
        }

        List<MovementDtoResponse> response = movimientos.getContent().stream()
                .map(movimientoMapper::toDto).toList();

        return new PageImpl<>(response, pageable, movimientos.getTotalElements());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MovementDtoResponse add(MovementDtoRequest dto) {
        final Integer cantidad = dto.cantidad();
        final String tipoMovimiento = dto.tipoMovimiento();
        final Integer productoId = dto.productoId();

        validateData(cantidad, tipoMovimiento, productoId);
        ProductDtoResponse producto = getProduct(productoId);
        validateStock(producto.stock(), tipoMovimiento);

        productClient.updateStock(productoId, cantidad, tipoMovimiento);

        Movimiento movimiento = movimientoMapper.toEntity(dto);
        movimiento.setFechaRegistro(LocalDateTime.now());
        return movimientoMapper.toDto(movimientoRepository.save(movimiento));
    }

    private void validateStock(Integer stock, String tipoMovimiento) {
        if (Objects.equals(tipoMovimiento, TipoMovimiento.SALIDA.name()) && stock <= 0) {
            throw new InventoryException(InventoryException.MOVEMENT_WITHOUT_STOCK);
        }
    }

    private ProductDtoResponse getProduct(Integer productoId) {
        Optional<ProductDtoResponse> producto = Optional.ofNullable(productClient.getProduct(productoId));
        if (producto.isEmpty()) {
            throw new InventoryException(InventoryException.INVALID_PRODUCT);
        }
        return producto.get();
    }

    private void validateData(Integer cantidad, String tipoMovimiento, Integer idProducto) {
        if (Utils.isNotPositive(cantidad)) {
            throw new InventoryException(InventoryException.INVALID_AMOUNT);
        }

        if (Utils.isBlank(tipoMovimiento)) {
            throw new InventoryException(InventoryException.INVALID_TYPE_MOVEMENT);
        }

        Utils.validateIdProduct(idProducto);
    }
}
