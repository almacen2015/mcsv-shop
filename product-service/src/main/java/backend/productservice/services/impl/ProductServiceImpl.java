package backend.productservice.services.impl;

import backend.pageable.Paginado;
import backend.productservice.enums.Estado;
import backend.productservice.enums.TipoMovimiento;
import backend.productservice.exceptions.ProductException;
import backend.productservice.mappers.ProductoMapper;
import backend.dto.request.ProductDtoRequest;
import backend.dto.response.ProductDtoResponse;
import backend.productservice.models.entities.Producto;
import backend.productservice.repositories.ProductoRepository;
import backend.productservice.services.ProductService;
import backend.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static backend.pageable.PageableUtils.constructPageable;
import static backend.pageable.PageableUtils.validatePagination;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper = ProductoMapper.INSTANCE;

    public ProductServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductDtoResponse update(ProductDtoRequest dto, Integer id) {
        validateData(dto);
        Utils.validateIdProduct(id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductException.PRODUCT_NOT_FOUND));

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
                .orElseThrow(() -> new ProductException(ProductException.PRODUCT_NOT_FOUND));

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
    public ProductDtoResponse add(ProductDtoRequest dto) {
        validateData(dto);
        Producto producto = productoMapper.toEntity(dto);
        producto.setEstado(Estado.ACTIVO.getValor());
        producto.setFechaCreacion(LocalDate.now());
        producto.setStock(0);
        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toDto(productoGuardado);
    }

    private void validateStock(Integer stock) {
        if (Utils.isNotPositive(stock)) {
            throw new ProductException(ProductException.INVALID_STOCK);
        }
    }

    private void validateAmount(Integer cantidad) {
        if (Utils.isNotPositive(cantidad)) {
            throw new ProductException(ProductException.AMOUNT_INVALID);
        }
    }

    private void validateData(ProductDtoRequest dto) {
        final String nombre = dto.nombre();
        final String descripcion = dto.descripcion();
        final Double precio = dto.precio();

        validateNombre(nombre);

        validateDescripcion(descripcion);

        validatePrecio(precio);
    }

    private void validatePrecio(Double precio) {
        if (Utils.isNotPositive(precio.intValue())) {
            throw new ProductException(ProductException.PRODUCT_PRICE_INVALID);
        }
    }

    private void validateDescripcion(String descripcion) {
        if (Utils.isBlank(descripcion)) {
            throw new ProductException(ProductException.PRODUCT_DESCRIPTION_EMPTY);
        }
    }

    private void validateNombre(String nombre) {
        if (Utils.isBlank(nombre)) {
            throw new ProductException(ProductException.PRODUCT_NAME_EMPTY);
        }
    }

    @Override
    public Page<ProductDtoResponse> listAll(Integer page, Integer size, String orderBy) {
        Paginado paginado = new Paginado(page, size, orderBy);
        validatePagination(paginado);
        Pageable pageable = constructPageable(paginado);

        Page<Producto> productos = productoRepository.findAll(pageable);

        List<ProductDtoResponse> response = productos.getContent().stream()
                .map(productoMapper::toDto)
                .toList();

        return new PageImpl<>(response, pageable, productos.getTotalElements());
    }

    @Override
    public ProductDtoResponse getById(Integer id) {
        Utils.validateIdProduct(id);

        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isEmpty()) {
            return null;
        }

        ProductDtoResponse response = productoMapper.toDto(producto.get());

        return response;
    }

    @Override
    public Page<ProductDtoResponse> listByname(String nombre, Paginado paginado) {
        validatePagination(paginado);
        validateNombre(nombre);

        Pageable pageable = constructPageable(paginado);

        Page<Producto> productos = productoRepository.findAllByNombreIgnoreCaseContaining(nombre, pageable);

        if (productos.isEmpty()) {
            return null;
        }

        List<ProductDtoResponse> response = productos.getContent().stream()
                .map(productoMapper::toDto).toList();

        return new PageImpl<>(response, pageable, productos.getTotalElements());
    }
}
