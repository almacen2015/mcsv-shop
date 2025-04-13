package backend.inventoryservice.services.impl;

import backend.inventoryservice.client.ProductoClient;
import backend.inventoryservice.exceptions.InventoryException;
import backend.inventoryservice.mappers.MovimientoMapper;
import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.inventoryservice.models.dtos.ProductoDtoResponse;
import backend.inventoryservice.models.entities.Movimiento;
import backend.inventoryservice.models.entities.TipoMovimiento;
import backend.inventoryservice.repositories.MovimientoRepository;
import backend.inventoryservice.services.MovimientoService;
import backend.inventoryservice.util.Paginado;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper = MovimientoMapper.INSTANCE;
    private final ProductoClient productoClient;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, ProductoClient productoClient) {
        this.movimientoRepository = movimientoRepository;
        this.productoClient = productoClient;
    }

    @Override
    public Page<MovimientoDtoResponse> listByIdProducto(Integer productId, Paginado paginado) {
        validatePaginado(paginado.page(), paginado.size(), paginado.orderBy());
        validateId(productId);

        Pageable pageable = constructPageable(paginado.page(), paginado.size(), paginado.orderBy());

        Page<Movimiento> movimientos = movimientoRepository.findAllByProductoId(productId, pageable);
        if (movimientos.isEmpty()) {
            return null;
        }

        List<MovimientoDtoResponse> response = movimientos.getContent().stream()
                .map(movimientoMapper::toDto).toList();

        return new PageImpl<>(response, pageable, movimientos.getTotalElements());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MovimientoDtoResponse add(MovimientoDtoRequest dto) {
        final Integer cantidad = dto.cantidad();
        final String tipoMovimiento = dto.tipoMovimiento();
        final Integer productoId = dto.productoId();

        validateData(cantidad, tipoMovimiento, productoId);
        ProductoDtoResponse producto = getProduct(productoId);
        validateStock(producto.stock(), tipoMovimiento);

        productoClient.updateStock(productoId, cantidad, tipoMovimiento);

        Movimiento movimiento = movimientoMapper.toEntity(dto);
        movimiento.setFechaRegistro(LocalDateTime.now());
        return movimientoMapper.toDto(movimientoRepository.save(movimiento));
    }

    private void validateStock(Integer stock, String tipoMovimiento) {
        if (Objects.equals(tipoMovimiento, TipoMovimiento.SALIDA.name()) && stock <= 0) {
            throw new InventoryException(InventoryException.MOVEMENT_WITHOUT_STOCK);
        }
    }

    private ProductoDtoResponse getProduct(Integer productoId) {
        Optional<ProductoDtoResponse> producto = Optional.ofNullable(productoClient.getProduct(productoId));
        if (producto.isEmpty()) {
            throw new InventoryException(InventoryException.INVALID_PRODUCT);
        }
        return producto.get();
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new InventoryException(InventoryException.INVALID_ID);
        }
    }

    private void validateData(Integer cantidad, String tipoMovimiento, Integer idProducto) {
        if (cantidad == null || cantidad <= 0) {
            throw new InventoryException(InventoryException.INVALID_AMOUNT);
        }

        if (tipoMovimiento == null || tipoMovimiento.isBlank()) {
            throw new InventoryException(InventoryException.INVALID_TYPE_MOVEMENT);
        }

        validateId(idProducto);
    }

    private PageRequest constructPageable(Integer page, Integer size, String orderBy) {
        return PageRequest.of(page - 1, size, Sort.by(orderBy).descending());
    }

    private void validatePaginado(Integer page, Integer size, String orderBy) {
        if (page <= 0) {
            throw new InventoryException(InventoryException.PAGE_NUMBER_INVALID);
        }

        if (size <= 0) {
            throw new InventoryException(InventoryException.SIZE_NUMBER_INVALID);
        }

        if (orderBy == null || orderBy.isBlank()) {
            throw new InventoryException(InventoryException.SORT_NAME_INVALID);
        }
    }
}
