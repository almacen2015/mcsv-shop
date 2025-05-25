package backend.clientservice.services;

import backend.dto.request.ClientDtoRequest;
import backend.dto.response.ClientDtoResponse;
import org.springframework.data.domain.Page;

public interface ClientService {
    ClientDtoResponse add(ClientDtoRequest cliente);

    Page<ClientDtoResponse> listAll(Integer page, Integer size, String orderBy);

    ClientDtoResponse getById(Long id);

    ClientDtoResponse getByDocumentNumber(String documentNumber, String documentType);

}
