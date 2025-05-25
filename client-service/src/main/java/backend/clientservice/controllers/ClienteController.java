package backend.clientservice.controllers;

import backend.dto.request.ClienteRequestDTO;
import backend.dto.response.ClientDtoResponse;
import backend.clientservice.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClienteController {
    private final ClientService clientService;

    public ClienteController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar Clientes", description = "Lista todos los clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes listados"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping
    public ResponseEntity<Page<ClientDtoResponse>> listAll(@RequestParam Integer page,
                                                           @RequestParam Integer size,
                                                           @RequestParam String orderBy) {
        return new ResponseEntity<>(clientService.listAll(page, size, orderBy), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Registrar un cliente", description = "Registra un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping
    public ResponseEntity<ClientDtoResponse> add(@RequestBody ClienteRequestDTO cliente) {
        return new ResponseEntity<>(clientService.add(cliente), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar un cliente por ID", description = "Busca un cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDtoResponse> getById(@PathVariable Long id) {
        return new ResponseEntity<>(clientService.getById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar cliente por número de documento", description = "Buscar cliente por número de documento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/document/{documentNumber}/{documentType}")
    public ResponseEntity<ClientDtoResponse> getByDocumentNumber(@PathVariable String documentNumber, @PathVariable String documentType) {
        return new ResponseEntity<>(clientService.getByDocumentNumber(documentNumber, documentType), HttpStatus.OK);
    }

}
