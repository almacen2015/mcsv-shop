package backend.productservice.controllers;

import backend.pageable.Paginado;
import backend.dto.request.ProductDtoRequest;
import backend.dto.response.ProductDtoResponse;
import backend.productservice.services.ProductService;
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

    private final ProductService productService;

    public ProductoController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Agregar producto", description = "Agrega un nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductDtoResponse> add(@RequestBody ProductDtoRequest dto) {
        ProductDtoResponse producto = productService.add(dto);
        return new ResponseEntity<>(producto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar productos", description = "Lista todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos listados")
    })
    @GetMapping()
    public ResponseEntity<Page<ProductDtoResponse>> list(@RequestParam Integer page,
                                                         @RequestParam Integer size,
                                                         @RequestParam String orderBy) {
        return new ResponseEntity<>(productService.listAll(page, size, orderBy), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Buscar producto por id", description = "Busca un producto por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDtoResponse> getById(@PathVariable Integer id) {
        ProductDtoResponse producto = productService.getById(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Buscar producto por nombre", description = "Busca un producto por su nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/nombre/{nombre}")
    public ResponseEntity<Page<ProductDtoResponse>> getByName(@PathVariable String nombre, @RequestBody Paginado paginado) {
        return new ResponseEntity<>(productService.listByname(nombre, paginado), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDtoResponse> update(@RequestBody ProductDtoRequest dto, @PathVariable Integer id) {
        ProductDtoResponse producto = productService.update(dto, id);
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
        productService.updateStock(idProducto, cantidad, tipoMovimiento);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
