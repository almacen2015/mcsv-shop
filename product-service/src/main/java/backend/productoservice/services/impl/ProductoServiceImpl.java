package backend.productoservice.services.impl;

import backend.productoservice.enums.Estado;
import backend.productoservice.enums.TipoMovimiento;
import backend.productoservice.exceptions.ProductoException;
import backend.productoservice.mappers.ProductoMapper;
import backend.productoservice.models.dto.request.ProductoDtoRequest;
import backend.productoservice.models.dto.response.ProductoDtoResponse;
import backend.productoservice.models.entities.Producto;
import backend.productoservice.repositories.ProductoRepository;
import backend.productoservice.services.ProductoService;
import backend.productoservice.util.Paginado;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper = ProductoMapper.INSTANCE;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductoDtoResponse update(ProductoDtoRequest dto, Integer id) {
        validateData(dto);
        validateId(id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoException(ProductoException.PRODUCT_NOT_FOUND));

        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());

        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toDto(productoGuardado);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateStock(Integer idProducto, Integer cantidad, String tipoMovimiento) {
        validateAmount(cantidad);
        Producto productoEncontrado = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoException(ProductoException.PRODUCT_NOT_FOUND));

        final int stock = productoEncontrado.getStock();
        int stockFinal;
        if (Objects.equals(tipoMovimiento, TipoMovimiento.SALIDA.name())) {
            stockFinal = stock - cantidad;
        } else {
            stockFinal = stock + cantidad;
        }

        validateStock(stockFinal);
        productoEncontrado.setId(productoEncontrado.getId());
        productoEncontrado.setStock(stockFinal);
        productoRepository.save(productoEncontrado);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductoDtoResponse add(ProductoDtoRequest dto) {
        validateData(dto);
        Producto producto = productoMapper.toEntity(dto);
        producto.setEstado(Estado.ACTIVO.getValor());
        producto.setFechaCreacion(LocalDate.now());
        producto.setStock(0);
        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toDto(productoGuardado);
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new ProductoException(ProductoException.INVALID_STOCK);
        }
    }

    private void validateAmount(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ProductoException(ProductoException.AMOUNT_INVALID);
        }
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new ProductoException(ProductoException.INVALID_ID);
        }
    }

    private void validateData(ProductoDtoRequest dto) {
        final String nombre = dto.nombre();
        final String descripcion = dto.descripcion();
        final Double precio = dto.precio();

        validateNombre(nombre);

        validateDescripcion(descripcion);

        validatePrecio(precio);
    }

    private void validatePrecio(Double precio) {
        if (precio == null || precio <= 0) {
            throw new ProductoException(ProductoException.PRODUCT_PRICE_INVALID);
        }
    }

    private void validateDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new ProductoException(ProductoException.PRODUCT_DESCRIPTION_EMPTY);
        }
    }

    private void validateNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ProductoException(ProductoException.PRODUCT_NAME_EMPTY);
        }
    }

    @Override
    public Page<ProductoDtoResponse> listAll(Integer page, Integer size, String orderBy) {
        validatePaginado(page, size, orderBy);
        Pageable pageable = constructPageable(page, size, orderBy);

        Page<Producto> productos = productoRepository.findAll(pageable);

        List<ProductoDtoResponse> response = productos.getContent().stream()
                .map(productoMapper::toDto)
                .toList();

        return new PageImpl<>(response, pageable, productos.getTotalElements());
    }

    @Override
    public ProductoDtoResponse getById(Integer id) {
        if (id == null || id <= 0) {
            throw new ProductoException(ProductoException.INVALID_ID);
        }

        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isEmpty()) {
            return null;
        }

        ProductoDtoResponse response = productoMapper.toDto(producto.get());

        return response;
    }

    @Override
    public Page<ProductoDtoResponse> listByname(String nombre, Paginado paginado) {
        validatePaginado(paginado.page(), paginado.size(), paginado.orderBy());
        validateNombre(nombre);

        Pageable pageable = constructPageable(paginado.page(), paginado.size(), paginado.orderBy());

        Page<Producto> productos = productoRepository.findAllByNombreIgnoreCaseContaining(nombre, pageable);

        if (productos.isEmpty()) {
            return null;
        }

        List<ProductoDtoResponse> response = productos.getContent().stream()
                .map(productoMapper::toDto).toList();

        return new PageImpl<>(response, pageable, productos.getTotalElements());
    }

    private PageRequest constructPageable(Integer page, Integer size, String orderBy) {
        return PageRequest.of(page - 1, size, Sort.by(orderBy).descending());
    }

    private void validatePaginado(Integer page, Integer size, String orderBy) {
        if (page <= 0) {
            throw new ProductoException(ProductoException.PAGE_NUMBER_INVALID);
        }

        if (size <= 0) {
            throw new ProductoException(ProductoException.SIZE_NUMBER_INVALID);
        }

        if (orderBy == null || orderBy.isBlank()) {
            throw new ProductoException(ProductoException.SORT_NAME_INVALID);
        }
    }
}
