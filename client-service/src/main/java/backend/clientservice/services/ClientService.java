package backend.clientservice.services;

import backend.dto.request.ClienteRequestDTO;
import backend.dto.response.ClientDtoResponse;
import org.springframework.data.domain.Page;

public interface ClientService {
    ClientDtoResponse add(ClienteRequestDTO cliente);

    Page<ClientDtoResponse> listAll(Integer page, Integer size, String orderBy);

    ClientDtoResponse getById(Long id);

    ClientDtoResponse getByDocumentNumber(String documentNumber, String documentType);

}
