package backend.clientservice.services.impl;

import backend.clientservice.exceptions.ClienteException;
import backend.clientservice.models.dtos.ClienteRequestDTO;
import backend.clientservice.models.dtos.ClienteResponseDTO;
import backend.clientservice.models.entities.Cliente;
import backend.clientservice.models.entities.TipoDocumento;
import backend.clientservice.models.mappers.ClienteMapper;
import backend.clientservice.repositories.ClienteRepository;
import backend.clientservice.services.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper = ClienteMapper.INSTANCE;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public ClienteResponseDTO getByDocumentNumber(String documentNumber, String documentType) {
        validateLetraNumeroDocumento(documentNumber);
        validateNumeroDocumento(documentNumber, documentType);
        validateTipoDocumento(documentType);

        Optional<Cliente> clientFound = clienteRepository.findByNumeroDocumento(documentNumber);
        if (clientFound.isPresent()) {
            return clienteMapper.toResponseDTO(clientFound.get());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = ClienteException.class)
    public ClienteResponseDTO add(ClienteRequestDTO dto) {
        validateNombre(dto.nombre());
        validateApellido(dto.apellido());
        validateTipoDocumento(dto.tipoDocumento());
        validateNumeroDocumento(dto.numeroDocumento(), dto.tipoDocumento());
        validateFechaNacimiento(dto.fechaNacimiento());

        if (clienteRepository.existsByNumeroDocumento(dto.numeroDocumento())) {
            throw new ClienteException(ClienteException.DOCUMENT_NUMBER_EXISTS);
        }

        Cliente cliente = clienteMapper.toEntity(dto);

        Cliente clientSaved = clienteRepository.save(cliente);

        ClienteResponseDTO response = clienteMapper.toResponseDTO(clientSaved);

        return response;
    }

    @Override
    public List<ClienteResponseDTO> listAll() {
        List<Cliente> clientes = clienteRepository.findAll();

        return clienteMapper.toListResponseDTO(clientes);
    }

    @Override
    public ClienteResponseDTO getById(Long id) {
        validateId(id);
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteException(ClienteException.CLIENT_NOT_FOUND));

        return clienteMapper.toResponseDTO(cliente);
    }

    private void validateLetraNumeroDocumento(String numeroDocumento) {
        if (numeroDocumento.matches(".*[a-zA-Z]+.*")) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_NUMBER);
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ClienteException(ClienteException.ID_INVALID);
        }
    }

    private void validateNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteException(ClienteException.INVALID_NAME);
        }
    }

    private void validateApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new ClienteException(ClienteException.INVALID_LAST_NAME);
        }
    }

    private void validateNumeroDocumento(String numeroDocumento, String tipoDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_NUMBER);
        }

        validateLetraNumeroDocumento(numeroDocumento);

        if (Objects.equals(tipoDocumento, TipoDocumento.DNI.name())) {
            validateDni(numeroDocumento);
        }
    }

    private void validateDni(String numeroDocumento) {
        if (numeroDocumento.length() != 8) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_NUMBER);
        }
    }

    private void validateTipoDocumento(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_TYPE);
        }

        if (!tipoDocumento.equals(TipoDocumento.PASAPORTE.name()) && !tipoDocumento.equals(TipoDocumento.DNI.name())) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_TYPE);
        }
    }

    private void validateFechaNacimiento(String fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) {
            throw new ClienteException(ClienteException.INVALID_BIRTH_DATE);
        }

        LocalDate fechaNacimientoLocalDate = convertToLocalDate(fechaNacimiento);
        if (fechaNacimientoLocalDate.isAfter(LocalDate.now())) {
            throw new ClienteException(ClienteException.INVALID_BIRTH_DATE);
        }
    }

    private LocalDate convertToLocalDate(String fechaNacimiento) {
        try {
            return LocalDate.parse(fechaNacimiento);
        } catch (Exception e) {
            throw new ClienteException(ClienteException.INVALID_BIRTH_DATE);
        }
    }

    private String convertString(LocalDate fechaNacimiento) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return fechaNacimiento.format(formatter);
        } catch (Exception e) {
            throw new ClienteException(ClienteException.INVALID_BIRTH_DATE);
        }
    }

}
