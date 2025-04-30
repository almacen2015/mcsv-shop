package backend.productservice.controllers;

import backend.productservice.models.dto.request.ProductoDtoRequest;
import backend.productservice.models.dto.response.ProductoDtoResponse;
import backend.productservice.services.ProductoService;
import backend.productservice.util.Paginado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "BearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Agregar producto", description = "Agrega un nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductoDtoResponse> add(@RequestBody ProductoDtoRequest dto) {
        ProductoDtoResponse producto = productoService.add(dto);
        return new ResponseEntity<>(producto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar productos", description = "Lista todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos listados")
    })
    @GetMapping()
    public ResponseEntity<Page<ProductoDtoResponse>> list(@RequestParam Integer page,
                                                          @RequestParam Integer size,
                                                          @RequestParam String orderBy) {
        return new ResponseEntity<>(productoService.listAll(page, size, orderBy), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Buscar producto por id", description = "Busca un producto por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> getById(@PathVariable Integer id) {
        ProductoDtoResponse producto = productoService.getById(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Buscar producto por nombre", description = "Busca un producto por su nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/nombre/{nombre}")
    public ResponseEntity<Page<ProductoDtoResponse>> getByName(@PathVariable String nombre, @RequestBody Paginado paginado) {
        return new ResponseEntity<>(productoService.listByname(nombre, paginado), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> update(@RequestBody ProductoDtoRequest dto, @PathVariable Integer id) {
        ProductoDtoResponse producto = productoService.update(dto, id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Actualizar stock", description = "Actualiza el stock de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/stock/{idProducto}/{cantidad}/{tipoMovimiento}")
    public ResponseEntity<Void> updateStock(@PathVariable Integer idProducto, @PathVariable Integer cantidad, @PathVariable String tipoMovimiento) {
        productoService.updateStock(idProducto, cantidad, tipoMovimiento);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
