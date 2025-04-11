package backend.clientservice.repositories;

import backend.clientservice.models.entities.Cliente;
import backend.clientservice.models.entities.TipoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void testListAll() {
        Cliente cliente1 = Cliente.builder()
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        clienteRepository.save(cliente1);

        Cliente cliente2 = Cliente.builder()
                .nombre("Maria")
                .apellido("Martinez")
                .numeroDocumento("34567218")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        clienteRepository.save(cliente2);

        List<Cliente> clientes = clienteRepository.findAll();

        assertEquals(1L, clientes.get(0).getId());
        assertEquals(2, clientes.size());
    }

    @Test
    void testAdd() {
        Cliente cliente1 = Cliente.builder()
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        Cliente clientSaved = clienteRepository.save(cliente1);

        assertNotNull(clientSaved.getId());
        assertEquals(1L, clientSaved.getId());
        assertEquals("Victor", clientSaved.getNombre());
    }

    @Test
    void testFinById() {
        Cliente cliente1 = Cliente.builder()
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();
        clienteRepository.save(cliente1);

        Optional<Cliente> clientFound = clienteRepository.findById(1L);

        assertTrue(clientFound.isPresent());
        assertEquals(1L, clientFound.get().getId());
        assertEquals("Victor", clientFound.get().getNombre());
    }
}