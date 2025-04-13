package backend.inventoryservice.controllers;

import backend.inventoryservice.models.dtos.MovimientoDtoRequest;
import backend.inventoryservice.models.dtos.MovimientoDtoResponse;
import backend.inventoryservice.services.MovimientoService;
import backend.inventoryservice.util.Paginado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@SecurityRequirement(name = "BearerAuth")
public class InventoryController {

    private final MovimientoService movimientoService;

    public InventoryController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Registrar movimiento", description = "Registra un nuevo movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping
    public ResponseEntity<MovimientoDtoResponse> add(@RequestBody MovimientoDtoRequest dto) {
        MovimientoDtoResponse response = movimientoService.add(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar movimientos por producto", description = "Lista los movimientos de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos listados"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping("/{idProducto}")
    public ResponseEntity<Page<MovimientoDtoResponse>> listByIdProducto(@PathVariable Integer idProducto, @RequestBody Paginado paginado) {
        Page<MovimientoDtoResponse> response = movimientoService.listByIdProducto(idProducto, paginado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
