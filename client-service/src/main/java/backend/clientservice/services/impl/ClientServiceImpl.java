package backend.clientservice.services.impl;

import backend.clientservice.exceptions.ClienteException;
import backend.dto.request.ClienteRequestDTO;
import backend.dto.response.ClientDtoResponse;
import backend.clientservice.models.entities.Cliente;
import backend.clientservice.models.entities.TipoDocumento;
import backend.clientservice.models.mappers.ClienteMapper;
import backend.clientservice.repositories.ClientRepository;
import backend.clientservice.services.ClientService;
import backend.pageable.PageableUtils;
import backend.pageable.Paginado;
import backend.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClienteMapper clienteMapper = ClienteMapper.INSTANCE;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDtoResponse getByDocumentNumber(String documentNumber, String documentType) {
        validateDni(documentNumber);
        validateNumeroDocumento(documentNumber, documentType);
        validateTipoDocumento(documentType);

        Optional<Cliente> clientFound = clientRepository.findByNumeroDocumento(documentNumber);
        if (clientFound.isPresent()) {
            return clienteMapper.toResponseDTO(clientFound.get());
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = ClienteException.class)
    public ClientDtoResponse add(ClienteRequestDTO dto) {
        validateNombre(dto.nombre());
        validateApellido(dto.apellido());
        validateTipoDocumento(dto.tipoDocumento());
        validateNumeroDocumento(dto.numeroDocumento(), dto.tipoDocumento());
        validateFechaNacimiento(dto.fechaNacimiento());

        if (clientRepository.existsByNumeroDocumento(dto.numeroDocumento())) {
            throw new ClienteException(ClienteException.DOCUMENT_NUMBER_EXISTS);
        }

        Cliente cliente = clienteMapper.toEntity(dto);

        Cliente clientSaved = clientRepository.save(cliente);

        ClientDtoResponse response = clienteMapper.toResponseDTO(clientSaved);

        return response;
    }

    @Override
    public Page<ClientDtoResponse> listAll(Integer page, Integer size, String orderBy) {
        Paginado paginado = new Paginado(page, size, orderBy);
        PageableUtils.validatePagination(paginado);
        Pageable pageable = PageableUtils.constructPageable(paginado);

        Page<Cliente> clientes = clientRepository.findAll(pageable);

        List<ClientDtoResponse> response = clientes.getContent().stream()
                .map(clienteMapper::toResponseDTO).toList();

        return new PageImpl<>(response, pageable, clientes.getTotalElements());
    }

    @Override
    public ClientDtoResponse getById(Long id) {
        validateId(id);
        Cliente cliente = clientRepository.findById(id).orElseThrow(() -> new ClienteException(ClienteException.CLIENT_NOT_FOUND));

        return clienteMapper.toResponseDTO(cliente);
    }

    private void validateId(Long id) {
        if (Utils.isNotPositive(id.intValue())) {
            throw new ClienteException(ClienteException.ID_INVALID);
        }
    }

    private void validateNombre(String nombre) {
        if (Utils.isBlank(nombre)) {
            throw new ClienteException(ClienteException.INVALID_NAME);
        }
    }

    private void validateApellido(String apellido) {
        if (Utils.isBlank(apellido)) {
            throw new ClienteException(ClienteException.INVALID_LAST_NAME);
        }
    }

    private void validateNumeroDocumento(String numeroDocumento, String tipoDocumento) {
        if (Utils.isNotValidDni(numeroDocumento)) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_NUMBER);
        }

        if (Objects.equals(tipoDocumento, TipoDocumento.DNI.name())) {
            validateDni(numeroDocumento);
        }
    }

    private void validateDni(String numeroDocumento) {
        if (Utils.isNotValidDni(numeroDocumento)) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_NUMBER);
        }
    }

    private void validateTipoDocumento(String tipoDocumento) {
        if (Utils.isBlank(tipoDocumento)) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_TYPE);
        }

        if (!tipoDocumento.equals(TipoDocumento.PASAPORTE.name()) && !tipoDocumento.equals(TipoDocumento.DNI.name())) {
            throw new ClienteException(ClienteException.INVALID_DOCUMENT_TYPE);
        }
    }

    private void validateFechaNacimiento(String fechaNacimiento) {
        if (Utils.isBlank(fechaNacimiento)) {
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
}
