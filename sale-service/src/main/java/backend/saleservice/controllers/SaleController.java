package backend.saleservice.controllers;

import backend.pageable.Paginado;
import backend.saleservice.models.dtos.request.SaleRequestDto;
import backend.saleservice.models.dtos.response.SaleResponseDto;
import backend.saleservice.services.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Registra venta", description = "Registra venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Acceso no autorizado")
    })
    @PostMapping
    public ResponseEntity<SaleResponseDto> createVenta(@RequestBody SaleRequestDto requestDto) {
        return new ResponseEntity<>(service.add(requestDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar ventas", description = "Lista todas las ventas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas listadas"),
            @ApiResponse(responseCode = "401", description = "Acceso no autorizado")
    })
    @GetMapping
    public ResponseEntity<Page<SaleResponseDto>> list(@RequestParam Integer page,
                                                      @RequestParam Integer size,
                                                      @RequestParam String orderBy) {
        return new ResponseEntity<>(service.getAll(page, size, orderBy), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar ventas por cliente", description = "Lista ventas por cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas listadas"),
            @ApiResponse(responseCode = "401", description = "Acceso no autorizado")
    })
    @PostMapping("/client/{id}")
    public ResponseEntity<Page<SaleResponseDto>> getByClient(@RequestBody Paginado paginado, @PathVariable Integer id) {
        return new ResponseEntity<>(service.getSalesByClient(id, paginado), HttpStatus.OK);
    }
}
