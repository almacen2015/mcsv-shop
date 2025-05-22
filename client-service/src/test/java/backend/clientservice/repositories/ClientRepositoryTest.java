package backend.clientservice.repositories;

import backend.clientservice.models.entities.Cliente;
import backend.clientservice.models.entities.TipoDocumento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE clientes ALTER COLUMN id RESTART WITH 1");
    }

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
    }

    @Test
    void testListAll() {
        Cliente cliente1 = Cliente.builder()
                .nombre("Victor")
                .apellido("Orbegozo")
                .numeroDocumento("12345678")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        clientRepository.save(cliente1);

        Cliente cliente2 = Cliente.builder()
                .nombre("Maria")
                .apellido("Martinez")
                .numeroDocumento("34567218")
                .tipoDocumento(TipoDocumento.DNI)
                .fechaNacimiento(LocalDate.of(1994, 5, 4))
                .build();

        clientRepository.save(cliente2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cliente> clientes = clientRepository.findAll(pageable);

        assertThat(clientes).isNotNull();
        assertThat(clientes.getTotalElements()).isEqualTo(2);

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

        Cliente clientSaved = clientRepository.save(cliente1);

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
        clientRepository.save(cliente1);

        Optional<Cliente> clientFound = clientRepository.findById(1L);

        assertTrue(clientFound.isPresent());
        assertNotNull(clientFound.get().getId());
        assertEquals("Victor", clientFound.get().getNombre());
    }
}