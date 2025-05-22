package backend.clientservice.services;

import backend.clientservice.models.dtos.ClienteRequestDTO;
import backend.clientservice.models.dtos.ClienteResponseDTO;
import org.springframework.data.domain.Page;

public interface ClientService {
    ClienteResponseDTO add(ClienteRequestDTO cliente);

    Page<ClienteResponseDTO> listAll(Integer page, Integer size, String orderBy);

    ClienteResponseDTO getById(Long id);

    ClienteResponseDTO getByDocumentNumber(String documentNumber, String documentType);

}
