package backend.inventoryservice.controllers;

import backend.dto.request.MovementDtoRequest;
import backend.dto.response.MovementDtoResponse;
import backend.inventoryservice.services.MovimientoService;
import backend.pageable.Paginado;
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
@RequestMapping("/api/inventory")
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
    public ResponseEntity<MovementDtoResponse> add(@RequestBody MovementDtoRequest dto) {
        MovementDtoResponse response = movimientoService.add(dto);
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
    public ResponseEntity<Page<MovementDtoResponse>> listByIdProducto(@PathVariable Integer idProducto, @RequestBody Paginado paginado) {
        Page<MovementDtoResponse> response = movimientoService.listByIdProducto(idProducto, paginado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
