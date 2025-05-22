package backend.clientservice.services.impl;

import backend.clientservice.exceptions.ClienteException;
import backend.clientservice.models.dtos.ClienteRequestDTO;
import backend.clientservice.models.dtos.ClienteResponseDTO;
import backend.clientservice.models.entities.Cliente;
import backend.clientservice.models.entities.TipoDocumento;
import backend.clientservice.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientServiceImpl service;

    @Test
    void testGetByDocumentNumber_whenDocumentTypeIsInvalid_returnError() {
        assertThrows(ClienteException.class, () -> {
            service.getByDocumentNumber("12345678", "AAA");
        });
    }

    @Test
    void testGetByDocumentNumber_whenDocumentIsInvalid_returnError() {
        assertThrows(ClienteException.class, () -> {
            service.getByDocumentNumber("1234", TipoDocumento.DNI.name());
        });
    }

    @Test
    void testGetByDocumentNumber_whenDocumentIsValid_returnClient() {
        Cliente cliente1 = Cliente.builder()
                .id(1L)
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        when(repository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(cliente1));

        ClienteResponseDTO response = service.getByDocumentNumber("12345678", TipoDocumento.DNI.name());

        assertThat(response).isNotNull();
        assertEquals(1, response.id());
    }

    @Test
    void testGetById_whenIdIsNotFound_returnError() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClienteException.class, () -> service.getById(1L));
    }

    @Test
    void testGetById_whenIdIsLessThanZero_returnError() {
        assertThrows(ClienteException.class, () -> service.getById(-1L));
    }

    @Test
    void testGetById_whenIdIsZero_returnError() {
        assertThrows(ClienteException.class, () -> service.getById(0L));
    }

    @Test
    void testGetById_whenIdIsNull_returnError() {
        assertThrows(ClienteException.class, () -> service.getById(null));
    }

    @Test
    void testGetById_whenIdIsValid_returnClient() {
        Cliente cliente1 = Cliente.builder()
                .id(1L)
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(cliente1));

        ClienteResponseDTO response = service.getById(1L);

        assertThat(response).isNotNull();
        assertEquals(1, response.id());
        assertEquals("Victor", response.nombre());

    }

    @Test
    void testAdd_whenFechaNacimientoIsInvalid_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Victor",
                "Orbegozo",
                "12345678",
                "DNI",
                "19-19-19");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenFechaNacimientoIsBlank_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Victor",
                "Orbegozo",
                "12345678",
                "DNI",
                " ");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenFechaNacimientoIsEmpty_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Victor",
                "Orbegozo",
                "12345678",
                "DNI",
                "");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenFechaNacimientoIsNull_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Victor",
                "Orbegozo",
                "12345678",
                "AAAAA",
                null);
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenTipoDocumentoIsInvalid_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "12345678", "AAAAA", "1994-05-04");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenTipoDocumentoIsBlank_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "12345678", "   ", "1994-05-04");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenTipoDocumentoIsNull_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "12345678", null, "1994-05-04");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenTipoDocumentoIsEmpty_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "12345678", "", "1994-05-04");
        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenDocumentNumberExists_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Victor",
                "Orbegozo",
                "12345678",
                "DNI",
                "1994-05-04");

        when(repository.existsByNumeroDocumento("12345678")).thenReturn(Boolean.TRUE);

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenNumeroDocumentoIsDniAndNumeroDocumentoIsDifferent8Characters_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "123456789", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenNumeroDocumentoIsBlank_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "   ", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenNumeroDocumentoIsNull_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", null, "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenNumeroDocumentoIsEmpty_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenApellidoIsBlank_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "    ", "12345678", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenApellidoIsNull_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", null, "12345678", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenApellidoIsEmpty_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "", "12345678", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenNameIsBlank_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("     ", "Orbegozo", "12345678", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }


    @Test
    void testAdd_whenNameIsNull_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO(null, "Orbegozo", "12345678", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenNameIsEmpty_returnError() {
        ClienteRequestDTO dto = new ClienteRequestDTO("", "Orbegozo", "12345678", "DNI", "1994-05-04");

        assertThrows(ClienteException.class, () -> service.add(dto));
    }

    @Test
    void testAdd_whenDataValid_ReturnClient() {
        Cliente cliente1 = Cliente.builder()
                .id(1L)
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        ClienteRequestDTO dto = new ClienteRequestDTO("Victor", "Orbegozo", "12345678", "DNI", "1994-05-04");

        when(repository.save(any(Cliente.class))).thenReturn(cliente1);

        ClienteResponseDTO response = service.add(dto);

        assertThat(response).isNotNull();
        assertEquals(1, response.id());
        assertEquals("Victor", response.nombre());
        assertEquals("Orbegozo", response.apellido());
        assertEquals("12345678", response.numeroDocumento());
        assertEquals("DNI", response.tipoDocumento());
    }

    @Test
    void testListAll_whenDataEmpty_returnEmpty() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<ClienteResponseDTO> clientes = service.listAll(1, 10, "id");

        assertThat(clientes).isEmpty();
    }

    @Test
    void testListAll_returnClients() {
        Cliente cliente1 = Cliente.builder()
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        Cliente cliente2 = Cliente.builder()
                .nombre("Maria")
                .apellido("Martinez")
                .numeroDocumento("34567218")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        List<Cliente> listClientes = List.of(cliente1, cliente2);

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(listClientes, pageable, listClientes.size()));

        Page<ClienteResponseDTO> clientes = service.listAll(1, 10, "id");

        assertThat(clientes).hasSize(2);
    }
}